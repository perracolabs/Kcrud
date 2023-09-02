# kcrud
A simple **CRUD** example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* Most common **REST** operations.
* [GraphQL](https://graphql.org/) queries and mutations.
* [Koin](https://insert-koin.io/) dependency injection.
* [JWT Authentication](https://ktor.io/docs/jwt.html) for both REST and GraphQL.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) databases, for both in-memory or file based.

---

### Handling Security

*Kcrud* security is managed through JWT (JSON Web Tokens). The enabling or disabling of JWT security is managed through the `hconf` configuration file.

* #### Configuration in `hconf`

In the `hconf` file, under the `jwt` section, you'll find an `is_enabled` flag. This flag determines if JWT is active or not.

```hconf
...
    jwt {
        is_enabled = false  // Set to true to enable JWT
        ...
    }
```
When set to `True` all `REST` and `GraphQL` endpoints will require authentication.

* #### Fetching an authorization Token

For convenience, there is a `POST` endpoint to generate tokens.
Available only while the `development` setting in `hconf` is set to `true`.

Make a `POST` request with `token` endpoint:
```
http://localhost:8080/token
```

* #### Using the obtained Token in *[Postman](https://www.postman.com/)* requests

To include the obtained JWT token in the requests, follow these steps:

1. Open **Postman** and go to the **Headers** tab.
2. Add a new key-value pair:
   - Key: `Authorization`
   - Value: `Bearer <The-token-with-no-quotes>`

---

### **REST** endpoints:

* Create an employee: `POST` http://localhost:8080/v1/employee
* Update an employee: `PUT` http://localhost:8080/v1/employee/{id}
* Get an employee: `GET` http://localhost:8080/v1/employee/{id}
* Delete an employee: `DELETE` http://localhost:8080/v1/employee/{id}
* Get all employees: `GET` http://localhost:8080/v1/employees
* Delete all employees: `DELETE` http://localhost:8080/v1/employees

`json`
```json
{
    "firstName": "Saco",
    "lastName": "Paco",
    "dob": "1988-01-01",
    "contact": {
        "email": "saco.paco@email.com",
        "phone": "123-456-7890"
    }
}
```
---

### Postman **GraphQL** examples:
* Endpoint: http://localhost:8080/graphql
* Choose `GraphQL` under the `Body` option.

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
        contact {
            id
            email
            phone
        }
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
        contact {
            id
            email
            phone
        }
   }
}
```
#### Mutations:

* Create a new employee
```graphql
mutation {
    createEmployee(employee: {
        firstName: "Saco",
        lastName: "Paco",
        dob: "2000-01-01",
        contact: {
            email: "saco.paco@email.com"
            phone: "123-456-789"
        }
    }) {
        id
        firstName
        lastName
        fullName
        dob
        contact {
            id
            email
            phone
        }
    }
}
```

* Update an existing employee
```graphql
mutation {
    updateEmployee(id: 1, employee: {
        firstName: "NewSaco",
        lastName: "NewPaco",
        dob: "2000-01-01",
        contact: {
            email: "new.saco.paco@email.com"
            phone: "987-654-321"
        }
    }) {
        id
        firstName
        lastName
        fullName
        dob
        contact {
            id
            email
            phone
        }
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

