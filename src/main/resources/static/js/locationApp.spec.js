describe('Location App Module', function(){
    
    it('checks if mapboxgl token is set', function(){
        expect(mapboxgl.accessToken).toBeDefined();
    });

});