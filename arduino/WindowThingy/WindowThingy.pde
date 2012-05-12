#include "WiFly.h"
#define REQUEST_LENGTH 40

const char* UI = 
  "{"
  "'title': 'Window',"
  "'version': 1.0,"
  "'widgets': ["
  "{ 'id': '1', 'title': 'Window open', 'type': 'read-only', 'content-type': 'boolean', 'widgets': [] }"
  "]}";
  
boolean windowIsOpen = true;
char connectionInfo[100];

Server server(80);

void setup() {
  Serial.begin(9600);
  Serial.println("Go!");  
  
  joinBootstrapNetwork();
  readWifiInfo();
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

void readWifiInfo() {
  int index = 0;
 
   // Relies on Android device having this IP in AP mode.
   // Better solution: Use gateway IP.
  Client client("192.168.43.1", 44444);

  if (client.connect()) {
    client.println("GET / HTTP/1.0");
    client.println();

    int newlines = 0;
    boolean afterBlankLine = false;
    
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        Serial.print(c);
        if (c == '\r') {
          continue;
        } else if (c == '\n') {
          afterBlankLine = (++newlines == 2);
        } else {
          newlines = 0;
          if (index < 99 && afterBlankLine) {
            connectionInfo[index++] = c;
          }
        }
      }
    }
  } else {
    Serial.println("Request failed");
  }
  connectionInfo[index] = '\0'; 
}

void joinRealNetwork() {
  char* ssid = strtok(connectionInfo, "/");
  char* key = strtok(NULL, "/");

  // don't ask
  char key2[strlen(key) + 1];
  for (int i = 0; i < strlen(key); i++) 
    key2[i] = key[i];
  key2[strlen(key)] = '\0';

  WiFly.begin();
  if (WiFly.join(ssid, key2, true)) {
    Serial.println("Connected to real wifi!");
    Serial.println(WiFly.ip());
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

