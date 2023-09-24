# Kcrud
A simple **CRUD** example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* Most common **REST** operations.
* [GraphQL](https://graphql.org/) queries and mutations.
* [Koin](https://insert-koin.io/) dependency injection.
* [JWT](https://ktor.io/docs/jwt.html) authentication for both REST and GraphQL.
* [Basic](https://ktor.io/docs/basic.html) authentication example.
* [Rate limit](https://ktor.io/docs/rate-limit.html) examples.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) databases, for both in-memory or file based.

---

## Handling Security

The enabling or disabling of security is managed through the `hconf` configuration file.
Under the `jwt` and `basic-auth` sections, you'll find an `is_enabled` flag.

- For `JWT`, both  `REST` and `GraphQL` endpoints will require authentication.
- For `basic authentication`, only the root endpoint prompts for username and password.

### Generating and Refreshing JWT Authorization Tokens
- #### To Create a new JWT (JSON Web Token) authorization token use the following endpoint:
```
http://localhost:8080/auth/token/create
```
Creating a new token requires basic credential authentication. In *[Postman](https://www.postman.com/)* select the `Authorization` tab
and create a `Basic Auth` type with the credentials defined in the `application.conf` file
located under the `resources` directory.
- #### To refresh an existing token use the following endpoint:
```
http://localhost:8080/auth/token/refresh
```
- #### Refreshing or using the obtained Token in *[Postman](https://www.postman.com/)* requests:
1. Open **Postman** and select the **Headers** tab.
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
- Endpoint: http://localhost:8080/graphql

- Choose `GraphQL` under the `Body` option.

#### Queries:

- Return a single employee
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

- Return all employees
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

- Create a new employee
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

- Update an existing employee
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

- Delete a single employee
```graphql
mutation {
    deleteEmployee(id: 1)
}
```

- Delete all employees
```graphql
mutation {
    deleteAllEmployees
}
```

