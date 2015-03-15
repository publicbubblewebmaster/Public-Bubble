# Start from a Debian image with the latest version of Go installed
# and a workspace (GOPATH) configured at /go.
FROM golang

# Copy the local package files to the container's workspace.
ADD src /go/src

# Build the runnable eventsapp inside the container.
# (You may fetch or manage dependencies here,
# either manually or with a tool like "godep".)

RUN go install github.com/ipolding/eventsapp

# Run the outyet command by default when the container starts.
ENTRYPOINT /go/bin/eventsapp -dbUrl="postgres://plfozaklqmvfwp:bIJEDYSpnOQ6yHKHu18Q31znG0@ec2-107-20-177-12.compute-1.amazonaws.com:5432/da5082qak012p0?sslmode=require"

# Document which port the service listens on
EXPOSE 4000
