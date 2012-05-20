#include "WiFly.h"
#define REQUEST_LENGTH 40

// Relies on Android device having this IP in AP mode.
// Better solution: Use gateway IP.
#define BOOTSTRAP_GATEWAY "192.168.43.1"
#define BOOTSTRAP_PORT 44444
#define LED_PIN 13

const char* UI = 
  "{"
  "'title': 'Light switch',"
  "'version': 1.0,"
  "'widgets': ["
  "{ 'id': '1', 'title': 'Light on', 'type': 'editable', 'content-type': 'boolean', 'widgets': [] }"
  "]}";
  
boolean currentStatus = false;
char ssid[33];
char key[60];
//char* ssid = "HomeAP";
//char* key = "abcdefgh";
const char* ip;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);
  
  Serial.begin(9600);
  Serial.println("Go! *****");
  
  joinBootstrapNetwork();
  readWifiInfo();
  joinRealNetwork();
  joinBootstrapNetwork();
  saveIp();
  joinRealNetwork();
  runServer();
}

void runServer() {
  WiFly.runServer();
}

void joinBootstrapNetwork() {
  ip = NULL;
  while (ip == NULL) {
    Serial.println("Connecting to BootstrapAP");
    WiFly.newBegin();
    ip = WiFly.newConnect("BootstrapAP", "1234567890");
  }
  Serial.println("Connected to bootstrap!");
}

void saveIp() {
  Client client(BOOTSTRAP_GATEWAY, BOOTSTRAP_PORT);
  Serial.println("Saving IP");
  
  if (client.connect()) {
    client.print("GET /saveip/");
    client.print(ip);
    client.println(" HTTP/1.0");
    client.println();
    delay(100);
    client.stop();
  }
  
  Serial.println("Saved");
}

void readWifiInfo() {
  int ssidIndex = 0;
  int keyIndex = 0;
 
  Client client(BOOTSTRAP_GATEWAY, BOOTSTRAP_PORT);

  if (client.connect()) {
    client.println("GET /getssid HTTP/1.0");
    client.println();

    int newlines = 0;
    boolean afterBlankLine = false;
    boolean afterSlash = false;
    
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        if (c == '\r') {
          continue;
        } else if (c == '\n') {
          afterBlankLine = (++newlines == 2);
          afterSlash = false;
        } else if (c == '/') {
          afterSlash = true;
          newlines = 0;
        } else {
          newlines = 0;
          if (afterBlankLine) {
            if (c == ' ') {
              c = '$';
            }
            if (afterSlash && keyIndex < 59)
              key[keyIndex++] = c;
            else if (ssidIndex < 32)
              ssid[ssidIndex++] = c;
          }
        }
      }
    }
  } else {
    Serial.println("Request failed");
  }
  ssid[ssidIndex] = '\0'; 
  key[keyIndex] = '\0';
  
  Serial.println(ssid);
  Serial.println(key);
  client.stop();
}

void joinRealNetwork() {
  ip = NULL;
  while (ip == NULL) {
    Serial.println("Connecting to HomeAP");
    WiFly.newBegin();
    ip = WiFly.newConnect(ssid, key);
  }
}

void loop() {
  delay(100);
/*  if (millis() - lastRequestTime > 20000) {
    Serial.println("More than 20 seconds since request, reconnecting");
    joinRealNetwork();
  }  */
  
  Client client = server.available();
  if (client) {
    Serial.println("got a client");
    char firstLine[REQUEST_LENGTH];
    boolean currentLineIsBlank = true;
    boolean isFirstLine = true;
    int index = 0;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        
        if (isFirstLine && index < REQUEST_LENGTH) {
          firstLine[index++] = c;
        }        
        
        if (c == '\n' && currentLineIsBlank) {
          firstLine[index] = '\0';
          sendResponse(client, firstLine);
          break;
        }
        if (c == '\n') {
          currentLineIsBlank = true;
          isFirstLine = false;
        } else if (c != '\r') {
          currentLineIsBlank = false;
        }
      }
    }
    delay(100); // give the web browser time to receive the data
    client.stop();
//    lastRequestTime = millis();
  }
}

void sendResponse(Client client, char* request) {
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text/html");
  client.println();
  
  if (strstr(request, " /ui ") != NULL) {
    client.println(UI);
  } else if (strstr(request, " /values/") != NULL) {
    sendValues(client, request);
  } else {
    client.println("Go to /ui or /values");
  }
}

void sendValues(Client client, char* request) {
  if (strstr(request, "/values/1") != NULL) {
    Serial.println("turning on");
    currentStatus = true;
    digitalWrite(LED_PIN, HIGH);
  } else if (strstr(request, "/values/0") != NULL) {
    Serial.println("turning off");
    currentStatus = false;
    digitalWrite(LED_PIN, LOW);
  } 
  
  client.print("{ '1': ");
  client.print(currentStatus ? "true" : "false");
  client.println(" }");
}

