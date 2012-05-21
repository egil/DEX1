#include "WiFly.h"

// Relies on Android device having this IP in AP mode.
// Better solution: Use gateway IP.
#define BOOTSTRAP_GATEWAY "192.168.43.1"
#define BOOTSTRAP_PORT 44444
#define LED_PIN 13 // also defined in WiFlyDevice (sigh)
 
char ssid[33];
char key[60];
//char* ssid = "HomeAP";
//char* key = "abcdefgh";
char ip[16];

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
  const char* bootIP = NULL;
  while (bootIP == NULL) {
    Serial.println("Connecting to BootstrapAP");
    WiFly.newBegin();
    bootIP = WiFly.newConnect("BootstrapAP", "1234567890");
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
  const char* realip = NULL;
  while (realip == NULL) {
    Serial.println("Connecting to HomeAP");
    WiFly.newBegin();
    realip = WiFly.newConnect(ssid, key);
  }
  strcpy(ip, realip);
}

void loop() {
  Serial.println("We shouldn't be here");
  while(1); 
}

