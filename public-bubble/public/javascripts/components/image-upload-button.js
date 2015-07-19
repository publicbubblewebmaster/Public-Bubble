var ImageUploadButton = React.createClass({
    getInitialState : function() {
            return {
                    imageAttached:false,
                    imageUploads : false}},

    imageAttached : function(event) {
                        this.setState({imageAttached : true})
                    },
    uploadStatus : function() {

                        return <<div data-alert="" class="alert-box alert round">
                                Upload failed :(
                               </div>;
                    },

    uploadImage : function() {

                        console.log("image upload button clicked");
                        var x = new FormData()
                        x.append("image1", document.getElementById("image1").files[0]);
                        x.append("domainObject", "blog");

                        var blogId = document.getElementById("image1").value

                        x.append("id", blogId)
                        var request = new XMLHttpRequest();
                        request.open("POST", this.props.imageUploadUrl, method="post" );

                       request.onreadystatechange=function()
                       {
                         if(request.readyState==4)
                           this.uploadStatus()
                       }

                        request.send(x);
                        console.log("response status = " + request.status);
                    },

    render: function() {
        return (
        <div className="row">
            <input id="image1" type="file" name="image" onChange={this.imageAttached}></input>
            <button onClick={this.uploadImage} disabled={!this.state.imageAttached}>
            Upload image
            {this.uploadSuccess()}
            </button>
        </div>
        );
        }
});