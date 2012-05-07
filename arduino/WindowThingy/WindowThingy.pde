#include "WiFly.h"
#include "Credentials.h"
#define REQUEST_LENGTH 40

const char* UI = 
  "{"
  "\"title\": \"Window\","
  "\"version\": 1.0,"
  "\"widgets\": ["
  "{ \"id\": \"1\", \"title\": \"Window open\", \"type\": \"read-only\", \"content-type\": \"boolean\", \"widgets\": [] }"
  "]}";
  
boolean windowIsOpen = true;

Server server(80);

void setup() {
  Serial.begin(9600);
  Serial.println("Go!");  
  
  WiFly.begin();

  if (!WiFly.join(ssid, passphrase)) {
    while (1) {
    }
  }

  Serial.println(WiFly.ip());
  
  server.begin();
}

void loop() {
  Client client = server.available();
  if (client) {
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
    client.print("{ \"1\": ");
    client.print(windowIsOpen ? "true" : "false");
    client.println(" }");
    windowIsOpen = !windowIsOpen;
  } else {
    client.println("Go to /ui or /values");
  }
}

