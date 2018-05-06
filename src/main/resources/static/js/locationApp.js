'use strict';

var app = angular.module('locationApp', []);

app.run([function () {
    mapboxgl.accessToken = 'pk.eyJ1IjoidmlqdWoiLCJhIjoiY2pncHAya3I2MGtyZjMzcG5jdm92c2dtaSJ9.GmmcNtf1nbUQxNJsnW5Mow';
}]);

app.constant('config',
    {
        locationTopic: "/topic/location",
        locationEndpoint: "/locdata",
        locationImage: 'img/icons8-gps-100.png'
    });