var ImageUploadButton = React.createClass({
    getInitialState : function() {
            return {imageAttached:false,
                    imageUploads : false,
                    uploadStatus : 0}
                    },
    imageAttached : function(event) {
                        this.setState({imageAttached : true})
                    },
    uploadStatusChange : function() {

                        if (this.state.uploadStatus === 500) {

                        return <div className="large-3 columns alert-box alert round " data-alert >
                                Upload failed :(
                                  <button href="#" className="close">&times;</button>
                               </div>;
                               }
                               },
    uploadImage : function() {

                        var x = new FormData()
                        x.append("image1", document.getElementById("image1").files[0]);
                        x.append("domainObject", "blog");

                        var blogId = document.getElementById("image1").value

                        x.append("id", blogId)
                        var request = new XMLHttpRequest();
                        request.open("POST", this.props.imageUploadUrl, method="post" );
                        var uploadStatusCode = 0

                        var componentReference = this
                        request.onreadystatechange=function()
                       {
                         if(request.readyState==4) {
                             componentReference.setState({uploadStatus: request.status});
                         }
                       }
                       request.send(x);
                    },

    render: function() {
        return (
        <div className="row">
            <br/>
            <button onClick={this.uploadImage} disabled={!this.state.imageAttached} className="large-3 columns">
            Upload image
            </button>
            {this.uploadStatusChange()}
            <input className="large-3 columns" id="image1" type="file" name="image" onChange={this.imageAttached} ></input>

        </div>
        );
        }
});