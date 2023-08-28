# kcrud
A simple **CRUD** example in [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

### Characteristics:
* All common **REST** operations.
* [GraphQL](https://graphql.org/) queries and mutations.
* [Koin](https://insert-koin.io/) dependency injection.
* [H2](https://github.com/h2database/h2database) and [SQLite](https://github.com/sqlite/sqlite) databases, for both in-memory or file based.

---

### [Postman](https://www.postman.com/) **REST** examples:

* Create employees: http://localhost:8080/v1/employee

```json
{
    "firstName": "Saco",
    "lastName": "Pago",
    "dob": "1988-01-01",
    "contactDetails": {
        "email": "saco.pago@email.com",
        "phone": "123-456-7890"
    }
}
```

* For updating employees, same json as above but supply the id in the url: http://localhost:8080/v1/employee/{id}
  
* Get or delete an existing employee: http://localhost:8080/v1/employee/{id}

* Get or delete all employees: http://localhost:8080/v1/employees

---

### Postman **GraphQL** examples:
* Use the next URL in Postman: http://localhost:8080/graphql
* Choose **GraphQL** under the **Body** option. 

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
        contactDetails {
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
        contactDetails {
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
        firstName: "NewName",
        lastName: "NewSurname",
        dob: "2000-01-01",
        contactDetails: {
            email: "saco1.paco1@email.com"
            phone: "987-654-321"
        }
    }) {
        id
        firstName
        lastName
        fullName
        dob
        contactDetails {
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
        firstName: "NewName",
        lastName: "NewSurname",
        dob: "2000-01-01",
        contactDetails: {
            email: "saco1.paco1@email.com"
            phone: "987-654-321"
        }
    }) {
        id
        firstName
        lastName
        fullName
        dob
        contactDetails {
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
