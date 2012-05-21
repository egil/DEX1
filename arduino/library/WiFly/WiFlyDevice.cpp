
#include "WiFly.h"

#define DEBUG_LEVEL 0
#define REQUEST_LENGTH 40
// also defined in pde file
#define LED_PIN 9 

#include "Debug.h"

boolean currentStatus = false;
const char* UI = 
  "{"
  "'title': 'Light switch',"
  "'version': 1.0,"
  "'widgets': ["
  "{ 'id': '1', 'title': 'Light on', 'type': 'editable', 'content-type': 'boolean', 'widgets': [] }"
  "]}";


boolean WiFlyDevice::findInResponse(const char *toMatch,
                                    unsigned int timeOut = 0) {
  /*

   */

  // TODO: Change 'sendCommand' to use 'findInResponse' and have timeouts,
  //       and then use 'sendCommand' in routines that call 'findInResponse'?

  // TODO: Don't reset timer after successful character read? Or have two
  //       types of timeout?

  int byteRead;

  unsigned int timeOutTarget; // in milliseconds


/*  Serial.println("Entered findInResponse");
  Serial.print("Want to match: ");
  Serial.println(toMatch);
  Serial.println("Found: ");*/

  for (unsigned int offset = 0; offset < strlen(toMatch); offset++) {

    // Reset after successful character read
    timeOutTarget = millis() + timeOut; // Doesn't handle timer wrapping

    while (!uart.available()) {
      // Wait, with optional time out.
      if (timeOut > 0) {
        if (millis() > timeOutTarget) {
          return false;
        }
      }
      delay(1); // This seems to improve reliability slightly
    }

    // We read this separately from the conditional statement so we can
    // log the character read when debugging.
    byteRead = uart.read();

    delay(1); // Removing logging may affect timing slightly

    DEBUG_LOG(5, "Offset:");
    DEBUG_LOG(5, offset);
//    Serial.print((char) byteRead);
    DEBUG_LOG(4, byteRead);

    if (byteRead != toMatch[offset]) {
      offset = 0;
      // Ignore character read if it's not a match for the start of the string
      if (byteRead != toMatch[offset]) {
        offset = -1;
      }
      continue;
    }
  }

  return true;
}



boolean WiFlyDevice::responseMatched(const char *toMatch) {
  /*
   */
  boolean matchFound = true;

  DEBUG_LOG(3, "Entered responseMatched");

  for (unsigned int offset = 0; offset < strlen(toMatch); offset++) {
    while (!uart.available()) {
      // Wait -- no timeout
    }
    if (uart.read() != toMatch[offset]) {
      matchFound = false;
      break;
    }
  }
  return matchFound;
}



#define COMMAND_MODE_ENTER_RETRY_ATTEMPTS 5

#define COMMAND_MODE_GUARD_TIME 250 // in milliseconds

boolean WiFlyDevice::enterCommandMode(boolean isAfterBoot) {
  /*

   */

  DEBUG_LOG(1, "Entered enterCommandMode");

  // Note: We used to first try to exit command mode in case we were
  //       already in it. Doing this actually seems to be less
  //       reliable so instead we now just ignore the errors from
  //       sending the "$$$" in command mode.

  for (int retryCount = 0;
       retryCount < COMMAND_MODE_ENTER_RETRY_ATTEMPTS;
       retryCount++) {

    // At first I tried automatically performing the
    // wait-send-wait-send-send process twice before checking if it
    // succeeded. But I removed the automatic retransmission even
    // though it makes things  marginally less reliable because it speeds
    // up the (hopefully) more common case of it working after one
    // transmission. We also now have automatic-retries for the whole
    // process now so it's less important anyway.

    if (isAfterBoot) {
      delay(1000); // This delay is so characters aren't missed after a reboot.
    }

    delay(COMMAND_MODE_GUARD_TIME);

    uart.print("$$$");

    delay(COMMAND_MODE_GUARD_TIME);

    // We could already be in command mode or not.
    // We could also have a half entered command.
    // If we have a half entered command the "$$$" we've just added
    // could succeed or it could trigger an error--there's a small
    // chance it could also screw something up (by being a valid
    // argument) but hopefully it's not a general issue.  Sending
    // these two newlines is intended to clear any partial commands if
    // we're in command mode and in either case trigger the display of
    // the version prompt (not that we actually check for it at the moment
    // (anymore)).

    // TODO: Determine if we need less boilerplate here.

    uart.println();
    uart.println();

    // TODO: Add flush with timeout here?

    // This is used to determine whether command mode has been entered
    // successfully.
    // TODO: Find alternate approach or only use this method after a (re)boot?
    uart.println("ver");

    if (findInResponse("\r\nWiFly Ver", 1000)) {
      // TODO: Flush or leave remainder of output?
      return true;
    }
  }
  return false;
}



