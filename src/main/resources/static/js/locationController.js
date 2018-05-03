'use strict';

angular.module('locationApp')
.controller('LocationController', ['$scope', 'config', function($scope, config){

    // location history with time
    $scope.locationList = [];

     // create a GeoJSON point to serve as a starting point
     var point = {
         "type": "Point",
         "coordinates": [-74.50, 40]
     };

     var map = new mapboxgl.Map({
        container: 'locationmap',                   // container id
        style: 'mapbox://styles/mapbox/streets-v9', // stylesheet location
        center: point.coordinates,                  // starting position [lng, lat]
        zoom: 10                                    // starting zoom
    });

    /**
     * Centers the map at the given longitude and latitude.
     * Also adds the given location/time to the location history
     */
    function setPosition(lon, lat, epochtime) {
        point.coordinates[0] = lon;
        point.coordinates[1] = lat;
        map.getSource('drone').setData(point);
        map.setCenter(point.coordinates);
        if ($scope.locationList.length == 10) {
            $scope.locationList.length = 0;
        }
        var time = "";
        if (epochtime) {
            var dt = new Date(epochtime * 1000);
            time = dt.toLocaleString();
        }
        $scope.locationList.push("Lon: " + lon + " Lat: " + lat + " " + time);
        $scope.$apply();
    }

    // add a new layer to display the location
    map.on('load', function () {
        // add the GeoJSON above to a new vector tile source
        map.addSource('drone', { type: 'geojson', data: point });

        map.addLayer({
            "id": "drone-glow-strong",
            "type": "circle",
            "source": "drone",
            "paint": {
                "circle-radius": 10,
                "circle-color": "#0000ff",
                "circle-opacity": 0.6
            }
        });
    });

    // new connection to the WebSocket endpoint
    var socket = new SockJS(config.locationEndpoint);
    var stompClient = Stomp.over(socket);
    stompClient.connect({ }, function(frame) {
        // subscribe to /topic/location endpoint
        stompClient.subscribe(config.locationTopic, function(data) {
            var message = JSON.parse(data.body);
            setPosition(message.lon, message.lat, message.tst);
        });
    });

}]);

