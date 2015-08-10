// replace with ES6 - https://facebook.github.io/react/docs/reusable-components.html

var GoogleMap = React.createClass({

    initialize : function() {

          var mapOptions = {
            center: new google.maps.LatLng(51.4992, 0.1247),
            zoom: 13
          };

          var map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

          var input = (
              document.getElementById('pac-input'));

          var types = document.getElementById('type-selector');
          map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);
          map.controls[google.maps.ControlPosition.TOP_LEFT].push(types);

          var placesService = new google.maps.places.PlacesService(map);
          var request = {query:"Local Government Association, Smith Square, City of London, London, United Kingdom"}
          function placeSearchCallback(placeResultArray, placesServiceStatus, placeSearchPagination) {
             putOnMap(placeResultArray[0], map)
          }

          placesService.textSearch(request, placeSearchCallback)
    },

    putOnMap :  function(place, map) {
            var marker =
                new google.maps.Marker({
                    map: map,
                    anchorPoint: new google.maps.Point(0, -29)
                });

            // If the place has a geometry, then present it on a map.
            if (place.geometry.viewport) {


            map.fitBounds(place.geometry.viewport);
            } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17);  // Why 17? Because it looks good.
            }
            marker.setIcon(({
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(35, 35)
            }));
            marker.setPosition(place.geometry.location);
            marker.setVisible(true);

            var address = '';
        if (place.address_components) {
          address = [
            (place.address_components[0] && place.address_components[0].short_name || ''),
            (place.address_components[1] && place.address_components[1].short_name || ''),
            (place.address_components[2] && place.address_components[2].short_name || '')
          ].join(' ');
        }

            var infowindow = new google.maps.InfoWindow();
            infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
            infowindow.open(map, marker);
    },

    componentDidMount :  function() {
        google.maps.event.addDomListener(window, 'load', initialize);
    },

    render: function() {
            return

            <input type="text" ref="placeAutocomplete">

            </input>
            }
});