void WiFlyDevice::skipRemainderOfResponse() {
  /*
   */

  DEBUG_LOG(3, "Entered skipRemainderOfResponse");

    while (!(uart.available() && (uart.read() == '\n'))) {
      // Skip remainder of response
    }
}


void WiFlyDevice::waitForResponse(const char *toMatch) {
  /*
   */

   // Note: Never exits if the correct response is never found
   while(!responseMatched(toMatch)) {
     skipRemainderOfResponse();
   }
}



WiFlyDevice::WiFlyDevice(SpiUartDevice& theUart) : uart (theUart) {
  /*

    Note: Supplied UART should/need not have been initialised first.

   */
  // The WiFly requires the server port to be set between the `reboot`
  // and `join` commands so we go for a "useful" default first.
  serverPort = DEFAULT_SERVER_PORT;
  serverConnectionActive = false;
}

// TODO: Create a constructor that allows a SpiUartDevice (or better a "Stream") to be supplied
//       and/or allow the select pin to be supplied.


void WiFlyDevice::begin() {
  /*
   */

  DEBUG_LOG(1, "Entered WiFlyDevice::begin()");

  while (!uart.begin()) {
    Serial.println("uart init failed, retrying");
  }
  reboot(); // Reboot to get device into known state
  requireFlowControl();
  setConfiguration();
}

// TODO: Create a `begin()` that allows IP etc to be supplied.

#define SOFTWARE_REBOOT_RETRY_ATTEMPTS 5

boolean WiFlyDevice::softwareReboot(boolean isAfterBoot = true) {
  /*

   */

  DEBUG_LOG(1, "Entered softwareReboot");

  for (int retryCount = 0;
       retryCount < SOFTWARE_REBOOT_RETRY_ATTEMPTS;
       retryCount++) {

    // TODO: Have the post-boot delay here rather than in enterCommandMode()?

    if (!enterCommandMode(isAfterBoot)) {
      return false; // If the included retries have failed we give up
    }

    uart.println("reboot");

    // For some reason the full "*Reboot*" message doesn't always
    // seem to be received so we look for the later "*READY*" message instead.

    // TODO: Extract information from boot? e.g. version and MAC address

    if (findInResponse("*READY*", 2000)) {
      return true;
    }
  }

  return false;
}

boolean WiFlyDevice::hardwareReboot() {
  /*
   */
  uart.ioSetDirection(0b00000010);
  uart.ioSetState(0b00000000);
  delay(1);
  uart.ioSetState(0b00000010);

  //return findInResponse("*READY*", 2000);
  waitForResponse("*READY*");
}

#define USE_HARDWARE_RESET true
#if USE_HARDWARE_RESET
#define REBOOT hardwareReboot
#else
#define REBOOT softwareReboot
#endif

void WiFlyDevice::reboot() {
  /*
   */

  DEBUG_LOG(1, "Entered reboot");

  while (!REBOOT()) {
    Serial.println("Failed reboot, retrying");
    delay(1000);
  }
}


