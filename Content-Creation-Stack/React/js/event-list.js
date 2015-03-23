var EventList = React.createClass( 

{
    render: function() {
        return(

        <div className="eventList">
           Populate using data from: {this.props.url}
        </div>
        );
    }
}
 
    );

React.render(
    <EventList url="events.json" />,
    document.getElementById('eventList')
    );