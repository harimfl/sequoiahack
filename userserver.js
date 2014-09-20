redis = require('redis');
_ = require('underscore');
var redis_client;

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
    uid: {type: String, required: true},
    snid: {type: Number, default: 0},
    snuid: {type: String, default: ""},
    rides: {type: Array, default: []},
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

userSchema.statics.getUserFromDb = function(id, callback) {
	var user = User.findOne({'id': id});
	if(!user) {
		console.log("No such user", id);
	}
	callback(user);
}
var User = mongoose.model('User', userSchema);

User._create("testing");

function updateGlobalLeaderBoard(pid, miles) {
	client.zadd('leaderboard:global', miles, pid);
};

function updateLeaderBoard(leaderboard ,pid, miles) {
	client.zadd(leaderboard, miles, pid);
};


function handleShutdown() {
	console.log("shutting down!");
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
}

app.post('/user/get', getUser);
app.get('/user/get', getUser);

app.post('/user/new', createNewUser);
app.get('/user/new', createNewUser);

app.get('/user/updatescore', updateScore);
app.post('/user/updatescore', updateScore);

