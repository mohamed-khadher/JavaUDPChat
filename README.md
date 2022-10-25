# JavaUDPChat
This is a very entry level UDP chat application written in JAVA 

# Working principle
This app is console based and uses DatagramPackets (aka through UDP protocol). It consists of a server, that listens on port 4080 for any login commands, then initiates
a thread for each new connected user. Each client later gets it's own port number to communicate with the ClientHandler on the server-side.
The client is just a simple bi-threaded process, one that listens for user input and the other one listens for server responses.
