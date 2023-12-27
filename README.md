# Kcrud
A REST/GraphQL **CRUD** server example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* Most common **REST** operations, including filterable and sortable pagination examples.
* [Exposed](https://github.com/JetBrains/Exposed) database framework.
* [Database Connection Pooling](https://ktor.io/docs/connection-pooling-caching.html#connection-pooling) with [HikariCP ](https://github.com/brettwooldridge/HikariCP).
* [Encryption](https://github.com/perracolabs/Kcrud/blob/master/src/main/kotlin/com/kcrud/data/database/tables/ContactTable.kt) at field level example.
* [GraphQL](https://graphql.org/) with either [ExpediaGroup](https://opensource.expediagroup.com/graphql-kotlin/docs/server/ktor-server/ktor-overview) or [KGraphQL](https://github.com/aPureBase/KGraphQL) frameworks. Examples: Context, Mutations, Queries, Pagination and Filters.
* [Koin](https://insert-koin.io/) dependency injection.
* [Connection Rate limit](https://ktor.io/docs/rate-limit.html) examples.
* [JWT authentication](https://ktor.io/docs/jwt.html) for both REST and GraphQL.
* [Basic authentication](https://ktor.io/docs/basic.html) example.
* [JSON serialization](https://ktor.io/docs/serialization.html) with [Kotlinx](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).
* [HTML DSL](https://ktor.io/docs/html-dsl.html) example.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) embeddable databases, both in-memory and file-based.
* [HOCON configuration](https://ktor.io/docs/configuration-file.html) example, including parsing for strongly typed settings.
* [Swagger-UI](https://ktor.io/docs/swagger-ui.html#configure-swagger), [OpenAPI](https://ktor.io/docs/openapi.html) and [Redoc](https://swagger.io/blog/api-development/redoc-openapi-powered-documentation/) integration.
* [Routing](https://ktor.io/docs/routing-in-ktor.html) organization examples.
* [Call Logging](https://ktor.io/docs/call-logging.html) and [Call ID](https://ktor.io/docs/call-id.html) examples for events traceability.
* [Snowflake](https://en.wikipedia.org/wiki/Snowflake_ID) unique IDs for logging purposes, suitable for distributed systems.
---

#### TODO:
* Add database migrations examples. 
---

For convenience, it is included a
*[Postman Collection (kcrud.postman_collection)](https://github.com/perracolabs/Kcrud/blob/master/postman/kcrud.postman_collection.json)*
with all the available REST endpoints, including the GraphQL queries and mutations.

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

### Postman **GraphQL**:

Both most popular GraphQL frameworks are included in the project, *ExpediaGroup GraphQL* and *KGraphQL*.
Once the project starts the console will display their endpoints, including the playground.

- Endpoint: http://localhost:8080/graphql

#### Query Example:

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

#### Mutations Example:

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

