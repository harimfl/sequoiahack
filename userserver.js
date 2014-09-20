redis = require('redis');
_ = require('underscore');
_dummydata = require('./dummydata.json');

var redis_client;

var sn_list = ""
var stateServer_port = 4000;

var app = require('./app').init(stateServer_port, handleShutdown);

app.get('/user/', function(req, res) {
    res.send('user', {"returnCode": 1, "errorMsg": "boohoo!"});
});

mongoose = require('mongoose');
Schema = mongoose.Schema;

function createRedisConnection() {
	redis_client = redis.createClient(6379, "127.0.0.1");
	redis_client.on("error", function (err) {
		console.log("Error " + err);
	});
	redis_client.on("ready", function (err) {
		console.log("Redis Instance connected on port : 6379");
	});
}

function createMongoConnection() {
	db = mongoose.connect('mongodb://'+'127.0.0.1:27017'+'/'+'olawars');
}
createMongoConnection();

var userSchema = new mongoose.Schema({
    id: {type: String, required: true},
    snid: {type: Number, default: 0},
    snuid: {type: String, default: ""},
    rides: {type: Array, default: []},
    sn_friend_list: {type: Array, default: []},  //format pid_snuid_name
    city: {type: String, default: ""},
    meta: {
        total_miles: {type: Number, default: 0},
        total_time_spent_ms: {type: Number, default: 0},
        total_paid: {type: Number, default: 0},
        max_used_vehicle_type: {type: String, default: "0"},
        last_used_vehicle_type: {type: String, default: "0"}
    }
}, {autoIndex: false});


userSchema.statics._create = function(id, name) {
	var user = new User;
	user.id = id;
	user.save();
}

userSchema.statics.updateScore = function(id, score) {
	User.update({'id': id},{$inc: {'meta.total_miles': score }}, { multi: false });      
}

userSchema.statics.getCity = function(id, callback) {
	User.findOne({'id': pid }, function(err, user) {
		if(!user || err) {
			console.log("No such user", id, err);
			callback(false);
		}
		callback(user.city);
	});
}

userSchema.statics.getUserFromDb = function(id, callback) {
	User.findOne({'id': pid }, function(err, user) {
		if(!user || err) {
			console.log("No such user", id, err);
			callback(false);
		}
		callback(user);
	});
}

var User = mongoose.model('User', userSchema);

User._create("testing");

function updateGlobalLeaderBoard(pid, miles) {
	redis_client.zadd('leaderboard:global', miles, pid);
};

function updateLeaderBoard(leaderboard ,pid, miles) {
	redis_client.zadd(leaderboard, miles, pid);
};


function handleShutdown() {
	console.log("shutting down!");
}


function getFacebookFriends(snuid, fb_access_token, cb) {
    var options = {
        host: 'graph.facebook.com',
        port: 443,
        path: '/me/friends?access_token='+fb_access_token,
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    };

request("https://graph.facebook.com/me/friends?access_token="+fb_access_token, function(err, res, body) {
        if(!err) {
            var obj = JSON.parse(body);
            if(obj && obj.data) {
                cb(obj.data);   
            } else {
                winston.warn("getFacebookFriends: friendlist improper json object ", {"meta": body, "snuid": snuid});
                cb(false);
            }
        } else {
            winston.warn("getFacebookFriends: friendlist improper json object ", {"snuid": snuid});
            cb(false);
        }
    });
}

function getUser(req, res) {
    req.params=_.extend(req.params || {}, req.query || {}, req.body || {});

    //req.assert('duid', 'Device identifier not present').notEmpty();
    req.assert('pid', 'User Id invalid').notEmpty().isInt();
    // req.assert('access_token', 'Access Token is invalid').notEmpty();
    // req.assert('snid', 'Social network id is invalid').notEmpty().isInt();
    // // req.assert('snuid', 'Social network uid is invalid').notEmpty().isInt();
    // req.assert('h', 'No checksum present').notEmpty();
    // req.assert('device_type', 'No device type present').notEmpty().isInt();
    // req.assert('device_token', 'No device token param present');
    // //req.assert('dau_source', 'No dau_source parameter').notEmpty();
    // var errors = req.validationErrors();
    User.getUserFromDb(req.params.pid, function(user) {
	    var retData = {};
	    retData.snuid = user.snuid;
	    retData.sn_name = user.sn_name;
	    retData.total_miles = user.meta.total_miles;
	    retData.city = user.city;
		res.send(JSON.stringify(retData));
    });
}