boolean WiFlyDevice::sendCommand(const char *command,
                                 boolean isMultipartCommand = false,
                                 const char *expectedResponse = "AOK") {
  /*
   */
  DEBUG_LOG(1, "Entered sendCommand");
  DEBUG_LOG(2, "Command:");
  DEBUG_LOG(2, command);

  uart.print(command);

  if (!isMultipartCommand) {
    uart.flush();
    uart.println();

    // TODO: Handle other responses
    //       (e.g. autoconnect message before it's turned off,
    //        DHCP messages, and/or ERR etc)
    waitForResponse(expectedResponse);
  }

  return true;
}


void WiFlyDevice::requireFlowControl() {
  /*


    Note: If flow control has been set but not saved then this
          function won't handle it correctly.

    Note: Any other configuration changes made since the last
          reboot will also be saved by this function so this
          function should ideally be called immediately after a
          reboot.

   */

  DEBUG_LOG(1, "Entered requireFlowControl");

  enterCommandMode();

  // TODO: Reboot here to ensure we get an accurate response and
  //       don't unintentionally save a configuration we don't intend?

  sendCommand("get uart", false, "Flow=0x");

  while (!uart.available()) {
    // Wait to ensure we have the full response
  }

  char flowControlState = uart.read();

  uart.flush();

  if (flowControlState == '1') {
    return;
  }

  // Enable flow control
  sendCommand("set uart flow 1");

  sendCommand("save", false, "Storing in config");

  // Without this (or some delay--but this seemed more useful/reliable)
  // the reboot will fail because we seem to lose the response from the
  // WiFly and end up with something like:
  //     "*ReboWiFly Ver 2.18"
  // instead of the correct:
  //     "*Reboot*WiFly Ver 2.18"
  // TODO: Solve the underlying problem
  sendCommand("get uart", false, "Flow=0x1");

  reboot();
}

void WiFlyDevice::setConfiguration() {
  /*
   */
  enterCommandMode();

  // TODO: Handle configuration better
  // Turn off auto-connect
  sendCommand("set wlan join 0");
  sendCommand("set ip dhcp 1");

  // TODO: Turn off server functionality until needed
  //       with "set ip protocol <something>"

  // Set server port
  sendCommand("set ip localport ", true);
  // TODO: Handle numeric arguments correctly.
  uart.print(serverPort);
  sendCommand("");

  // Turn off remote connect message
  sendCommand("set comm remote 0");
  
  // Turn off status messages
  // sendCommand("set sys printlvl 0");

  // TODO: Change baud rate and then re-connect?

  // Turn off RX data echo
  // TODO: Should really treat as bitmask
  // sendCommand("set uart mode 0");
}


boolean WiFlyDevice::join(const char *ssid) {
    // Ron Guest -- begin change to support space in SSID
      // First we set the SSID (putting the SSID on the join command doesn't work
      Serial.print("Connecting to ");
      Serial.println(ssid);
      sendCommand("set wlan ssid ",true);
      sendCommand(ssid);
      if (sendCommand("join", false, "")) {// "Associated!")) {
        delay(2000);
        skipRemainderOfResponse();
        
        const char* newIp = ip();
        if (strcmp(newIp, "0.0.0.0") == 0 || strcmp(newIp, "169.254.1.1") == 0) {
          Serial.println("Connection failed");
          return false;
        }
        
        // TODO: Extract information from complete response?
        // TODO: Change this to still work when server mode not active
        //waitForResponse("Listen on ");
        
        return true;
      }
    // Ron Guest -- end change. Uncomment the below block and delete this one to restore original code

      /*sendCommand("join ", true);
      // TODO: Actually detect failure to associate
      // TODO: Handle connecting to Adhoc device
      if (sendCommand(ssid, false, "Associated!")) {
        // TODO: Extract information from complete response?
        // TODO: Change this to still work when server mode not active
        waitForResponse("Listen on ");
        skipRemainderOfResponse();
        return true;
      }*/
      return false;
}


boolean WiFlyDevice::join(const char *ssid, const char *passphrase,
                          boolean isWPA) {
  /*
   */
  // TODO: Handle escaping spaces/$ in passphrase and SSID

  // TODO: Do this better...
  sendCommand("set wlan ", true);

  if (isWPA) {
    sendCommand("passphrase ", true);
  } else {
    sendCommand("key ", true);
  }

  sendCommand(passphrase);

  return join(ssid);
}


