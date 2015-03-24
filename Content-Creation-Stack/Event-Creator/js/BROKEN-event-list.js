var Event = React.createClass({
  render: function() {
    return (
      <div className="event">
          {this.props.title}
      </div>
    );
  }
});


var EventList = React.createClass({   

    loadCommentsFromServer : function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            success: function(data) {
                this.setState({data:data});
                }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
            });
    },
    
    getInitialState: function() {
         return {data : []};
    },
    componentDidMount: function() {
        this.loadCommentsFromServer();
        setInterval(this.loadCommentsFromServer, 10);  
    },

    render: function() {
        
        var eventNodes = this.state.data.map(
            function(event) {
                <Event title={event.title}>
                    Monkey see
                </Event>
                    
            });

        return ( 
        <div url="events.json">
            {eventNodes}
        </div>
        );
    }});


React.render(
    <EventList url="events.json" />,
    document.getElementById('eventList')
    );