// This #include statement was automatically added by the Particle IDE.
#include <PietteTech_DHT.h>
#include <vector>



#include "PietteTech_DHT.h"
#define DHTTYPE DHT22
#define DHTPIN D2
#define DELAY_INTERVAL 2000

void dht_wrapper();

//Library Instantiation
PietteTech_DHT DHT(DHTPIN, DHTTYPE, dht_wrapper);

//Global variables for Water Supplies
bool showerIsOn = false;
bool isDHTStarted = false;
unsigned int DHTnextSampleTime;
std::vector<int> humidityVal;
double INITIAL_STATE = -1;
int counter = 0;
int BUFFER = 30;
double convertFromMilliToBase = .001;
double convertFromBaseToKilo = .001;
double convertFromSecToMin = 1/60;
double convertFromMinToHour = 1/60;
double prevBudget = 0;
//Global variables for power measurement output
int currentValue = 0;
double totalEnergyValue = 0;
double voltage = 3.3;
float Budget = 0;
bool alreadyOff = false;
bool alreadyOn = false;
bool fiftyPercent = false;
bool seventyfivePercent = false;
int counterSecData = 0;
bool datacollectionBegin = false;
int temperature = 0;

int humidity = 0;

void setup() {
    
    Serial.begin(9600);
    //Particle.subscribe("hook-response/awsHumidity", myHandler, MY_DEVICES);
    Particle.function("getShwr", getShowerStatus);
    Particle.function("setBgt", setBudget);
    Particle.function("getElec", getElectricityStatus);
    pinMode(D3, OUTPUT);
    pinMode(D4, OUTPUT);
    Particle.function("turnonoff", turnOnOff);
    digitalWrite(D3, LOW);
    digitalWrite(D4, LOW);
    Spark.function("gettmp", getTemperature);
    Spark.function("gethmd", getHumidity);
    //Particle.variable("totalEnergyVal", &totalEnergyValue);
    //Particle.variable("Bgt", &Budget);
}

int getTemperature(String args){
    return temperature;
    //return (int)dht.readTemperature();
}

int getHumidity(String args){
    return humidity;
    //return (int)dht.readHumidity();
}

void dht_wrapper() {
    DHT.isrCallback();
}

