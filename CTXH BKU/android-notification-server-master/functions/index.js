const functions = require('firebase-functions');

var {google} = require('googleapis');
var MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

var SCOPES = [MESSAGING_SCOPE];

var express = require('express');
var app =  express();

var bodyParser = require('body-parser');
var request = require('request');

var router =  express.Router();

app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());

router.post('/send', function(req, res) {

	getAccessToken().then(function(access_token) {
		var title = req.body.title;
		var body = req.body.body;
		var token = req.body.token;

		request.post({
			headers: {
				Authorization: 'Bearer ' + access_token
			},
			url:"https://fcm.googleapis.com/v1/projects/ctxh-manager/messages:send",
			body: JSON.stringify(
				{
					"message": {
						"token": token,
						"notification": {
							"body": body,
							"title": title,

						}
					}
				}
			)
		}, function(err, response, body){
			res.end(body);
			console.log(body);
		});

	});


});

app.use('/api', router);

function getAccessToken() {
	return new Promise(function(resolve, reject) {
		var key = require("./service-account.json");

		var jwtClient = new google.auth.JWT(
			key.client_email,
			null,
			key.private_key,
			SCOPES,
			null
		);

		jwtClient.authorize(function(err, token) {
			if (err) {
				reject(err);
				return;
			}
			resolve(token.access_token)
		});
	});
}



// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
exports.api = functions.https.onRequest(app);
