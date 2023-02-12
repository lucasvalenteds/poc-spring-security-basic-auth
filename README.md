# POC: Spring Security Basic Auth

It demonstrates how to secure a REST API using username and password.

The goal is to develop a Web service that can find all products a user posses. The application should find the user
based on the `Authorization` header provided in the request and expose it to the resource handler, meaning we don't need
any query parameters or path parameters to determine which records should be fetched from the database.

The first step was to implement user management according to Spring Security interfaces. The application uses JPA to map
user and their authorities almost as-is from standard DDL provided by the framework. That implementation is used
internally and the interaction with the Web layer is made using custom implementations of `UserDetails` (user
representation) and `UserDetailsService` (participant that finds the user). The security requirements for each resource
the API has is defined using a custom instance of `SecurityFilterChain`. The resource that finds the products depend
on `SecurityContext` to get the principal (user authenticated) defined by the framework based on the request.

The implementation is tested using unit tests and integration tests with all dependencies provided automatically,
including the Postgres database which is provisioned by Testcontainers and migrations managed by Flyway.

## How to run

| Description             | Command                      |
|:------------------------|:-----------------------------|
| Run tests               | `./gradlew test`             |
| Run application         | `./gradlew bootRun`          |
| Provision the database¹ | `docker-compose up --detach` |
| Destroy the database¹   | `docker-compose down`        |

> ¹ Provisioning the database is required only for manual testing.

## Preview

Script for manual testing:

```shell
#!/bin/bash

username="$1"
password="$2"
credentials="$(printf "%b" "$username:$password" | base64)"

curl --verbose \
  --header "Authorization: Basic $credentials" \
  http://localhost:8080/products
```

Server response for one user:

```text
$ ./test.sh john.smith s3cr3t
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /products HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.87.0
> Accept: */*
> Authorization: Basic am9obi5zbWl0aDpzM2NyM3Q=
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sun, 12 Feb 2023 14:13:41 GMT
```

```json
{
  "products": [
    {
      "id": 1,
      "name": "Notebook"
    },
    {
      "id": 2,
      "name": "Pencil"
    },
    {
      "id": 5,
      "name": "Eraser"
    }
  ]
}
```

Server response for another user:

```text
$ ./test.sh mary.jane p4ssw0rd
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /products HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.87.0
> Accept: */*
> Authorization: Basic bWFyeS5qYW5lOnA0c3N3MHJk
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sun, 12 Feb 2023 14:18:35 GMT
```

```json
{
  "products": [
    {
      "id": 3,
      "name": "Pen"
    },
    {
      "id": 4,
      "name": "Paper"
    }
  ]
}
```

Server response for a user without permission to have products:

```
$ ./test.sh guest guest
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /products HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.87.0
> Accept: */*
> Authorization: Basic Z3Vlc3Q6Z3Vlc3Q=
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 403 
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 0
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sun, 12 Feb 2023 14:25:26 GMT
```

```json
{
  "timestamp": "2023-02-12T14:25:26.035+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/products"
}
```
