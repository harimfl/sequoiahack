	gcm = require('node-gcm');	
	//925617659837
	////AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU
	var gcmsender = new gcm.Sender('AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU');
	var duid = "APA91bGdeLvW6-BMnbDaQkhIW9yb0q6Iycf3VBvX4-Ng9R7Z2l2S7VHS0C30U8lR6D-nbRC2bxUIJLJKqwSpW-gzxMJEuDAw7kVE7T37_1ZxEJKBpWj0ZZ09Bu3N7H0d_rRfW_iCYH6M3GVz_zBevzND-uqetSaOjA";
	
var msg = new gcm.Message();
                    
                        msg.collapseKey = '2';

                        msg.delayWhileIdle = true;

	        		    msg.timeToLive = parseInt(1000);   
                    
	        		msg.addData('text', "Tap to Join Gitesh");

	        		msg.addData('title', "Gitesh ran over you ");
                    msg.addData('type', 2);	        		
                    msg.addData('typenew', 4);
	        		msg.addData('param1', "10");
	        		msg.addData('param2', "1123");
	        		msg.addData('param3', "1123");
                    msg.addData('param4', "1123");
                    msg.addData('param5', "1123");
                    msg.addData('param6', "1283286538");
                    msg.addData('ts',Date.now());
  
	        	
                    




// Statics.sendPushNotif(4, 38263915, "12", "Teen Patti Gold", 
//                                             "1", text, "12", 1283286538, ['13','26','39'], 1, 0);
// Statics.sendPushNotif = function(type, pid, param1, param2, param3, text, senderId, senderSnuid, cards, snid, usertimer) {

	gcmsender.send(msg, duid, 2, function (err, result) {
              if(err) {
			//TODO: Sample this?
			console.log("Gitesh sent pn fail");
			//StatsController.count.apply(StatsController, [pid, 'push_notif_send', 1, 'fail', notifType, 'android', senderId, err]);
		} else {
			console.log("Gitesh sent pn");
			//TODO: Sample this?
			//StatsController.count.apply(StatsController, [pid, 'push_notif_send', 1, 'success', notifType, 'android', senderId]);
		}
	});