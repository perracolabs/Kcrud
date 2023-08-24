# kcrud
A simple **CRUD** implementation in [Kotlin](https://kotlinlang.org/) using [Ktor](https://ktor.io/), including dependency injection with [Koin](https://insert-koin.io/).


### [Postman](https://www.postman.com/) examples:

Create or update users: http://localhost:8080/v1/user

```
{
    "name": "Saco Paco",
    "age": 25
}
```
Patch an existing user: http://localhost:8080/v1/user/{id}

```
{
    "age": 45
}
```

Get or delete existing users: http://localhost:8080/v1/user/{id}

Get or delete all users: http://localhost:8080/v1/users
