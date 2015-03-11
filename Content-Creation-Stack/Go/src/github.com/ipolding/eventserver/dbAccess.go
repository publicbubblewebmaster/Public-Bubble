package eventserver
 
import (
    "log"
    "database/sql"
    "github.com/mattn/go-sqlite3"
)

var dataSourceName string
var DB_DRIVER string

func DbInit(dbLocation string) {
    dataSourceName = dbLocation
    sql.Register(DB_DRIVER, &sqlite3.SQLiteDriver{})
    }

func PersistEvent(event Event) {

        db, err := sql.Open(DB_DRIVER, dataSourceName)
        log.Print(dataSourceName)
        tx, err := db.Begin()
        if err != nil {
            log.Fatal(err)
        }
         
        _, err = db.Exec(
          "INSERT INTO events (title, location, description) values (?, ?, ?)", 
              event.Title,
              event.Location, 
              event.Description,
              )

        if err != nil {
              log.SetFlags(log.Llongfile)
              log.Fatal(err)
               }
        tx.Commit()
// close the DB?
    }
