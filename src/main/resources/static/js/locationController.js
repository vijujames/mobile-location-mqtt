'use strict';

angular.module('locationApp')
.controller('LocationController', ['$scope', 'config', function($scope, config){

    // location history with time
    $scope.locationList = [];

    // create a GeoJSON point to serve as a starting point
    $scope.point = {
        "type": "Point",
        "coordinates": [-74.50, 40]
    };

    // a featureCollection to use with a symbol/image
    $scope.featureCollection = {
        "type": "FeatureCollection",
        "features": [{
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": $scope.point.coordinates
            }
        }]
    };

    var map = new mapboxgl.Map({
        container: 'locationmap',                   // container id
        style: 'mapbox://styles/mapbox/streets-v9', // stylesheet location
        center: $scope.point.coordinates,           // starting position [lng, lat]
        zoom: 10                                    // starting zoom
    });


    /**
     * Adds the given location/time to the location history
     */
    $scope.setPosition = function(lon, lat, epochtime) {
        $scope.point.coordinates[0] = lon;
        $scope.point.coordinates[1] = lat;

        if ($scope.locationList.length == 10) {
            $scope.locationList.length = 0;
        }
        var time = "";
        if (epochtime) {
            var dt = new Date(epochtime * 1000);
            time = dt.toLocaleString();
        }
        $scope.locationList.push("Lon: " + lon + " Lat: " + lat + " " + time);
    }

    function addCircleLayer() {
        map.addSource('locCircleSource', { type: 'geojson', data: $scope.point });
        map.addLayer({
            "id": "drone-glow-strong",
            "type": "circle",
            "source": "locCircleSource",
            "paint": {
                "circle-radius": 10,
                "circle-color": "#0000ff",
                "circle-opacity": 0.6
            }
        });
    }

    function addImageLayer() {
       map.loadImage(config.locationImage, function(error, image) {
            if (error) throw error;
            map.addImage('gpsIcon', image);

            map.addSource('locSymbolSource', {type: 'geojson', "data": $scope.featureCollection});
            map.addLayer({
                "id": "points",
                "type": "symbol",
                "source": "locSymbolSource",
                "layout": {
                    "icon-image": "gpsIcon",
                    "icon-size": 0.25
                }
            });
        });
    }

    // add a new layer to the map to display the location
    map.on('load', function() {
        // addCircleLayer();
        addImageLayer();
    });


    // new connection to the WebSocket endpoint
    var socket = new SockJS(config.locationEndpoint);
    var stompClient = Stomp.over(socket);
    stompClient.connect({ }, function(frame) {
        // subscribe to /topic/location endpoint
        stompClient.subscribe(config.locationTopic, function(data) {
            var message = JSON.parse(data.body);
            $scope.setPosition(message.lon, message.lat, message.tst);
            //        map.getSource('locCircleSource').setData($scope.point);
            map.getSource('locSymbolSource').setData($scope.featureCollection);
            map.setCenter($scope.point.coordinates);
            $scope.$apply();
        });
    });

}]);

