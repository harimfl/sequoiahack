gcm = require('node-gcm');	
var gcmsender = new gcm.Sender('AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU');
var duid = "APA91bGZi40M_iQUzPIAHRrRsfmyREAsCs_Yfg5GCiIuBxz_iDwJzdal2Y0qe9Dh8u0aUNltGNjlSeA4j6Vfqyc6qivN2YWBm3qRnxbj41GJCAFNx3-cafnMdWNrbc8MaAxJQroZH2Pj1F2aQwYd9IlulM7Es5HdCQ";
var type = 1;	
var msg = new gcm.Message();
msg.collapseKey = '2';
msg.delayWhileIdle = true;
msg.timeToLive = parseInt(1000);
//1 -- You Ran Over <param>
//2 -- <param> Ran You Over
//3 -- You Ran Over <param> People
//4 -- <param> People Ran Over You
switch(type) {
    case 1: {
        msg.addData('type', 1); 
        msg.addData('other_name', "Rads");
        msg.addData('traveller_snuid', "100004176944370");
        msg.addData('other_snuid', "524740442");
    } break;
    case 2: {
        msg.addData('type', 2); 
        msg.addData('traveller_name', "Srinath");
        msg.addData('traveller_snuid', "524740442");
        msg.addData('other_snuid', "100004176944370");
    } break;
    case 3: {
        msg.addData('type', 3); 
        msg.addData('num_people', "11");
    } break;
    case 4: {
        msg.addData('type', 4); 
        msg.addData('num_people', "13");
    } break;    
}
msg.addData('ts',Date.now());

gcmsender.send(msg, [duid], 2, function (err, result) {
    if(err) {
		//TODO: Sample this?
		console.log("Gitesh sent pn fail", err);
	} else {
		console.log("Gitesh sent pn");
	}
});
