	gcm = require('node-gcm');	
	//925617659837
	////AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU
	var gcmsender = new gcm.Sender('AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU');
    var duid = "APA91bGZi40M_iQUzPIAHRrRsfmyREAsCs_Yfg5GCiIuBxz_iDwJzdal2Y0qe9Dh8u0aUNltGNjlSeA4j6Vfqyc6qivN2YWBm3qRnxbj41GJCAFNx3-cafnMdWNrbc8MaAxJQroZH2Pj1F2aQwYd9IlulM7Es5HdCQ";
	
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
			console.log("Gitesh sent pn fail", err);
			//StatsController.count.apply(StatsController, [pid, 'push_notif_send', 1, 'fail', notifType, 'android', senderId, err]);
		} else {
			console.log("Gitesh sent pn");
			//TODO: Sample this?
			//StatsController.count.apply(StatsController, [pid, 'push_notif_send', 1, 'success', notifType, 'android', senderId]);
		}
	});