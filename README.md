# Public-Bubble
The website for the Public Bubble - a thinking space for public sector graduates


To run in your local development environment.

```sh
docker run --rm --name public_bubble_postgres -p 5432:5432 -e POSTGRES_DB=public_bubble_local -e POSTGRES_USER=docker -e POSTGRES_PASSWORD=password postgres:9.4
./activator run
```

To interact with the database

```sh
docker run -it  --link public_bubble_postgres postgres:9.4 bash
```


Visit www.publicbubble.co.uk/login and log-in using the public bubble gmail address.