function createNewUser(req, res) {
    req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
    req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User._create(pid);
    res.send(200);
}

function updateScore(req, res) {
	req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User.updateScore(pid, score);
    updateGlobalLeaderBoard(pid, score);
    User.getCity(pid, function(city) {
    	if(city && city != "") {
    		updateLeaderBoard("leaderboard:" + city ,pid, score);
    	}
    });
    res.send(200);
}

function getlocallb(req, res) {
	req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User.getCity(pid, function(city) {
    	if(city && city != "") {
    		
    	}
    });
    User.updateScore(pid, score);
}

function getfriendlb(req, res) {
	// req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	// req.assert('pid', 'User Id invalid').notEmpty();
 //    var pid = req.params.pid;
 //    User.getUserFromDb(pid, function(user) {
 //    	if(user) {
 //    		getFriendsChips(user.sn_friend_list, function(list) {
 //    			var sortable = [];
 //    			for (var i =0; i<list.length;i++) {
	// 			   	sortable.push([list['id'], list['olamiles'], list['snuid'], list['name']]);
	// 			}
	// 			sortable.sort(function(a, b) {return b[1] - a[1]});	

	// 			var retObj = {};

	// 			retObj['top'] = [];

	// 			for (var i =0; i<3;i++) {
	// 				var temp = {};
	// 				temp['name'] = sortable[i][3];
	// 				temp['snuid'] = sortable[i][2];
	// 				temp['olamiles'] = sortable[i][1];
	// 				temp['rank'] = i+1;
	// 				retObj['top'].push(temp);
	// 			}

	// 			for (var i =0; i<list.length;i++) {
	// 				temp['name'] = sortable[i][3];
	// 				temp['snuid'] = sortable[i][2];
	// 				temp['olamiles'] = sortable[i][1];
	// 				temp['rank'] = i+1;
	// 				retObj['top'].push();
	// 			}


 //   			});
 //    	}
 //    });
    res.send(JSON.stringify(_dummydata));
}

getLeaderboardWinners = function(leaderboard, callback) {
    var chain = redis_client.multi();
    
    chain.zrevrange(leaderboard, 0, 2, 'withscores')
    chain.exec(function(err, result) {
        if(err || !result) {
            winston.info("isTournamentFinished : cant find the users in redis!");
            callback(false);
            return;
        }
        callback(result[0]);
    });
}


getFriendsChips = function(friend_ids, callback) {
	var returnList = [];
	var chain = redis_client.multi();
	var index = [];
	var count = 0;
	friend_ids.forEach(function(item) {
		chain.zscore('leaderboards:global', item.split('_')[0]);
		index[count++] = item;
	});

	chain.exec(function(err, replies) {
		if (!err && replies) {
			replies.forEach(function(data, i) {
				returnList.push({"id": index[i].split('_')[0], "olamiles": data, "snuid":index[i].split('_')[1], "name":index[i].split('_')[2]});
			});
			callback(returnList);
		} else {
			callback(returnList);
		}
	});
};

app.post('/user/get', getUser);
app.get('/user/get', getUser);

app.post('/user/new', createNewUser);
app.get('/user/new', createNewUser);

app.get('/user/updatescore', updateScore);
app.post('/user/updatescore', updateScore);

app.get('/user/getlocallb', getlocallb);
app.post('/user/getlocallb', getlocallb);

app.get('/user/getfriendlb', getfriendlb);
app.post('/user/getfriendlb', getfriendlb);
