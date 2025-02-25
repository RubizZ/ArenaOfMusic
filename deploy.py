#!/usr/bin/env python
"""Despliega tu aplicación web en tu contenedor Docker de la FDI"""

import sys
import subprocess
import glob
import json
import sshtunnel  # Instala via pip install sshtunnel
import fabric     # Instala via pip install fabric

print("Checking for database files...")
dbfiles = glob.glob("iwdb.*")
if len(dbfiles) > 0:
    print(f"Found {len(dbfiles)} database files: {dbfiles}")
else:
    print("No database files found, exiting.")
    sys.exit(1)

print("Checking for data files...")
datafiles = glob.glob("iwdata", recursive=True)
if len(datafiles) > 0:
    print(f"Found {len(datafiles)} data files, compressing...")
    subprocess.run(["zip", "-r", "iwdata.zip", "iwdata"], shell=True, check=True)
else:
    print("No data files found.")
    sys.exit(1)

print("Building deployment jar file, using container profile...")
try:
    subprocess.run(["mvn", "package", "-Dspring.profiles.active=container", "-DskipTests=true"], shell=True, check=True)
    jar_files = glob.glob("target/*.jar")
    if not jar_files:
        print("Error: No JAR file found in target/. Exiting.")
        sys.exit(1)
    jar = jar_files[0]
    print(f"Deployment jar file is ready: {jar}")
except subprocess.CalledProcessError:
    print("Error: Could not build jar file. Exiting.")
    sys.exit(1)

print("Loading credentials from `credentials.json` ...")
try:
    credentials = json.load(open('credentials.json'))
except Exception as e:
    print(f"Error: Could not load credentials file. {e}")
    sys.exit(1)

print("Connecting to jumphost ...")
with sshtunnel.open_tunnel(
    (credentials['jumphost'], 22),
    ssh_username=credentials['jumphost_user'],
    ssh_password=credentials['jumphost_pass'],
    remote_bind_address=(credentials['target'], 22),
    local_bind_address=('0.0.0.0', 2222)
) as tunnel:
    print(f"Tunnel to {credentials['jumphost']} established, binding via localhost:2222 ...")
    
    with fabric.Connection(
        host='127.0.0.1',
        user=credentials['target_user'],
        port=2222,
        connect_kwargs={"password": credentials['target_pass']}
    ) as c:
        print(f"Connected to target host {credentials['target']} as {credentials['target_user']}")

        print("Uploading database files...")
        for f in dbfiles:
            c.put(f)

        print("Uploading data files...")
        c.put("iwdata.zip")
        c.run("unzip -o iwdata.zip && rm iwdata.zip")

        print("Uploading jar file...")
        c.put(jar)

        print("Checking for existing tmux session...")
        result = c.run("tmux has-session -t myapp", warn=True)
        if result.ok:
            print("Existing 'myapp' tmux session found. Stopping it...")
            c.run("tmux kill-session -t myapp", warn=True)

        print("Starting application inside tmux...")
        c.run(f"tmux new-session -d -s myapp 'SPRING_PROFILES_ACTIVE=container java -jar {jar}'")
        print("Application deployed successfully!")
