
var EventForm = React.createClass(
    {   validate: function() {
            var title = this.refs.title.getDOMNode().value.trim();
            var description = this.refs.description.getDOMNode().value.trim();
            var address = this.refs.address.getDOMNode().value.trim();
            console.log("title=" + title);
            console.log("address=" + description);
            console.log("description=" + title);
        },


        handleSubmit : function(e) {
            e.preventDefault();
            console.log("clicked submit");
            this.validate()
            var title = this.refs.title.getDOMNode().value.trim();
            var description = this.refs.description.getDOMNode().value.trim();
            var address = this.refs.address.getDOMNode().value.trim();
            console.log("this.props.url = " + this.props.url);
            $.ajax();            
            this.props.onCommentSubmit({author: author, text: text});
            this.refs.author.getDOMNode().value = '';
            this.refs.description.getDOMNode().value = '';           
        },

        render : function() {
            return (
        <div> 
        <h1 className="small-6 small-centered column">Post an Event</h1>
        <form className="small-6 small-centered column" onSubmit={this.handleSubmit}>
            <input type="text" placeholder="Event Title..." ref="title" className="small-6 small-centered column"/>
            <input type="text" placeholder="Event address..." ref="address" className="small-6 small-centered column"/>
            <textarea placeholder="Event Description..." ref="description" className="small-6 small-centered column"/>
            <input type="submit" value="Post" className="small-6 small-centered column"/>
        </form>
        </div>
           );
        }
    }
    );

React.render(
    <EventForm url="http://localhost:3000" />,
    document.getElementById('eventForm')
    );
