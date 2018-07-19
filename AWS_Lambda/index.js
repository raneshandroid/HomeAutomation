/**
 * App ID for the skill
 */
var APP_ID = "amzn1.ask.skill.15b92eba-a015-4dda-ae3a-9b530dc7780e"; //replace with "amzn1.echo-sdk-ams.app.[your-unique-value-here]";

/**
 * The AlexaSkill prototype and helper functions
 */

var http = require('https');
var AlexaSkill = require('./AlexaSkill');
var previousStatus = "false";
/*
 *
 * Particle is a child of AlexaSkill.
 *
 */
var Particle = function () {
    AlexaSkill.call(this, APP_ID);
};


// Extend AlexaSkill
Particle.prototype = Object.create(AlexaSkill.prototype);
Particle.prototype.constructor = Particle;

Particle.prototype.eventHandlers.onSessionStarted = function (sessionStartedRequest, session) {
    console.log("Particle onSessionStarted requestId: " + sessionStartedRequest.requestId + ", sessionId: " + session.sessionId);
};

Particle.prototype.eventHandlers.onLaunch = function (launchRequest, session, response) {
    console.log("Particle onLaunch requestId: " + launchRequest.requestId + ", sessionId: " + session.sessionId);
    var speechOutput = "Welcome to the Particle Demo, you can ask me what is the temperature or humidity. You can also tell me to turn on Red or Green light.";
	console.log("YE");
    response.ask(speechOutput);
};

Particle.prototype.eventHandlers.onSessionEnded = function (sessionEndedRequest, session) {
    console.log("Particle onSessionEnded requestId: " + sessionEndedRequest.requestId + ", sessionId: " + session.sessionId);
};

