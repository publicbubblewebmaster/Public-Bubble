
var EventForm = React.createClass(
    {   validate: function() {
            var title = this.refs.title.getDOMNode().value.trim();
            var description = this.refs.description.getDOMNode().value.trim();
            console.log("title=" + title);
            console.log("description=" + description);
        },


        handleSubmit : function(e) {
            console.log("clicked submit");
            e.preventDefault();
            if (!text || !author) {
                return;
            }
            this.props.onCommentSubmit({author: author, text: text});
            this.refs.author.getDOMNode().value = '';
            this.refs.description.getDOMNode().value = '';           
        },

        render : function() {
            return (
        <div> 
        <h1>What does an H1 heading look like?</h1>
        <form className="small-6 small-centered column" onSubmit={this.handleSubmit}>
            <input type="text" placeholder="Event Title..." ref="author" className="small-6 small-centered column"/>
            <input type="text" placeholder="Event address..." ref="address" className="small-6 small-centered column"/>
            <textarea placeholder="Event Description..." ref="description" className="small-6 small-centered column"/>
            <input type="submit" value="Post" className="small-6 small-centered column"/>
        </form>
        <button id="previewButton">Preview</button>
        </div>
           );
        }
    }
    );

React.render(
    <EventForm />,
    document.getElementById('eventForm')
    );
