var exec = require('cordova/exec');

// module.exports= {
// 	coolMethod : function (arg0, success, error) {
//     cordova.exec(success, error, 'TokenCordovaWrapper', 'coolMethod', [arg0]);
// }
// };

// module.exports = {
//     createMemberToken: function (successCallback, errorCallback) {
//         cordova.exec(successCallback, errorCallback, "TokenCordovaWrapper", "createMemberToken");
//     }
// };


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

exports.approveAccessToken = function (arg0, success, error) {
    exec(success, error, 'token', 'approveAccessToken', [arg0]);
};

exports.cancelAccessToken = function (arg0, success, error) {
    exec(success, error, 'token', 'cancelAccessToken', [arg0]);
};

exports.approveTransferToken = function (arg0, success, error) {
    exec(success, error, 'token', 'approveTransferToken', [arg0]);
};

exports.memberRecovery = function (arg0, success, error) {
    exec(success, error, 'token', 'memberRecovery', [arg0]);
};

exports.getRecoveredMember = function (arg0, success, error) {
    exec(success, error, 'token', 'getRecoveredMember', [arg0]);
};

exports.provisionRequest = function (arg0, success, error) {
    exec(success, error, 'token', 'provisionRequest', [arg0]);
};

exports.provisionResponse = function (arg0, success, error) {
    exec(success, error, 'token', 'provisionResponse', [arg0]);
};

exports.resolveAlias = function (arg0, success, error) {
    exec(success, error, 'token', 'resolveAlias', [arg0]);
};

exports.getMember = function (arg0, success, error) {
    exec(success, error, 'token', 'getMember', [arg0]);
};

exports.onAccountRevoke = function (arg0, success, error) {
    exec(success, error, 'token', 'onAccountRevoke', [arg0]);
};

