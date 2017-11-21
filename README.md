# Satellite

## Roadmap
* Create CRUD class
* Create Useful annotations
* Create Linker to Database

### CRUD
Implement of CRUD
* Create - by given Object
* Read - by given id
* Update - by given id
* Delete - by given Object
* Delete - by given id

### Annotations
Implement our own annotation or java annotation

-- Exemple : Id for primary key of Object
```java
@Entity
public class User {
    @Id
    private int unique_id;
    private String name;
    private int age;
}
```

### Linker
Implement of Linker
Communicate with the Database

Implement of Linker in DB
* Push - all update or create Object
* FetchAll - all database Object
* FetchWithQuery - fetch by query in param