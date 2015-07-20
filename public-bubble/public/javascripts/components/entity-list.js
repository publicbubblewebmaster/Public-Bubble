var Entity = React.createClass({
    render: function() {
        return (
        <div className="row">
            <li className={this.props.entityName}>
              <a href={"/update/" + this.props.entityName + "/" + this.props.idNum}>{this.props.title}</a>
              <a className="alert round label" href={"/delete/"+ this.props.entityName + "/" + this.props.idNum}>DELETE!</a>
            </li>
        </div>
        );
        }
});

var EntityList = React.createClass({

  getInitialState: function() {
      return {data: []};
  },

  componentDidMount: function() {
      $.ajax({
          url: this.props.dataSourceUrl,
          dataType: 'json',
          success: function(data) {
            this.setState({data: data})
          }.bind(this)
      });
  },

  render: function() {
    var entityName = this.props.entityName
    var entityNodes = this.state.data.map(function(entity) {
        return (<Entity entityName={entityName} title={entity.title} idNum={entity.id}/>)});
            return (
                <div className="entityList">
                    {entityNodes}
                </div>
             )}
        });
