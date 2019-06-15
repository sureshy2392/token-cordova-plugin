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

exports.getAccounts = function (arg0, success, error) {
    exec(success, error, 'token', 'getAccounts', [arg0]);
};

exports.getAccount = function (arg0, success, error) {
    exec(success, error, 'token', 'getAccount', [arg0]);
};

exports.getConsents = function (arg0, success, error) {
    exec(success, error, 'token', 'getConsents', [arg0]);
};

exports.getTransfers = function (arg0, success, error) {
    exec(success, error, 'token', 'getTransfers', [arg0]);
};

exports.getProfile = function (arg0, success, error) {
    exec(success, error, 'token', 'getProfile', [arg0]);
};

exports.getProfilePicture = function (arg0, success, error) {
    exec(success, error, 'token', 'getProfilePicture', [arg0]);
};

exports.unlinkAccounts = function (arg0, success, error) {
    exec(success, error, 'token', 'unlinkAccounts', [arg0]);
};

exports.deleteMember = function (arg0, success, error) {
    exec(success, error, 'token', 'deleteMember', [arg0]);
};
