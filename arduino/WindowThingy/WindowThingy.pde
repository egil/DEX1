#include "WiFly.h"
#define REQUEST_LENGTH 40

// Relies on Android device having this IP in AP mode.
// Better solution: Use gateway IP.
#define BOOTSTRAP_GATEWAY "192.168.43.1"
#define BOOTSTRAP_PORT 44444

const char* UI = 
  "{"
  "'title': 'Window',"
  "'version': 1.0,"
  "'widgets': ["
  "{ 'id': '1', 'title': 'Window open', 'type': 'read-only', 'content-type': 'boolean', 'widgets': [] }"
  "]}";
  
boolean windowIsOpen = true;
char ssid[33];
char key[60];
const char* ip;

Server server(80);

void setup() {
  Serial.begin(9600);
  Serial.println("Waiting a bit");
  delay(2000);  
  Serial.println("Go!");
  
  joinBootstrapNetwork();
  readWifiInfo();
  joinRealNetwork();
  joinBootstrapNetwork();
  saveIp();
  joinRealNetwork();
  
  server.begin();
}

void joinBootstrapNetwork() {
  WiFly.begin();
  while (!WiFly.join("BootstrapAP", "1234567890", true)) {
    Serial.println("Retrying");
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
  WiFly.begin();
  if (WiFly.join(ssid, key, true)) {
    ip = WiFly.ip();
    Serial.println("Connected to real wifi!");
    Serial.println(ip);
  } else {
    Serial.println("Wifi failed");
    while(1);
  }
}


void loop() {
  Client client = server.available();
  if (client) {
    Serial.println("got a client");
    boolean currentLineIsBlank = true;
    boolean isFirstLine = true;
    char firstLine[REQUEST_LENGTH];
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
  }
}

void sendResponse(Client client, char* request) {
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text/html");
  client.println();
  
  if (strstr(request, " /ui ") != NULL) {
    client.println(UI);
  } else if (strstr(request, " /values ") != NULL) {
    sendValues(client);
  } else {
    client.println("Go to /ui or /values");
  }
}

void sendValues(Client client) {
  client.print("{ '1': ");
  client.print(windowIsOpen ? "true" : "false");
  client.println(" }");
  windowIsOpen = !windowIsOpen;
}

