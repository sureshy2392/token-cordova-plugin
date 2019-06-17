cordova.define("com.at.tokencordova.TokenCordovaWrapper", function(require, exports, module) {
// var exec = require('cordova/exec');

module.exports= {
	coolMethod : function (arg0, success, error) {
    cordova.exec(success, error, 'TokenCordovaWrapper', 'coolMethod', [arg0]);
}
};

module.exports = {
    createMemberToken: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "TokenCordovaWrapper", "createMemberToken");
    }
};

});
