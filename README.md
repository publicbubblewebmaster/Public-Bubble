# Public-Bubble
The website for the Public Bubble - a thinking space for public sector graduates

# Local set-up
To run in your local development environment.

```sh
docker run --rm --name public_bubble_postgres -p 5432:5432 -e POSTGRES_DB=public_bubble_local -e POSTGRES_USER=docker -e POSTGRES_PASSWORD=password postgres:9.4
./activator run
```

To interact with the database

```sh
docker run -it  --link public_bubble_postgres postgres:9.4 bash
```

# Usage
Visit www.publicbubble.co.uk/login and log-in using the public bubble gmail address.

# Production set-up
Set up a postgres instance
Register a Google API project with OAuth credentials

set the following environment variables:
1. JDBC_URL
1. DATABASE_USERNAME
1. DATABASE_PASSWORD
1. GOOGLE_API_SERVER_KEY

Connect to the database:
```
docker run -it  --link public_bubble_postgres postgres:9.4 psql --host public_bubble_postgres --user docker --db public_bubble_local
```

Initialize the database:
```sql
insert into user (email, role) values ('email-address', 'admin');
```
