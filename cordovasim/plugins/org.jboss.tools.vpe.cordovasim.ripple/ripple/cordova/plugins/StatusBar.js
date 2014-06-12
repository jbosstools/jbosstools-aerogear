var emulatorBridge = ripple('emulatorBridge');
function logStatusBarError(functionName) {
    emulatorBridge.window().console.error("Unfortunately StatusBar." + functionName + " is not supported");
}
module.exports = {

    _ready: function (success, error, args) {
      	logStatusBarError("_ready");
    },

    overlaysWebView: function (success, error, args) {
      	logStatusBarError("overlaysWebView");
    },

    styleDefault: function (success, error, args) {
      	logStatusBarError("styleDefault");
    },

    styleLightContent: function (success, error, args) {
      	logStatusBarError("styleLightContent");
    },

    styleBlackTranslucent: function (success, error, args) {
      	logStatusBarError("styleBlackTranslucent");
    },

    styleBlackOpaque: function (success, error, args) {
      	logStatusBarError("styleBlackOpaque");
    },

    backgroundColorByName: function (success, error, args) {
      	logStatusBarError("backgroundColorByName");
    },

    backgroundColorByHexString: function (success, error, args) {
      	logStatusBarError("backgroundColorByHexString");
    },

    hide: function (success, error, args) {
      	logStatusBarError("hide");
    },

    show: function (success, error, args) {
      	logStatusBarError("show");
    }
};