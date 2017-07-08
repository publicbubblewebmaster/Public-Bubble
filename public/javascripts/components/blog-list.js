var Blog = React.createClass({
    render: function() {
        return (
        <div className="row">
            <li className="blog">
              <a href={"/update/blog/" + this.props.idNum}>{this.props.title}</a>
              <a className="alert round label" href={"/delete/blog/" + this.props.idNum}>DELETE!</a>
            </li>
        </div>
        );
        }
});

var BlogList = React.createClass({

  getInitialState: function() {
      return {data: []};
  },

  componentDidMount: function() {
      console.log("calling componentDidMount");
      $.ajax({
          url: this.props.url,
          dataType: 'json',
          success: function(data) {
            this.setState({data: data})
          }.bind(this)
      });
  },

  render: function() {
    var blogNodes = this.state.data.map(function(blog) {return (<Blog title={blog.title} idNum={blog.id}/>)});
        return (
            <div className="blogList">
              {blogNodes}
            </div>
  )}
});     
