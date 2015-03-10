package eventserver
 
import (
    "log"
    "database/sql"
    "github.com/mattn/go-sqlite3"
)

func PersistEvent(event Event) {

        var DB_DRIVER string
        sql.Register(DB_DRIVER, &sqlite3.SQLiteDriver{})
            
        db, err := sql.Open(DB_DRIVER, "/home/ian/dev/Projects/public-bubble/database/test.db")
        tx, err := db.Begin()
        if err != nil {
            log.Fatal(err)
        }
         
        _, err = db.Exec(
          "INSERT INTO events (title, location, description) values (?, ?, ?)", nil,
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