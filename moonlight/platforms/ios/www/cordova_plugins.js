cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.apache.cordova.console/www/console-via-logger.js",
        "id": "org.apache.cordova.console.console",
        "clobbers": [
            "console"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.console/www/logger.js",
        "id": "org.apache.cordova.console.logger",
        "clobbers": [
            "cordova.logger"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.device/www/device.js",
        "id": "org.apache.cordova.device.device",
        "clobbers": [
            "device"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.splashscreen/www/splashscreen.js",
        "id": "org.apache.cordova.splashscreen.SplashScreen",
        "clobbers": [
            "navigator.splashscreen"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.dialogs/www/notification.js",
        "id": "org.apache.cordova.dialogs.notification",
        "merges": [
            "navigator.notification"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/Coordinates.js",
        "id": "org.apache.cordova.geolocation.Coordinates",
        "clobbers": [
            "Coordinates"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/PositionError.js",
        "id": "org.apache.cordova.geolocation.PositionError",
        "clobbers": [
            "PositionError"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/Position.js",
        "id": "org.apache.cordova.geolocation.Position",
        "clobbers": [
            "Position"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.geolocation/www/geolocation.js",
        "id": "org.apache.cordova.geolocation.geolocation",
        "clobbers": [
            "navigator.geolocation"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "org.apache.cordova.console": "0.2.12",
    "org.apache.cordova.device": "0.2.13",
    "org.apache.cordova.splashscreen": "0.3.5",
    "org.apache.cordova.dialogs": "0.2.11",
    "org.apache.cordova.geolocation": "0.3.11"
}
// BOTTOM OF METADATA
});