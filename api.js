var express = require('express')
  , expressValidator = require('express-validator')
  , bodyParser = require('body-parser')
  , partials = require('express-partials')
  , app = express();

app.use(partials());
app.use(bodyParser());
app.use(expressValidator({
    errorFormatter: function(param, msg, value) {
        var namespace = param.split('.')
        , root    = namespace.shift()
        , formParam = root;

        while(namespace.length) {
            formParam += '[' + namespace.shift() + ']';
        }
        return {
            param : formParam,
            msg   : msg,
            value : value
        };
    }
}));
app.use(methodOverride());
app.enable("jsonp callback");

app.param(function(name, fn){
    if (fn instanceof RegExp) {
        return function(req, res, next, val) {
            var captures;
            if (captures = fn.exec(String(val))) {
                req.params[name] = captures;
                next();
            } else {
                next('route');
            }
        }
    }
});

app.set("base64encoding", true);

app.use( function(err, req, res, next) {
    console.log("ERROR!!: ", err, req.url);
    res.status(500);
    res.render('500.ejs', { locals: { error: err }, status: 500 });
});
server = app.listen(port);

console.log("Listening on port %d in %s mode", server.address().port, app.settings.env);

function redis_get(zid, field) {

}

function get_last_ride(req, res) {
    req.params=_.extend(req.params || {}, req.query || {}, req.body || {});

    res.send(200, {
        ride_id: "OLA_1_34452_79",
        start_time: "2014-09-19 19:21:32",
        end_time: "2014-09-19 19:42:32",
        distance_travelled: 7.92,
        vehicle_type: 3,
        start_la: null,
        start_lo: null,
        end_la: null,
        end_lo: null
    });
}

function get_user_data(req, res) {
    req.params=_.extend(req.params || {}, req.query || {}, req.body || {});
    res.send(200, {
        current_city: "Bangalore",
        current_city_code: 1,
        last_used_vehicle_type: 3,
        total_distance_travelled: 49.33
    });
}

app.get('/get/last_ride', get_last_ride);
app.post('/get/last_ride', get_last_ride);

app.get('/get/user_data', get_user_data);
app.post('/get/user_data', get_user_data);