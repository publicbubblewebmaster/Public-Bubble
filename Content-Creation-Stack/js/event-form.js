
var EventForm = React.createClass(
    {   validate: function() {
            var title = this.refs.title.getDOMNode().value.trim();
            var description = this.refs.description.getDOMNode().value.trim();
            var location = this.refs.location.getDOMNode().value.trim();
            console.log("title=" + title);
            console.log("location=" + location);
            console.log("description=" + description);
        },


        handleSubmit : function(e) {
            e.preventDefault();
            console.log("clicked submit");
            this.validate();
            var title = this.refs.title.getDOMNode().value.trim();
            var description = this.refs.description.getDOMNode().value.trim();
            var location = this.refs.location.getDOMNode().value.trim();
            console.log("INSIDE event-form.js this.props.url = " + this.props.url);
            var eventObject = {"title": title, "location": location, "description":description};
            $.ajax({
                url: this.props.url,
                dataType: 'json',
                data: JSON.stringify(eventObject),
                type: 'POST',
                    error : function(xhr, status, err) {
                    console.error(eventObject, status, err.toString());
                }.bind(this)
                });         
        },

        render : function() {
            return (
        <div> 
        <h1 className="small-6 small-centered column">Post an Event</h1>
        <form className="small-6 small-centered column" onSubmit={this.handleSubmit}>
            <input type="text" placeholder="Event Title..." ref="title" className="small-6 small-centered column"/>
            <input type="text" placeholder="Event address..." ref="location" className="small-6 small-centered column"/>
            <textarea placeholder="Event Description..." ref="description" className="small-6 small-centered column"/>
            <input type="submit" value="Post" className="small-6 small-centered column"/>
        </form>
        </div>
           );
        }
    }
    );

React.render(
    <EventForm url="http://localhost/events/" />,
    document.getElementById('eventForm')
    );
