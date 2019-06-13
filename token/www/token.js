var exec = require('cordova/exec');

exports.createMember = function (arg0, success, error) {
    exec(success, error, 'token', 'createMember', [arg0]);
};

exports.subscribe = function (arg0, success, error) {
    exec(success, error, 'token', 'subscribe', [arg0]);
};

exports.linkAccounts = function (arg0, success, error) {
    exec(success, error, 'token', 'linkAccounts', [arg0]);
};
