var BlogsBox = React.createClass({
    render: function(){

        return(<div className="blogBox">
                    I am an blog box and I have an blogs list
                    <BlogList url={this.props.url} />
               </div>);
    }
});

React.render(
            <BlogList url="/blogs/all"/>,
            document.getElementById('blogsBox')
        );