Particle.prototype.intentHandlers = {
    // register custom intent handlers
    ParticleIntent: function (intent, session, response) {
		var sensorSlot = intent.slots.sensor;
		var lightSlot = intent.slots.light;
		var onoffSlot = intent.slots.onoff;
		var statusSlot = intent.slots.statusSlot;
		var dollarSlot = intent.slots.dollars;
		var deviceSlot = intent.slots.devices;
		var centSlot = intent.slots.cents;
		
		var sensor = "";
		var light = "";
		var onoff = "";
		var dollars = "";
		var device = "";
		var cent = "";
		
		sensor = sensorSlot ? intent.slots.sensor.value : "";
		light = lightSlot ? intent.slots.light.value : "";
		onoff = onoffSlot ? intent.slots.onoff.value : "off";
		dollars = dollarSlot ? intent.slots.dollars.value : "";
		device = deviceSlot ? intent.slots.devices.value : "";
		cent = centSlot ? intent.slots.cents.value : "";
		//response.tell(cents);
		//response.tell(cent.length);
		
		var status = statusSlot ? intent.slots.statusSlot.value : "";
		
		//response.tell(device);
		
		var speakText = "";
		
		console.log("Sensor = " + sensor);
		console.log("Light = " + light);
		console.log("OnOff = " + onoff);
		//response.tell(light);
		var op = "";
		var pin = "";
		var pinvalue = "";
		var checkBudgetInput = false;
		
		// Replace these with action device id and access token
		var deviceid = "410022000247363339343638";
		var accessToken = "bd3b676c56061a314c1f7d1d455258aab7f1c0c2";
		
		var sparkHst = "api.particle.io";
		
		console.log("Host = " + sparkHst);
		
		// Check slots and call appropriate Particle Functions
		if(sensor == "temperature"){
			speakText = "Temperature is 69°";
			
			op = "gettmp";
			
		}
		else if(sensor == "humidity"){
			speakText = "Humidity is 75%";
			
			op = "gethmd";
		}
		else if(light == "red"){
			pin = "D0";
		}
		else if(light == "green"){
			pin = "D1";
		}else if (device == "fan"){
			pin = "D3";
		}else if (device == "light"){
			pin = "D4";
		}
		else if(status == "shower"){
			op = "getShwr";
		}else if (status == "electricity"){
			op = "getElec";
			//response.tell(op);
		}
		/*else if (cent.length > 0 && dollars == null){
			op = cent;
			response.tell(cent);
			checkBudgetInput = true;
		}*/
		
		if(dollars != null/*dollars.length > 0*/){
			//response.tell(cents);
			/*if(dollars.length > 0){
				op = dollars;
			}
			if(cents.length > 0){
				op += "."+cents;
				
			}*/
			
			op = dollars;
			//response.tell(dollars + cent);
			//response.tell("Im here");
			checkBudgetInput = true;
		}
		
		if (cent != null){
			op = cent;
			//response.tell(cent);
			checkBudgetInput = true;
		}
		
		
		//response.tell(cent);
		
		
		//response.tell(pin);
		
		
		// User is asking for temperature/pressure
		if(op.length > 0){
			var sparkPath = "/v1/devices/" + deviceid + "/" + op;
			//response.tell(sensor);
			console.log("Path = " + sparkPath);
			if(op == "gettmp" || op == "gethmd"){
				makeParticleRequest(sparkHst, sparkPath, "", accessToken, function(resp){
					var json = JSON.parse(resp);
				
					console.log(sensor + ": " + json.return_value);
				
					response.tellWithCard(sensor + " is " + json.return_value + ((sensor == "temperature") ? "°" : "%"), "Particle", "Particle!");
				});
			}else if (op == "getShwr" || op == "getElec"){
				if(op == "getShwr"){
					makeParticleRequest(sparkHst, sparkPath, "", accessToken, function(resp){
						var json = JSON.parse(resp);
					
						console.log(sensor + ": " + json.return_value);
						
						if(json.return_value == true){
						
							response.tellWithCard(status + " is on" , "Particle", "Particle!");
						}else if (json.return_value == false){
							response.tellWithCard(status + " is off" , "Particle", "Particle!");
						}
					});
				}else{
					
					makeParticleRequest(sparkHst, sparkPath, "", accessToken, function(resp){
						var json = JSON.parse(resp);
					
						console.log(sensor + ": " + json.return_value);
						
						if(json.return_value < 0){
							//response.tell(op);
							response.tellWithCard("You are " + (json.return_value/-1) + " over your budget." , "Particle", "Particle!");
						}else if (json.return_value == 0){
							response.tellWithCard("You have not set your budget yet. Please ask particle to set your electricity budget.", "Particle", "Particle!")
						}else{
							response.tellWithCard("You are " + json.return_value + " percent to your budget target." , "Particle", "Particle!");
						}
					});
				}
			}else if(checkBudgetInput){
				
				
				var sparkPath = "/v1/devices/" + deviceid + "/setBgt";
				var args = dollars;
				console.log("Path = " + sparkPath);
				/*if(cents == null){
					
				}*/
				if(dollars != null && cent == null){
					args = dollars;
					//response.tell(dollars);
				}else if (dollars != null && cent != null){
					args = dollars + "." + cent;
				}else if (dollars == null && cent != null){
					args = "0." + cent;
				}
				
				
				makeParticleRequest(sparkHst, sparkPath, args, accessToken, function(resp){
					var json = JSON.parse(resp);
					
					console.log("Budget " + json.return_value);
					if(dollars == null){
						dollars = 0;
					}
					if(cent == null){
						cent = 0;
					}
					
					response.tellWithCard("OK, your electricity budget is set to " + dollars + " dollars and " + cent + " cents", "Particle", "Particle!");
					response.ask("Continue?");
				});
				checkBudgetInput = false;
				
			}
		}
		// User is asking to turn on/off lights
		else if(pin.length > 0){
			//response.tell("Im here");
			if(onoff == "on"){
				pinvalue = "HIGH";
				
			}
			else{
				pinvalue = "LOW";
			}
			
			if(pin == "D3" || pin == "D4"){
				var sparkPath = "/v1/devices/" + deviceid + "/turnonoff";
			}else{
				var sparkPath = "/v1/devices/" + deviceid + "/ctrlled";
			}
			
			
			
			console.log("Path = " + sparkPath);
			
			var args = pin + "," + pinvalue;
			
			makeParticleRequest(sparkHst, sparkPath, args, accessToken, function(resp){
				var json = JSON.parse(resp);
				
				console.log("Temperature: " + json.return_value);
				
				if(json.return_value == -1){
					response.tellWithCard("I could not recognize your request.", "Particle", "Particle!");
				}else if (json.return_value == 1){
					if(pin == "D3"){
						response.tellWithCard("OK, your fan is turned " + onoff, "Particle", "Particle!");
					}else if (pin == "D4"){
						response.tellWithCard("OK, your light is turned " + onoff, "Particle", "Particle!");
					}else{
						response.tellWithCard("OK, " + light + " light turned " + onoff, "Particle", "Particle!");
					}
					
				}
				
				response.ask("Continue?");
			});
			//response.tell(args);
		}
		else{
			response.tell("Sorry, I could not understand what you said");
		}
		
		/*exports.handler = function (event, context, callback) {
		    // Create an instance of the Particle skill.
		    console.log("event name: " + event.Humidity);
		    
		    //var particleSkill = new Particle();
		    //particleSkill.execute(event, context);
		    response.tell(event.Humidity);
		    callback("How ya doin", event.Humidity);
		};*/
		
    },
    HelpIntent: function (intent, session, response) {
        response.ask("You can ask me what is the temperature or humidity. You can also tell me to turn on Red or Green light!");
    }
};

// Create the handler that responds to the Alexa Request.
/*exports.handler = function (event, context) {
    // Create an instance of the Particle skill.
    //if(event.Humidity.length == 0 && event.Humidity != "true" || event.Humidity != false){
    	console.log("event name: " + event.JSON);
    
    	var particleSkill = new Particle();
    	particleSkill.execute(event, context);
    
    
    
};*/

exports.handler = function index(event, context, callback) {
  //some code
	//if(event["/prod/Particle"] == null){  
		console.log("event name: " + event.JSON);
    
    	var particleSkill = new Particle();
    	particleSkill.execute(event, context);
	/*}else{
		callback("Hello", "Hello");
	}*/
}






function makeParticleRequest(hname, urlPath, args, accessToken, callback){
	// Particle API parameters
	//global.response.tell(args);
	var options = {
		hostname: hname,
		port: 443,
		path: urlPath,
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
			'Accept': '*.*'
		}
	}
	
	var postData = "access_token=" + accessToken + "&" + "args=" + args;
	
	console.log("Post Data: " + postData);
	
	// Call Particle API
	var req = http.request(options, function(res) {
		console.log('STATUS: ' + res.statusCode);
		console.log('HEADERS: ' + JSON.stringify(res.headers));
		
		var body = "";
		
		res.setEncoding('utf8');
		res.on('data', function (chunk) {
			console.log('BODY: ' + chunk);
			
			body += chunk;
		});
		
		res.on('end', function () {
            callback(body);
        });
	});

	req.on('error', function(e) {
		console.log('problem with request: ' + e.message);
	});

	// write data to request body
	req.write(postData);
	req.end();
	
	
}



