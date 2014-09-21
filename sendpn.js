gcm = require('node-gcm');	
var gcmsender = new gcm.Sender('AIzaSyDJu-OKmyz-MOHZSKIURSGSg_gFrsRpAeU');
//var duid = "APA91bGZi40M_iQUzPIAHRrRsfmyREAsCs_Yfg5GCiIuBxz_iDwJzdal2Y0qe9Dh8u0aUNltGNjlSeA4j6Vfqyc6qivN2YWBm3qRnxbj41GJCAFNx3-cafnMdWNrbc8MaAxJQroZH2Pj1F2aQwYd9IlulM7Es5HdCQ";
//1 -- You Ran Over <param>
//2 -- <param> Ran You Over
//3 -- You Ran Over <param> People
//4 -- <param> People Ran Over You

var snuids = [
'1623842314',
'524740442',
'592494638',
'1283286538'
];
var device_tokens = [
'APA91bESNIuXyMF75t8yZVztuv4pKsHuPsOXYY5b9lFTYcGCHtxY0gbgpdfAW7W_by1l10bMQ9NYMXe1dGwICapy2yGTJPBhTzwqdEhx4qvtYcd4kk9GH1Z6DoOqtDGLcOiAO43HlE6ho9p_8tp_OiUle2INQ-tXCg',
'APA91bE7ZuOwRKQ2SX3zxe8ZHc7w0GeNNnbcvC4iz7x_4nP8ENFrsVi_cfkG0h-8LbMQIUsOhJNYc3M0zMwtlOZAdQsBCf2eOOAJW-9cG9x6bbk20SiO9Xwz69hh9nBjEFjxLrvxCWjkmw7VXT5XBRK6at8S7m4Fpg',
'',
'APA91bFMNerQz_eJBc6k36HgB8dot39nfchGwuRdKQcUKFGndvl5BVepjJr40ZLB69Fc2iIBeWNwQtOHjyGNqWHh6OnwllPiuK33zQhyGshCbAt3P3GlCcWv1YaiAhS9T33113rwMHQ5md6iDwTqXTtxxmlbkYEcQA'
];

// switch(type) {
//     case 1: {
//         msg.addData('type', 1); 
//         msg.addData('traveller_snuid', "100004176944370");
//         msg.addData('other_snuid', "524740442");
//     } break;
//     case 2: {
//         msg.addData('type', 2); 
//         msg.addData('traveller_snuid', "524740442");
//         msg.addData('other_snuid', "100004176944370");
//     } break;
//     case 3: {
//         msg.addData('type', 3); 
//         msg.addData('num_people', "11");
//     } break;
//     case 4: {
//         msg.addData('type', 4); 
//         msg.addData('num_people', "13");
//     } break;    
// }


var p1 = process.argv[2];
var p2 = process.argv[3];

if(p2 == "beat") {
    var p3 = process.argv[4];
    sendPn1(device_tokens[p1-1], snuids[p1-1], snuids[p3-1]);
    sendPn2(device_tokens[p3-1], snuids[p3-1], snuids[p1-1]);
} else {
    sendPn3(device_tokens[p1-1], p2);
}


function sendPn1(d1, s1, s2) {
    var msg = new gcm.Message();
    msg.collapseKey = '2';
    msg.delayWhileIdle = true;
    msg.timeToLive = parseInt(1000);
    msg.timeToLive = 0;
    msg.addData('type', 1); 
    msg.addData('ts',Date.now());
    msg.addData('traveller_snuid', s1);
    msg.addData('other_snuid', s2);
    gcmsender.send(msg, [d1], 2, function (err, result) {
        if(err) {
            //TODO: Sample this?
            console.log("Gitesh sent pn fail", err);
        } else {
            console.log("Gitesh sent pn");
        }
    });
}

function sendPn2(d2, s1, s2) {
    var msg = new gcm.Message();
    msg.collapseKey = '2';
    msg.delayWhileIdle = true;
    msg.timeToLive = parseInt(1000);
    msg.timeToLive = 0;
    msg.addData('type', 2);
    msg.addData('ts',Date.now());
    msg.addData('traveller_snuid', s2);
    msg.addData('other_snuid', s1);
    gcmsender.send(msg, [d2], 2, function (err, result) {
        if(err) {
            //TODO: Sample this?
            console.log("Gitesh sent pn fail", err);
        } else {
            console.log("Gitesh sent pn");
        }
    });
}

function sendPn3(d1, n1) {
    var msg = new gcm.Message();
    msg.collapseKey = '2';
    msg.delayWhileIdle = true;
    msg.timeToLive = parseInt(1000);
    msg.timeToLive = 0;
    msg.addData('type', 3); 
    msg.addData('ts',Date.now());
    msg.addData('num_people', n1);
    gcmsender.send(msg, [d1], 2, function (err, result) {
        if(err) {
            //TODO: Sample this?
            console.log("Gitesh sent pn fail", err);
        } else {
            console.log("Gitesh sent pn");
        }
    });
}





