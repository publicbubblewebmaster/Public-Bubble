var Event = React.createClass({
    render: function() {
        return (
        <div className="row">
            <li className="event">
              <a href={"/update/event/" + this.props.idNum}>{this.props.title}</a>
              <a className="alert round label" href={"/delete/event/" + this.props.idNum}>DELETE!</a>
            </li>
        </div>
        );
        }
});

var EventList = React.createClass({

  getInitialState: function() {
      return {data: []};
  },

  componentDidMount: function() {
      $.ajax({
          url: this.props.url,
          dataType: 'json',
          success: function(data) {
            this.setState({data: data})
          }.bind(this)
      });
  },

  render: function() {
    var eventNodes = this.state.data.map(function(event) {return (<Event title={event.title} idNum={event.id}/>)});
        return (
            <div className="eventList">
              {eventNodes}
            </div>
  )}
});     
