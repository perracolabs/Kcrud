# kcrud
A simple **CRUD** example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* All common **REST** operations.
* [GraphQL](https://graphql.org/) queries and mutations.
* [Koin](https://insert-koin.io/) dependency injection.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) databases, for both in-memory or file based.

---

### [Postman](https://www.postman.com/) **REST** examples:

* Create or update employees: http://localhost:8080/v1/employee

```json
{
    "firstName": "Saco",
    "lastName": "Paco",
    "dob": "2000-01-01"
}
```
* Patch an existing employee: http://localhost:8080/v1/employee/{id}

```json
{
    "lastName": "Kotliner"
}
```

* Get or delete an existing employee: http://localhost:8080/v1/employee/{id}

* Get or delete all employees: http://localhost:8080/v1/employees

---

### Postman **GraphQL** examples:
* Use the next URL in Postman: http://localhost:8080/graphql
* Choose **Body** select **GraphQL**

#### Queries:

*  Return a single employee
```graphql
query {
    employee(id: 1) {
        id
        firstName
        lastName
        fullName
        dob
   }
}
```

* Return all employees
```graphql
query {
    employees {
        id
        firstName
        lastName
        fullName
        dob
   }
}
```
#### Mutations:

* Create a new employee
```graphql
mutation {
    createEmployee(employee: {
        firstName: "NewName",
        lastName: "NewSurname",
        dob: "2000-01-01"
    }) {
        id
        firstName
        lastName
        fullName
        dob
    }
}
```

* Update an existing employee
```graphql
mutation {
    createEmployee(id: 1, employee: {
        firstName: "NewName",
        lastName: "NewSurname",
        dob: "2000-01-01"
    }) {
        id
        firstName
        lastName
        fullName
        dob
    }
}
```

* Update individual fields of an existing employee
```graphql
mutation {
    createEmployee(id: 1, employee: {
        dob: "2012-01-01"
    }) {
        id
        firstName
        lastName
        fullName
        dob
    }
}
```

* Delete a single employee
```graphql
mutation {
    deleteEmployee(id: 1)
}
```

* Delete all employees
```graphql
mutation {
    deleteAllEmployees
}
```
