# 2-Phase Commit Protocol

## Description
This program will simulate a simple 2-phase commit protocol, in which a Client sends a request to a Transaction Coordinator(TC) which will then send a "prepare" request to two nodes (servers). Once the nodes both confirm their readiness to the TC, the TC will send another "commit" request, which will cause the nodes to execute the original request from the client and then send a "complete" response to the TC.  The TC will finally relay the results back to the Client.

### Tasks
1. node1 - Starts first node at port 8001
2. node2 - Starts second node at port 9000
3. TC - Starts transaction coordinator at port 8000
4. Client - Starts Client, which connects to TC at port 8000

## Protocol

### Requests
request: { "selected": <int: 1=add, 2=list, 0=quit>, "data": <thing to send>}

  add: data (string)
  prepare: no data
  ready: no data
  list: data (array of string)
  quit: no data

### Responses

sucess response: {"complete": "true" "data": thing to return }

error response: {"error": "message" : error string }


## How to run the program
### Terminal
  
Please use the following commands: 
  
For node1, run `gradle node1`
  
For node2, run `gradle node2`
  
For TC, run `gradle TC`

For Client, run `gradle Client`
    
### Requirements Fulfilled
* Server has default port of 8000
  * Run with `gradle TC`
 * TC receives requests from Client and checks with nodes
* Nodes connect to TC	
  * Run with `gradle node1`, `gradle node2` - Order of start doesn't matter here
  * Nodes receive prepare/commit requests from TC and respond appropiately
* Client connects to TC on port 8000 and localhost
  * Run with `gradle Client`
  * Client has options:
   1. Add 
   2. List
   3. Quit
  * Client requests are sent only to TC
 
 
  

 
### [ScreenCast demo](https://youtu.be/LVDo9rQIt3o)





