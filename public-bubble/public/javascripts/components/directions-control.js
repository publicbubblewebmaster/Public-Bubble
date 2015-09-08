// replace with ES6 - https://facebook.github.io/react/docs/reusable-components.html

var DirectionsControl = React.createClass({
  getInitialState: function() {
      return {directionsClicked: false};
  },

   directionsClicked : function(event) {
                        this.setState({directionsClicked : !this.state.directionsClicked})
                    },
    getDirections : function() {
                        console.log("getDirections called")
                        if (this.state.directionsClicked) {
                            return <textarea>Where are you coming from?</textarea>
                        }
                    },

    render: function() {
         var textAreaOrButton;
         if (this.state.directionsClicked) {
             textAreaOrButton = <textarea>I'm coming from...'</textarea>;
         } else {
             textAreaOrButton = <button onClick={this.directionsClicked}>
                                        Don't click me!
                                    </button>
         }

        return (
                <div className="row">
                    {textAreaOrButton}
                </div>
                );


        }
});