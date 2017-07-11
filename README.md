# net-glimpse

I wanted to have a little tool that can gives me a quick overview on what's going in my network, displaying it in the browser. Tools like tcpdump or [Wireshark](https://www.wireshark.org/) are great but don't give you this 'glimpse'.

net-glimpse has two parts: 1) Visualization of network traffic, and 2) Streaming of header data from your network interfaces via WebSockets.

**Have a look at [this video](https://youtu.be/-ICbIZWSxSQ).**

![screenshot](docs/screenshot3.png)

### Used technologies

* Java, JavaScript
* Pcap4J (https://github.com/kaitoy/pcap4j) to access network interfaces
* Play Framework 2.5 and sbt
* Akka to distribute network interface data to multiple WebSockets
* Graphics with [p5js](https://p5js.org/) and physics with [toxiclibs](https://github.com/hapticdata/toxiclibsjs)

## How to run

To run net-glimpse **Java 8** must be installed.

1. [Download the net-glimpse-x.x.zip](https://github.com/kristian-lange/net-glimpse/releases)

1. Unzip

1. To access network interfaces you have to start the program either as **root** or give Java special capabilities, e.g. with `sudo setcap cap_net_raw,cap_net_admin=eip /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java`.

1. It might be necessary to make the run script executable: `chmod u+x ./bin/net-glimpse`

1. Run on Linux or Unix `./bin/net-glimpse`
   
   You can specify IP and port with the parameters `-Dhttp.address` and `-Dhttp.port`. By default `localhost` and `9000` is used. E.g. `./bin/net-glimpse -Dhttp.address=172.23.1.81 -Dhttp.port=8080` binds net-glimpse to IP `172.23.1.81` and port `8080`.

1. Try in a browser, e.g. with `http://localhost:9000/?nif=myNetworkInterface` (instead of `myNetworkInterface` use the name of the network interface you want to intercept) - it should show you raw packet header data in JSON format

If you are done with net-glimpse you can stop it with `Ctrl+C`.

## Visualization of network traffic

1. `http://localhost:9000/glimpse?nif=myNetworkInterface` - shows both, Ethernet and Internet
   
1. `http://localhost:9000/ipglimpse?nif=myNetworkInterface` - shows only Internet
   
1. `http://localhost:9000/etherglimpse?nif=myNetworkInterface` - shows only Ethernet

1. `http://localhost:9000/?nif=myNetworkInterface` - shows raw packet header data in JSON

E.g. [`http://localhost:9000/glimpse?nif=wlp3s0`](http://localhost:9000/glimpse?nif=wlp3s0) shows a visualization of the Ethernet layer and the Internet layer of the network interface `wlp3s0`.

You can open multiple pages of the same or different network interface(s) at the same time.

### Visualization Details 

* Nodes represent MAC or IP addresses
* Node colors are determined by the MAC or IP address
* Nodes blink when a new packet is sent
* Edges represent sent packets
* The arrow shows the direction of the sent packet
* The edges get thicker the more packets are sent
* Edge colors are determined by the EtherType (Ethernet) or TCP/UDP port (Internet) (scroll down to see a glossary)
* If EtherType or port is one of the well known ones it's annotated at the edge (scroll down to see a glossary)
* Edges of unknown EtherTypes or ports are black/gray and by default aren't shown at the edge (can be changed in the configuration)
* Nodes and edges get removed after a while if no packets are sent (default is 10 s)
* In fullscreen mode the whole screen is used for the graph(s)

### Configuration

Many parameters (e.g. colors, node size, node repulsion, cleaning interval) can be changed in `./config/glimpse.conf`. Have a look - they have comments.

![screenshot](docs/screenshot2.png)


## Streaming of header data from your network interfaces via WebSockets

If you just want to get the header data without the visualization you have to open a WebSocket with the URL `/netdata` and the network interface you want to intercept has to be specified in the query string with the parameter 'nif'. 

E.g. in JavaScript (browser) to get traffic from the network interface `wlp3s0` one could write

```javascript
var socket = new WebSocket(ws://myhost/netdata/?nif=wlp3s0);
```

or more generally with secure WebSockets and assuming net-glimpse runs on the same host as your JavaScript is served.

```javascript
var socket = new WebSocket(
      ((window.location.protocol === "https:") ? "wss://" : "ws://") +
      window.location.host + "/netdata/?nif=wlp3s0");
```

The data format is JSON. The endpoint `/?nif=myNetworkInterface` just shows the raw data.

* It is possible to **stream different network interfaces in parallel**.
* It is also possible to **stream the same network interface to multiple destinations**.

