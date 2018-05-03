# mobile-location-mqtt
## Overview
This is a spring-boot application that uses MQTT messaging to find the location of the user (e.g., mobile phone) and shows that on a web page.
The flow is as follows:
- A mobile app (Owntracks) sends the GPS location to a cloud MQTT broker (e.g., www.cloudmqtt.com)
- This app subscribes to those messages from cloudmqtt
- On receiving the message, it is written to a Web socket
- The front-end browser makes a WebSocket connection to the backend
- The Web uses a mapping library (mapbox-gl from www.mapbox.com) to display the location on the map
