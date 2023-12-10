# Kcrud
A **CRUD** example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* Most common **REST** operations.
* [Exposed](https://github.com/JetBrains/Exposed) database framework.
* [Netty](https://ktor.io/docs/engines.html) async web server.
* [GraphQL](https://graphql.org/) queries and mutations using the [KGraphQL](https://github.com/aPureBase/KGraphQL) library.
* [Koin](https://insert-koin.io/) dependency injection.
* [Rate limit](https://ktor.io/docs/rate-limit.html) examples.
* [JWT](https://ktor.io/docs/jwt.html) authentication for both REST and GraphQL.
* [Basic](https://ktor.io/docs/basic.html) authentication example.
* [JSON](https://ktor.io/docs/serialization.html) serialization with [Kotlinx](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).
* [HTML DSL](https://ktor.io/docs/html-dsl.html) example.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) embeddable databases, both in-memory and file-based.
* [HCONF](https://ktor.io/docs/configuration-file.html) configuration example, including preload / parsing.
* [Swagger-UI](https://ktor.io/docs/swagger-ui.html#configure-swagger) generation.
* [Routing](https://ktor.io/docs/routing-in-ktor.html) organization examples.
* [UUID](https://github.com/perracolabs/Kcrud/blob/master/src/main/kotlin/com/kcrud/utils/UuidUtils.kt) serialization / deserialization examples for both REST and GraphQL.

---

#### TODO:
* Example using the [ExpediaGroup GraphQL](https://opensource.expediagroup.com/graphql-kotlin/docs/server/ktor-server/ktor-overview) library.
* Add database migrations example. 
---

For convenience find in the project a *[Postman](https://www.postman.com/)* collection: **`kcrud.postman_collection`**.

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

#### Employee & Contact
* `POST` http://localhost:8080/v1/employees
* `PUT` http://localhost:8080/v1/employees/{employee_id}
* `GET` http://localhost:8080/v1/employees/{employee_id}
* `DELETE` http://localhost:8080/v1/employees/{employee_id}
* Get all: `GET` http://localhost:8080/v1/employees
* Delete all: `DELETE` http://localhost:8080/v1/employees

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

#### Employment & Period
* `POST` http://localhost:8080/v1/employees/{employee_id}/employments
* `PUT` http://localhost:8080/v1/employees/{employee_id}/employments/{employment_id}
* `GET` http://localhost:8080/v1/employees/{employee_id}/employments/{employment_id}
* `DELETE` http://localhost:8080/v1/employees/{employee_id}/employments/{employment_id}
* Get all: `GET` http://localhost:8080/v1/employees/{employee_id}/employments/

`json`
```json
{
   "period": {
      "isActive": true,
      "startDate": "2023-01-01",
      "endDate": null,
      "comments": null
   },
   "probationEndDate": "2023-09-30"
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
    employee(employeeId: "b0984cf8-d63f-4d2c-a3bc-53bb3856ac3a") {
        id
        firstName
        lastName
        fullName
        dob
        maritalStatus
        honorific
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
        maritalStatus
        honorific
        contact {
            id
            email
            phone
        }
   }
}
```

- Return all employments for a specific employee
```graphql
query {
    employments(employeeId: "b0984cf8-d63f-4d2c-a3bc-53bb3856ac3a") {
        id
        probationEndDate
        period {
            isActive
            startDate
            endDate
            comments
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
        maritalStatus
        honorific
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
    updateEmployee(employeeId: "b0984cf8-d63f-4d2c-a3bc-53bb3856ac3a", employee: {
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
        maritalStatus
        honorific
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
    deleteEmployee(employeeId: "b0984cf8-d63f-4d2c-a3bc-53bb3856ac3a")
}
```

- Delete all employees
```graphql
mutation {
    deleteAllEmployees
}
```

- Create a new employment for a specific employee
```graphql
mutation {
    createEmployment(employeeId: "b0984cf8-d63f-4d2c-a3bc-53bb3856ac3a", mployment: {
         probationEndDate: "2023-04-01",
         period: {
             isActive: true,
             startDate: "2023-01-01",
             endDate: null,
             comments: null
         }
     }
    ) {
        id
        probationEndDate
        period {
            isActive
            startDate
            endDate
            comments
        }
    }
}
```
