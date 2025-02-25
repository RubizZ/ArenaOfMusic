function fn() {
    var env = karate.env; // get system property 'karate.env'
    karate.log('karate.env system property was:', env);

    if (!env) {
        env = 'dev';
    }

    /**
     * Variables here are available in all tests
     */
    var config = {
        env: env,
        myVarName: 'someValue',
        baseUrl: 'http://localhost:8080'
    }

    var osName = Java.type('java.lang.System').getProperty('os.name').toLowerCase();

    /**
     * Drivers for tests - currently configured value is good for Linux
     */
    karate.configure('driver', {
        type: 'chrome',
        executable: osName.includes('win') ? 
            "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" : 
            '/usr/bin/google-chrome', // Path for Ubuntu/Linux (default location for Chrome)
        addOptions: ["--remote-allow-origins=*",
        "--incognito",
        "--no-sandbox",
        "--disable-dev-shm-usage",
        "--headless",
        "--disable-extensions", // Disable extensions
        "--disable-gpu",         // Disable GPU (useful for CI)
        "--remote-debugging-port=9222"
        ],
        showDriverLog: true
    })

    if (env == 'dev') {
        // customize
        // e.g. config.foo = 'bar';
    } else if (env == 'e2e') {
        // customize
    }
    return config;
}
