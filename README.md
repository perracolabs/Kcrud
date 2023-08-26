# kcrud
A simple **CRUD** implementation in [Kotlin](https://kotlinlang.org/) using [Ktor](https://ktor.io/), including dependency injection with [Koin](https://insert-koin.io/).


### [Postman](https://www.postman.com/) examples:

Create or update employees: http://localhost:8080/v1/employee

```
{
    "name": "Saco Paco",
    "age": 25
}
```
Patch an existing employee: http://localhost:8080/v1/employee/{id}

```
{
    "age": 45
}
```

Get or delete an existing employee: http://localhost:8080/v1/employee/{id}

Get or delete all employees: http://localhost:8080/v1/employees
