cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-plugin-network-information.network",
      "file": "plugins/cordova-plugin-network-information/www/network.js",
      "pluginId": "cordova-plugin-network-information",
      "clobbers": [
        "navigator.connection",
        "navigator.network.connection"
      ]
    },
    {
      "id": "cordova-plugin-network-information.Connection",
      "file": "plugins/cordova-plugin-network-information/www/Connection.js",
      "pluginId": "cordova-plugin-network-information",
      "clobbers": [
        "Connection"
      ]
    },
    {
      "id": "com.at.token.token",
      "file": "plugins/com.at.token/www/token.js",
      "pluginId": "com.at.token",
      "clobbers": [
        "token"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-whitelist": "1.3.3",
    "cordova-plugin-network-information": "2.0.1",
    "com.at.token": "0.1.0"
  };
});