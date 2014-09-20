/* 
 * app.js
 * 
 * Our base app code, including Express configs
 */

var express = require('express')
  , engine = require('ejs-locals')
  , winston = require('winston')
  , expressValidator = require('express-validator')
  , partials = require('express-partials')
  , _ = require('underscore')
  , app = express()
   ,bodyParser = require('body-parser');

var path = require('path');

app.use(bodyParser());

exports.init = function(port) {
        app.use(partials());
        app.layoutFilename = path.join(__dirname, '/views/layout.ejs');
        app.set('views', __dirname + '/views');
        app.set('view engine', 'ejs');
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
        app.use(function(req, res, next) {
            if(req.url.search("/js/") >= 0) {
                res.sendfile(__dirname + '/static/js/' + req.url.substr(req.url.search("/js/") + 4, req.url.length - 1));
            } else if(req.url.search("/css/") >= 0) {
                res.sendfile(__dirname + '/static/css/' + req.url.substr(req.url.search("/css/") + 5, req.url.length - 1));
            } else if(req.url.search("/img/") >= 0) {
                res.sendfile(__dirname + '/static/img/' + req.url.substr(req.url.search("/img/") + 5, req.url.length - 1));
            } else {
                next();
            }
        })
        app.use('/html', express.static(__dirname + '/static'));
        //app.use('/files', express.directory(__dirname + '/files'));
        //app.use('/files', express.static(__dirname + '/files'));
        app.use(express.static(__dirname + '/static'));
        app.enable("jsonp callback");

    app.param(function(name, fn){
      if (fn instanceof RegExp) {
        return function(req, res, next, val){
            console.log("Helloo....");
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

    app.engine('ejs', engine);

    if ('development' == app.get('env')) {
      app.set("base64encoding", true);
    }

    if ('development' == app.get('env')) {
      app.set("base64encoding", true);
    }

    app.get("/tambola", function(req, res) {
      if(
        req.headers['user-agent'].match(/Android/i) ||
        req.headers['user-agent'].match(/iPhone|iPad|iPod/i) ||
        req.headers['user-agent'].match(/IEMobile/i) // yeah i know we can combine those, duh!
        ) {
        res.redirect("tambola://tambola.moonfrog.com/"); 
      } else {
        res.redirect("http://moonfroglabs.com");
      }
    });

    app.use( function(err, req, res, next) {
        res.render('500.ejs', { locals: { error: err }, status: 500 });
    });
    
    server = app.listen(port);

    console.log("Listening on port %d in %s mode", server.address().port, app.settings.env);
    return app;
}
