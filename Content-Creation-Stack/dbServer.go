package eventserver
 
import (
    "log"
    "net/http"
    "fmt"
    "os"
    "database/sql"
    "github.com/mattn/go-sqlite3"
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

            var DB_DRIVER string
            sql.Register(DB_DRIVER, &sqlite3.SQLiteDriver{})
            
            db, err := sql.Open(DB_DRIVER, "/home/ian/dev/Projects/public-bubble/database/test.db")
            tx, err := db.Begin()
            if err != nil {
                log.Fatal(err)
            }
            

            _, err = db.Exec(
              "INSERT INTO events (title, location, description) VALUES (?, ?, ?)", nil,
                "Swati Soni",
                  "go hardcoded location", "go hardcoded description")

                  if err != nil {
                   log.Fatal(err)
                   }
                tx.Commit()

                w.Write([]byte("Quick! Go check the database! There might be presents!"))
        default: http.Error(w, "Unsupported method", http.StatusNotImplemented)    
    }
}
