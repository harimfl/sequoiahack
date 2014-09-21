redis = require('redis');
_ = require('underscore');
_dummydata = require('./dummydata.json');

var redis_client;
const MIN_TIME_BEFORE_SN_FRIENDS_REFRESH_IN_SECONDS = 100;
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
    sn_friend_list_last_updated_at: { type: Date},
    sn_friend_list: {type: Array, default: []},  //format pid_snuid_name
    city: {type: String, default: "bangalore"},
    meta: {
        total_miles: {type: Number, default: 0},
        total_time_spent_ms: {type: Number, default: 0},
        total_paid: {type: Number, default: 0},
        max_used_vehicle_type: {type: String, default: "0"},
        last_used_vehicle_type: {type: String, default: "0"}
    }
}, {autoIndex: false});


userSchema.statics._create = function(id, cb) {
	var user = new User;
	user.id = id;
	user.save();
	cb(user);
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

userSchema.statics.getUserFromSnuid = function(snuid, callback) {
	User.findOne({'snuid': snuid }, function(err, user) {
		if(!user || err) {
			console.log("No such user", snuid, err);
			callback(false);
		}
		callback(user);
	});
}

var User = mongoose.model('User', userSchema);


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
    var snuid = req.params.snuid;
    var access_token = req.params.access_token;
    
    if(snuid === undefined) snuid = "";
    User.getUserFromDb(req.params.pid, function(user) {
    	user.snuid = snuid;
    	
    	//do this async
    	if(snuid !== "") {
    		user.access_token = access_token;
    		var currTime = Date.now();
	        if(!user.sn_friend_list_last_updated_at ||
	        ((currTime - user.sn_friend_list_last_updated_at)/1000 > MIN_TIME_BEFORE_SN_FRIENDS_REFRESH_IN_SECONDS)) {
		    	getFacebookFriends(snuid, access_token, function(result){
		    		var progress = result.length;
		    		result.forEach(function(item) {
			    		User.getUserFromSnuid(item.id,function(friend) {
			    			if(friend) {
			    				if(friend.sn_friend_list.indexOf(user.id + '_' + user.snuid + '_' + user.name) < 0 ) {
			    					friend.sn_friend_list.push(user.id + '_' + user.snuid + '_' + user.name);
			    				}
			    				if(user.sn_friend_list.indexOf(friend.id + '_' + friend.snuid + '_' + friend.name) < 0 ) {
			    					user.sn_friend_list.push(friend.id + '_' + friend.snuid + '_' + friend.name);
			    				}
			    				friend.save();
			    			}
			    			if(--progress == 0 ){
			    				user.sn_friend_list_last_updated_at = currTime;
			    				user.save();
			    			}
			    		});
			    	});

		    	});
    		}
    	}
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
    var snuid = req.params.snuid;
    var access_token = req.params.access_token;
    User._create(pid, function(user){
    	user.snuid = snuid;
    	
    	//do this async
    	if(snuid === undefined) snuid = "";
    	if(snuid !== "") {
    		user.access_token = access_token;
    		var currTime = Date.now();
	        if(!user.sn_friend_list_last_updated_at ||
	        ((currTime - user.sn_friend_list_last_updated_at)/1000 > MIN_TIME_BEFORE_SN_FRIENDS_REFRESH_IN_SECONDS)) {
		    	getFacebookFriends(snuid, access_token, function(result){
		    		var progress = result.length;
		    		result.forEach(function(item) {
			    		User.getUserFromSnuid(item.id,function(friend) {
			    			if(friend) {
			    				if(friend.sn_friend_list.indexOf(user.id + '_' + user.snuid + '_' + user.name) < 0 ) {
			    					friend.sn_friend_list.push(user.id + '_' + user.snuid + '_' + user.name);
			    				}
			    				if(user.sn_friend_list.indexOf(friend.id + '_' + friend.snuid + '_' + friend.name) < 0 ) {
			    					user.sn_friend_list.push(friend.id + '_' + friend.snuid + '_' + friend.name);
			    				}
			    				friend.save();
			    			}
			    			if(--progress == 0 ){
			    				user.sn_friend_list_last_updated_at = currTime;
			    				user.save();
			    			}
			    		});
			    	});

		    	});
    		}
    	}
	    var retData = {};
	    retData.snuid = user.snuid;
	    retData.sn_name = user.sn_name;
	    retData.total_miles = user.meta.total_miles;
	    retData.city = user.city;
		res.send(JSON.stringify(retData));
    });
    res.send(200);
}

function updateScore(req, res) {
	req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User.updateScore(pid, score);
    // updateGlobalLeaderBoard(pid, score);
    // User.getCity(pid, function(city) {
    // 	if(city && city != "") {
    // 		updateLeaderBoard("leaderboard:" + city ,pid, score);
    // 	}
    // });
    res.send(200);
}

function getlocallb(req, res) {
	req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User.getCity(pid, function(city) {
    	if(city && city != "") {

			var retObj = {};
			retObj['top'] = [];

    		getzrevrange("leaderboard:" + city, 1, 3, function(result){
    			console.log("Gitesh result", result);
    			for(var i = 0;i<3;i++){
    				User.getUserFromDb(result[i], function(user) {
	    				temp['name'] = user.name;
						temp['snuid'] = user.snuid;
						temp['olamiles'] = user.olamiles;
						temp['rank'] = i+1;
						retObj['top'].push(temp);
    				});
    			}

	    		getRank(pid,function(user_rank){
	    			var min = user_rank > 14 ? user_rank:14;
	    			retObj['rest'] = [];
	    			var progress = 21;
	    			getzrevrange("leaderboard:" + city, min - 10, min + 11, function(result){
						for(var i = min - 10; i < min + 11;) {
							User.getUserFromDb(result[i], function(user) {
			    				temp['name'] = user.name;
								temp['snuid'] = user.snuid;
								temp['olamiles'] = user.olamiles;
								temp['rank'] = i+1;
								retObj['top'].push(temp);
								if(--progress == 0){
									res.send(JSON.stringify(retObj));	
								}
		    				});
						}
					});
	    		});
    		});
    	}
    });
    User.updateScore(pid, score);
}


function getfriendlb(req, res) {
	//res.send(JSON.stringify(_dummydata));return;
	req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
	req.assert('pid', 'User Id invalid').notEmpty();
    var pid = req.params.pid;
    User.getUserFromDb(pid, function(user) {
    	if(user) {
    		getFriendsChips(user.sn_friend_list, function(list) {
    			var sortable = [];
    			for (var i =0; i<list.length;i++) {
				   	sortable.push([list['id'], list['olamiles'], list['snuid'], list['name']]);
				}
				sortable.sort(function(a, b) {return b[1] - a[1]});	

				var retObj = {};

				retObj['top'] = [];

				for (var i =0; i<3;i++) {
					var temp = {};
					temp['name'] = sortable[i][3];
					temp['snuid'] = sortable[i][2];
					temp['olamiles'] = sortable[i][1];
					temp['rank'] = i+1;
					retObj['top'].push(temp);
				}
				var user_rank;
				for (var i =0; i<list.length;i++) {
					if(user.snuid == sortable[i][2]);
					user_rank = i+1;
				}

				var min = user_rank > 14 ? user_rank:14;
				var max = list.length > min + 10 ? min + 10:list.length;

				retObj['rest'] = [];
				for(var i = min - 10; i< max;) {
					var temp = {};
					temp['name'] = sortable[i][3];
					temp['snuid'] = sortable[i][2];
					temp['olamiles'] = sortable[i][1];
					temp['rank'] = i+1;
					retObj['rest'].push(temp);
				}
				res.send(JSON.stringify(retObj));
   			});
    	}
    });
}

getzrevrange = function(leaderboard, min, max, callback) {
    var chain = redis_client.multi();
    
    chain.zrevrange(leaderboard, min-1, max -1, 'withscores')
    chain.exec(function(err, result) {
        if(err || !result) {
            winston.info("isTournamentFinished : cant find the users in redis!");
            callback(false);
            return;
        }
        callback(result[0]);
    });
}

getRank = function(pid) {

	redis_client.zrevrank(pid, function(err, reply) {
		if (!err && reply) {
			return callback(reply);
		}
		return callback(false);
	});
};

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
