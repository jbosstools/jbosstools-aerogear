var notifications = ripple('notifications');
var message = "AeroGear Push notifications can not be simulated, please use real hardware";
module.exports = {
    register: function (success, error, args) {
      notifications.openNotification("normal", message);
    },

    unregister: function (success, error, args) {
       notifications.openNotification("normal", message);
    },

    setApplicationIconBadgeNumber: function (success, error, args) {
       notifications.openNotification("normal", message);
    } 
};