var ImageUploadButton = React.createClass({
    uploadImage : function() {
                        console.log("image upload button clicked");
                        var x = new FormData()
                        x.append("image1", document.getElementById("image1of2").files[0]);
                        x.append("domainObject", "blog");

                        var blogId = document.getElementById("image1").value

                        x.append("id", blogId)
                        var request = new XMLHttpRequest();
                        request.open("POST", "@routes.BlogsController.uploadImage");
                        request.send(x);
                    },

    render: function() {
        return (
        <div className="row">
            <form id="pictureForm" action={this.props.imageUploadUrl} method="post" enctype="multipart/form-data">
                <input id="image1" type="file" name="image">
                <button id="imageUploadButton" type="button" value="Upload"/>
            </form>
            <button onclick={this.uploadImage}>
            Upload image
            </button>
        </div>
        );
        }
});