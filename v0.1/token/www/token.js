var exec = require('cordova/exec');

exports.createMember = function (arg0, success, error) {
    exec(success, error, 'token', 'createMember', [arg0]);
};
