# Public-Bubble
The website for the Public Bubble - a thinking space for public sector graduates


To run in your local development environment.

```sh
docker run --rm -p 5432:5432 -e POSTGRES_DB=public_bubble_local -e POSTGRES_USER=docker -e POSTGRES_PASSWORD=password postgres:9.4
./activator run
```
