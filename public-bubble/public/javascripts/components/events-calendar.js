
var EventsCalendar = React.createClass({
    render: function(){

        return(<div className="eventBox">
                    I am an event box and I have an events list
                    <EventList url={this.props.url} />
               </div>);
    }
});

React.render(
            <EventList url="/events/all"/>,
            document.getElementById('eventsBox')
        );