#define IP_ADDRESS_BUFFER_SIZE 16 // "255.255.255.255\0"

const char * WiFlyDevice::ip() {
  /*


    The return value is intended to be dropped directly
    into calls to 'print' or 'println' style methods.

   */
  static char ip[IP_ADDRESS_BUFFER_SIZE] = "";

  // TODO: Ensure we're not in a connection?

  enterCommandMode();

  // Version 2.19 of the WiFly firmware has a "get ip a" command but
  // we can't use it because we want to work with 2.18 too.
  sendCommand("get ip", false, "IP=");

  char newChar;
  byte offset = 0;

  // Copy the IP address from the response into our buffer
  while (offset < IP_ADDRESS_BUFFER_SIZE) {
    newChar = uart.read();

    if (newChar == ':') {
      ip[offset] = '\x00';
      break;
    } else if (newChar != -1) {
      ip[offset] = newChar;
      offset++;
    }
  }

  // This handles the case when we reach the end of the buffer
  // in the loop. (Which should never happen anyway.)
  // And hopefully this prevents us from failing completely if
  // there's a mistake above.
  ip[IP_ADDRESS_BUFFER_SIZE-1] = '\x00';

  // This should skip the remainder of the output.
  // TODO: Handle this better?
  waitForResponse("<");
  while (uart.read() != ' ') {
    // Skip the prompt
  }

  // For some reason the "sendCommand" approach leaves the system
  // in a state where it misses the first/next connection so for
  // now we don't check the response.
  // TODO: Fix this
  uart.println("exit");
  //sendCommand("exit", false, "EXIT");

  return ip;
}

boolean WiFlyDevice::configure(byte option, unsigned long value) {
  /*
   */

  // TODO: Allow options to be supplied earlier?

  switch (option) {
    case WIFLY_BAUD:
      // TODO: Use more of standard command sending method?
      enterCommandMode();
      uart.print("set uart instant ");
      uart.println(value);
      delay(10); // If we don't have this here when we specify the
                 // baud as a number rather than a string it seems to
                 // fail. TODO: Find out why.
      SpiSerial.begin(value);
      // For some reason the following check fails if it occurs before
      // the change of SPI UART serial rate above--even though the
      // documentation says the AOK is returned at the old baud
      // rate. TODO: Find out why
      if (!findInResponse("AOK", 100)) {
        return false;
      }
      break;
    default:
      return false;
      break;
  }
  return true;
}

/*****************************************************************************
  Code by David & Egil below this point. Fighting spaghetti with spaghetti!
  
*****************************************************************************/

void WiFlyDevice::write(const char* string) {
  uart.println(string);
  read();
}

void WiFlyDevice::read() {
  int timeout = 600;
  unsigned long startTime = millis();
  const char* ok = "AOK\r\n<2.23> \n";
  int index = 0;

  while (millis() < startTime + timeout) {
    while (!uart.available()) {
      if (!(millis() < startTime + timeout))
        return;
    }
    
    char c = (char)uart.read();
    Serial.print(c);
    
    if (c == ok[index]) {
      index++;
      if (index == strlen(ok)) {
        return;
      }
    } else {
      index = 0;
    }
    
    startTime = millis();
  }
}

void WiFlyDevice::newBegin() {
  while (!uart.begin()) {
    Serial.println("uart init failed, retrying");
  }
  
  reboot(); // Reboot to get device into known state
  requireFlowControl();
}

const char* WiFlyDevice::newConnect(const char* ssid, const char* pass) {
  write("$$$");
  
  write("set wlan join 0");
  write("set ip dhcp 1");
  write("set ip localport 80");
  write("set comm remote 0");

  uart.write("set wlan ssid ");
  write(ssid);
  uart.write("set wlan passphrase ");
  write(pass);
  uart.println("join");
  const char* ip = waitForIP();
  write("exit");
  return ip;
}

