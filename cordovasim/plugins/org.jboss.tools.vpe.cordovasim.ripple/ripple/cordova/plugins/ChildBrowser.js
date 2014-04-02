var inAppBrowser = ripple('platform/cordova/3.0.0/bridge/inappbrowser');                 

module.exports = {
    showWebPage: function (success, error, args) {
        inAppBrowser.open(success, error, args);
    },

    openExternal: function (success, error, args) {
        inAppBrowser.open(success, error, args);
    },

    close: function (success, error, args) {
        inAppBrowser.close(success, error, args);
    }
};