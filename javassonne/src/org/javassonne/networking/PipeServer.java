package org.javassonne.networking;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.WireFormatMessage;
import net.jxta.endpoint.WireFormatMessageFactory;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.CountingOutputStream;
import net.jxta.util.DevNullOutputStream;
/**
* This tutorial illustrates the use of JXTA Pipes to exchange messages.
* <p/>
* This peer is the pipe "server". It opens the pipe for input and waits for
* messages to be sent. Whenever a Message is received from a "client" the
* contents are printed.
*/
public class PipeServer implements PipeMsgListener {
static PeerGroup netPeerGroup = null;
/**
* Network is JXTA platform wrapper used to configure, start, and stop the
* the JXTA platform
*/
transient NetworkManager manager;
private PipeService pipeService;
private PipeAdvertisement pipeAdv;
private InputPipe inputPipe = null;
/**
* Constructor for the PipeServer object
*/
public PipeServer() {
manager = null;
try {
manager = new net.jxta.platform.NetworkManager(NetworkManager.ConfigMode.ADHOC,
"PipeServer", new File(new File(".cache"), "PipeServer").toURI());
manager.startNetwork();
} catch (Exception e) {
e.printStackTrace();
System.exit(-1);
}
//Get the NetPeerGroup
netPeerGroup = manager.getNetPeerGroup();
// get the pipe service, and discovery
pipeService = netPeerGroup.getPipeService();
// create the pipe advertisement
pipeAdv = PipeClient.getPipeAdvertisement();
}
/**
* main
*
* @param args command line args
*/
public static void main(String args[]) {
PipeServer server = new PipeServer();
server.start();
}
/**
* Dumps the message content to stdout
*
* @param msg the message
* @param verbose dumps message element content if true
*/
public static void printMessageStats(Message msg, boolean verbose) {
try {
CountingOutputStream cnt;
ElementIterator it = msg.getMessageElements();
System.out.println("------------------Begin Message---------------------");
WireFormatMessage serialed = WireFormatMessageFactory.toWire(
msg,
new MimeMediaType("application/x-jxta-msg"), null);
System.out.println("Message Size :" + serialed.getByteLength());
while (it.hasNext()) {
MessageElement el = it.next();
String eName = el.getElementName();
cnt = new CountingOutputStream(new DevNullOutputStream());
el.sendToStream(cnt);
long size = cnt.getBytesWritten();
System.out.println("Element " + eName + " : " + size);
if (verbose) {
System.out.println("[" + el + "]");
}
}
System.out.println("-------------------End Message----------------------");
} catch (Exception e) {
e.printStackTrace();
}
}
/**
* Creates the input pipe with this as the message listener
*/
public void start() {
try {
System.out.println("Creating input pipe");
// Create the InputPipe and register this for message arrival
// notification call-back
inputPipe = pipeService.createInputPipe(pipeAdv, this);
} catch (IOException io) {
io.printStackTrace();
return;
}
if (inputPipe == null) {
System.out.println(" cannot open InputPipe");
System.exit(-1);
}
System.out.println("Waiting for msgs on input pipe");
}
/**
* Closes the output pipe and stops the platform
*/
public void stop() {
// Close the input pipe
inputPipe.close();
//Stop JXTA
manager.stopNetwork();
}
/**
* PipeMsgListener interface for asynchronous message arrival notification
*
* @param event the message event
*/
public void pipeMsgEvent(PipeMsgEvent event) {
Message msg;
try {
// Obtain the message from the event
msg = event.getMessage();
if (msg == null) {
System.out.println("Received an empty message");
return;
}
// dump the message content to screen
printMessageStats(msg, true);
} catch (Exception e) {
e.printStackTrace();
return;
}
// get all the message elements
Message.ElementIterator en = msg.getMessageElements();
if (!en.hasNext()) {
return;
}
// get the message element in the name space PipeClient.MESSAGE_NAME_SPACE
MessageElement msgElement = msg.getMessageElement(null, PipeClient.MESSAGE_NAME_SPACE);
// Get message
if (msgElement.toString() == null) {
System.out.println("null msg received");
} else {
Date date = new Date(System.currentTimeMillis());
System.out.println("Message received at :" + date.toString());
System.out.println("Message created at :" + msgElement.toString());
}
}
}