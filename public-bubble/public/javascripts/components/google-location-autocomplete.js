// replace with ES6 - https://facebook.github.io/react/docs/reusable-components.html

var GooglePlaceAutocomplete = React.createClass({

    googleInit :  function() {
        new google.maps.places.Autocomplete(
        React.findDOMNode(this.refs.placeAutocomplete), null);
    },

    render: function() {
            return <textarea ref="placeAutocomplete">
                {this.googleInit()}
            </textarea>
            }
});