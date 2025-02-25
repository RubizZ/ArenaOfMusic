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

    // Handle driver configuration based on OS platform (Linux or Windows)
    var os = require('os');
    var platform = os.platform(); // 'win32' for Windows, 'linux' for Ubuntu, etc.

    /**
     * Drivers for tests - currently configured value is good for Linux
     */
    karate.configure('driver', {
        type: 'chrome',
        executable: platform === 'win32' ? 
            "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" : 
            '/usr/bin/google-chrome', // Path for Ubuntu/Linux (default location for Chrome)
        addOptions: ["--remote-allow-origins=*", "--incognito"],
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
