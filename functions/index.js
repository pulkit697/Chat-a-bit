const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref("/messages/{chatId}/{msgId}")
    .onCreate((snapshot, context) =>{
      const chatIdL = context.params.chatId;
      const messageFrom = snapshot.val().senderId;
      const userId = chatIdL.replace(messageFrom, "");
      const status = admin.firestore().collections("users")
          .doc(userId).get().then((doc) => {
            const token = doc.data().deviceToken;
            console.log(token);
            return true;
          });
      return status;
    });
