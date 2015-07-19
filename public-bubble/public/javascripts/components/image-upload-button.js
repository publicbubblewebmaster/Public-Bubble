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

                        return <div data-alert="" className="alert-box alert round">
                                Upload failed :(
                               </div>;
                               } else if (this.state.uploadStatus === 0) {
                        return <div data-alert="" className="alert-box info radius">
                                                        Please select an image.
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
            {this.uploadStatusChange()}
            <input id="image1" type="file" name="image" onChange={this.imageAttached}></input>
            <button onClick={this.uploadImage} disabled={!this.state.imageAttached}>
            Upload image
            </button>
        </div>
        );
        }
});