const char* WiFlyDevice::waitForIP() {
  int timeout = 6000;
  unsigned long startTime = millis();
  static char expected[] = "IP=";
  static char donotwant[] = "FAILED";
  static char ip[16] = "";
  int index = 0;
  int ipIndex = 0;
  int badIndex = 0;
  
  while (true) {
    while (!uart.available()) {
      if (!(millis() < startTime + timeout))
        return NULL;
    }
    
    char c = uart.read();
    Serial.print(c);
    
    if (c == donotwant[badIndex]) {
      badIndex++;
      if (badIndex == strlen(donotwant)) {
        return NULL;
      }
    } else {
      badIndex = 0;
    }
    
    if (c == expected[index]) {
      index++;
      if (index == strlen(expected)) {
        while (c != ':') {
          while (!uart.available());
          c = uart.read();
          ip[ipIndex++] = c;
        }
        ip[--ipIndex] = '\0';
        return ip;
      }
    } else {
      index = 0;
    }
  }
}

void WiFlyDevice::runServer() {
  while (true) {
    waitForRequest();
  }
}

void WiFlyDevice::waitForRequest() {
  int timeout = 8000;
  unsigned long start = millis();
  static char expected[] = "*OPEN*";
  int index = 0;
 
  Serial.println("Waiting for open");
  while (true)
  {
    while (!uart.available()) {
      if (millis() > start + timeout) {
        enterCommandMode();
        uart.println("close");
        uart.println("exit");
        return;
      }
    }
    char c = uart.read();
    Serial.print(c);
    if (c == expected[index]) {
      index++;
      if (index == strlen(expected)) {
        handleRequest();
        return;
      }
    } else if (c == '*') {
      index = 1; // lol
    } else {
      index = 0;
    }
  }
}

void WiFlyDevice::handleRequest() {
  int timeout = 800;
  unsigned long start = millis();
  Serial.println("Reading request");
  static char firstLine[REQUEST_LENGTH];
  boolean currentLineIsBlank = true;
  boolean isFirstLine = true;
  int index = 0;
  while (true) {
    while (!uart.available()) {
      if (millis() > start + timeout)
       {
         enterCommandMode();
         uart.println("close");
         uart.println("exit");
         return;
       }
    }
    char c = uart.read();
    Serial.print(c);
    if (isFirstLine && index < REQUEST_LENGTH) {
      firstLine[index++] = c;
    }        
    
    if (c == '\n' && currentLineIsBlank) {
      firstLine[index] = '\0';
      sendResponse(firstLine);
      return;
    }
    if (c == '\n') {
      currentLineIsBlank = true;
      isFirstLine = false;
    } else if (c != '\r') {
      currentLineIsBlank = false;
    }
  }
}

void WiFlyDevice::sendResponse(char* request) {
  Serial.print("Request is: ");
  Serial.println(request);

  uart.println("HTTP/1.1 200 OK");
  uart.println("Content-Type: text/html");
  uart.println("");
  
  if (strstr(request, " /ui ") != NULL) {
    uart.write(UI);
  } else if (strstr(request, " /values/") != NULL) {
    sendValues(request);
  } else {
    uart.write("Go to /ui or /values");
  }
  Serial.println("Sent response.");
 
  enterCommandMode();
  uart.println("close");
  uart.println("exit");
}

void WiFlyDevice::sendValues(char* request) {
  if (strstr(request, "/values/1") != NULL) {
    Serial.println("turning on");
    currentStatus = true;
    digitalWrite(LED_PIN, HIGH);
  } else if (strstr(request, "/values/0") != NULL) {
    Serial.println("turning off");
    currentStatus = false;
    digitalWrite(LED_PIN, LOW);
  } 
  
  uart.write("{ '1': ");
  uart.write(currentStatus ? "true" : "false");
  uart.write(" }\n");
}




// Preinstantiate required objects
SpiUartDevice SpiSerial;
WiFlyDevice WiFly(SpiSerial);

