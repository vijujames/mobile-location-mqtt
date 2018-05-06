describe('LocationController', function(){
    beforeEach(module('locationApp'));
    
    var $controller;  
  
    beforeEach(inject(function(_$controller_){
        $controller = _$controller_;
    }));
    
    describe('location', function(){
        var $scope, controller;
        
        beforeEach(function(){
            $scope = {};

            spyOn(mapboxgl, 'Map').and.callFake(function(){
                return {
                    on: function(){}
                }
            });

            spyOn(window, 'SockJS').and.callThrough();
            spyOn(window, 'Stomp').and.callFake(function(){
                return {
                    connect: function(){},
                    subscribe: function(){}
                }
            });
            
            controller = $controller('LocationController', { $scope: $scope} )
        });
        
        
        it('sets position to lon:40, lat:30', function(){
            var dt = new Date();
            $scope.setPosition(40, 30, 1525390909);
            expect($scope.point.coordinates).toEqual([40, 30]);
        });
        
        it('calls setPosition() 12 times', function(){
            var origLen = $scope.locationList.length;
            for (var i = 0; i < 12; i++) {
                $scope.setPosition(40, 30, 1525390909);
                // make sure the list clears itself after a top limit of 10
                var expectedlen = (i + origLen) % 10 + 1;
                expect($scope.locationList.length).toBe(expectedlen);
            }
        });

    });
    
});