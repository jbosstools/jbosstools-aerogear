var emulatorBridge = ripple('emulatorBridge');
function logStatusBarError(functionName) {
    emulatorBridge.window().console.error("Unfortunately Keyboard." + functionName + " is not supported");
}
module.exports = {

    hideKeyboardAccessoryBar: function (success, error, args) {
      	logStatusBarError("hideKeyboardAccessoryBar");
    },

    disableScroll: function (success, error, args) {
      	logStatusBarError("disableScroll");
    },

    styleDark: function (success, error, args) {
        logStatusBarError("styleDark");
    },

    close: function (success, error, args) {
      	logStatusBarError("close");
    }
};