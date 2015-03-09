package main
 
import (
    "log"
    "net/http"
    "fmt"
    "os"
)
 
type Event struct {
    Title string
    Location string
    Description string
}
 
func main() {
    // your http.Handle calls here
    http.Handle("/kill", http.HandlerFunc(kill))
    http.Handle("/events", http.HandlerFunc(EventsHandler))
    log.Fatal(http.ListenAndServe("localhost:4000", nil))
}
 
func kill(w http.ResponseWriter, r *http.Request) {
    fmt.Println("Thus I die.")
    os.Exit(0)
}

var events_slice []Event = make([]Event, 1)

func EventsHandler(w http.ResponseWriter, r *http.Request) {
    request_method := r.Method
    switch request_method {
         
        case "GET": 
            test_event := Event{"Test Title", "Test Location", "Test Description"}
            events_slice = append(events_slice, test_event)   
            for _, event := range events_slice {
                w.Write([]byte(event.Title))
            }
        default: http.Error(w, "Unsupported method", http.StatusNotImplemented)    
    }
}