void loop() {
    if(millis() > DHTnextSampleTime){
        
        /*The following code is to determine when the shower is on/off based on humidity.*/
        
        if(!isDHTStarted){
            
            //Serial.print("Hi");
            DHT.acquire();
            isDHTStarted = true;
            //Serial.println("Hi");
        }
        
        
        if (!DHT.acquiring()) {		// has sample completed?
    
    
            
    	    // get DHT status
    	    int result = DHT.getStatus();
    
    	    Serial.print("Read sensor: ");
    	    switch (result) {
    		case DHTLIB_OK:
    		    Serial.println("OK");
    		    break;
    		case DHTLIB_ERROR_CHECKSUM:
    		    Serial.println("Error\n\r\tChecksum error");
    		    break;
    		case DHTLIB_ERROR_ISR_TIMEOUT:
    		    Serial.println("Error\n\r\tISR time out error");
    		    break;
    		case DHTLIB_ERROR_RESPONSE_TIMEOUT:
    		    Serial.println("Error\n\r\tResponse time out error");
    		    break;
    		case DHTLIB_ERROR_DATA_TIMEOUT:
    		    Serial.println("Error\n\r\tData time out error");
    		    break;
    		case DHTLIB_ERROR_ACQUIRING:
    		    Serial.println("Error\n\r\tAcquiring");
    		    break;
    		case DHTLIB_ERROR_DELTA:
    		    Serial.println("Error\n\r\tDelta time to small");
    		    break;
    		case DHTLIB_ERROR_NOTSTARTED:
    		    Serial.println("Error\n\r\tNot started");
    		    break;
    		default:
    		    Serial.println("Unknown error");
    		    break;
    	    }
        
        
        
            /*Serial.print("Temperature is ");
            Serial.print(DHT.getFahrenheit());
            Serial.println(" degrees Fahrenheit");*/
            
            
            Serial.print("Humidity is ");
            Serial.println(DHT.getHumidity());
            //if(counter <= BUFFER){
                humidityVal.push_back(DHT.getHumidity());
            //}
            temperature = DHT.getFahrenheit();
            humidity = DHT.getHumidity();
            /*
            In the following code, I am calculating the standard deviation to ultimately calculate the z-value.
            */
            
            //Calculating Average
            double sum = 0;
            for(int i = 0; i < humidityVal.size(); i++){
                
                sum += humidityVal[i];
            }
            
            double avg = sum/humidityVal.size();
            
            //Calculating summation of (x - x')^2 where x' is average.
            double summation = 0;
            for(int i = 0; i < humidityVal.size(); i++){
                summation += pow(humidityVal[i] - avg, 2);
                
            }
            Serial.print("counter is: ");
            Serial.println(summation);
            //Calculating Standard Deviation
            double standardDeviation = sqrt(summation/humidityVal.size());
            
            //Calculate the z-value
            double zValue = abs((humidityVal[humidityVal.size() - 1]-avg)/standardDeviation);
            Serial.print("Z-Value is: ");
            Serial.println(zValue);
            /*
                So after detecting when a value is away from the z-value by a significant margin
                , clear the previous array and repopulate with new set of values. When that value turns 
            */
            //Particle.publish("awsHumidity", "true", PRIVATE);
            //delay(60000);
            if(zValue >= 3){
                if(!showerIsOn && INITIAL_STATE == -1 && counter > BUFFER){
                    Serial.println("The shower is on.");
                    showerIsOn = true;
                    INITIAL_STATE = DHT.getHumidity();
                    humidityVal.clear();
                    
                    Serial.print("INITIAL_STATE: ");
                    Serial.println(INITIAL_STATE);
                    zValue = 0;
                    Particle.publish("awsData", "true");
                }
            }else if(humidityVal[humidityVal.size() - 1] <= INITIAL_STATE && showerIsOn){
                Serial.println("The shower is off.");
                showerIsOn = false;
                humidityVal.clear();
                INITIAL_STATE = -1;
                zValue = 0;
                counter = 0;
                BUFFER += 10;
                Particle.publish("awsData", "false");
            }
            
            isDHTStarted = false;  // reset the sample flag so we can take another
            
            /*The following code is for collecting the current from the fan and calculating the cost.*/
            
            currentValue = analogRead(A0) - 2000;
            Serial.print("The current value is: ");
            Serial.println(currentValue);
            
            //digitalWrite(D3, LOW);
            
            //The following code calculates the energy for each 2 second time interval and sums that up to calculate the total energy
            if(Budget != 0 && prevBudget == Budget){
                totalEnergyValue += currentValue*voltage*DELAY_INTERVAL*.13*(.01666)*.01666*.001;
                Serial.print("The budget is $");
                Serial.println(Budget);
                Serial.print("The total energy value is $");
                Serial.println(totalEnergyValue);
                //digitalWrite(D3, HIGH);
            }else{
                totalEnergyValue = 0;
            }
            
            if(prevBudget != Budget){
                totalEnergyValue = 0;
                prevBudget = Budget;
            }
            
            if(totalEnergyValue >= Budget*0.5 && !fiftyPercent && Budget != 0){
                fiftyPercent = true;
                Particle.publish("pushbullet", "You are half way to your budget target.", 60, PRIVATE);
            }
            
            if(totalEnergyValue >= Budget*0.75 && !seventyfivePercent && Budget != 0){
                seventyfivePercent = true;
                Particle.publish("pushbullet", "You are 75% to your budget target.", 60, PRIVATE);
            }
            
            if(Budget != 0){
                counter+=2;
            }
            
            if(totalEnergyValue >= Budget && Budget != 0){
                digitalWrite(D3, LOW);
                Serial.println("You have crossed your limits!");
                Particle.publish("pushbullet", "You have crossed your electricity budget and your devices have been turned off.", 60, PRIVATE);
                Budget = 0;
                fiftyPercent = false;
                seventyfivePercent = false;
                counter = 0;
            }
            
            if(totalEnergyValue < Budget && Budget != 0){
                char buf[256];
	            snprintf(buf, sizeof(buf), "{\"a\":%d,\"b\":%.3f,\"c\":%d}", counter, totalEnergyValue, !datacollectionBegin);
	            Serial.printlnf("publishing %s", buf);
	            Particle.publish("budgetData", buf, PRIVATE);
	            datacollectionBegin = true;
            }
            //Particle.publish("Status", "You have crossed your electricity budget and your devices have been turned off.");
            Particle.publish("awsData", "Energy");
            Serial.print("The total kilowatt-hour is: ");
            Serial.println(totalEnergyValue/(convertFromMilliToBase*convertFromBaseToKilo*convertFromSecToMin*convertFromMinToHour));
            
            DHTnextSampleTime = millis() + DELAY_INTERVAL;
            counter++;
        }
        
        
        
    }
}

bool getShowerStatus(String args){
    return showerIsOn;
}

float setBudget(String args){
    //prevBudget = 0;
    Budget = args.toFloat();
    Serial.print("My budget is $");
    Serial.println(Budget);
    return Budget;
}

double getElectricityStatus(String args){
    Serial.print("The total energy value in getElec is $");
    Serial.println(totalEnergyValue);
    if(Budget == 0){
        return 0;
        
    }else if(totalEnergyValue > Budget){
        
        return (totalEnergyValue - Budget)*-1;
        
    }else{
        return (totalEnergyValue/Budget)*100;
    }   
}

int turnOnOff(String args){
    int pos = args.indexOf(',');
    int pin = 0;
    int state = 0;
    if(-1 == pos){
        return -1;
    }
    
    String strPin = args.substring(0, pos);
    String strValue = args.substring(pos + 1);
    
    if(strPin.equalsIgnoreCase("D3")){
        pin = D3;
    }else if(strPin.equalsIgnoreCase("D4")){
        pin = D4;
    }
    
    if(strValue.equalsIgnoreCase("HIGH")){
        state = HIGH;
        alreadyOn = true;
        alreadyOff = false;
    }else if(strValue.equalsIgnoreCase("LOW")){
        state = LOW;
        alreadyOff = true;
        alreadyOn = false;
    }
    
    digitalWrite(pin, state);
    return 1;
    
}

void myHandler(const char *event, const char *data) {
  // Handle the integration response
  //Serial.println(*event);
  //Serial.println(*data);
}
