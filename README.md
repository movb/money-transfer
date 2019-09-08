# Money transfer

Simple money transfer rest service.

## Notes:

Trying to stick to the principle of "the simpler the better", I did not use different database implementations,
but made a simple storage based on ConcurrentHashMap. The design is also simple, but have some extensibility -
versioning APIs. I also believe that transfer API must supports idempotency for safely retrying requests without
accidentally performing the same operation twice, so I added optional idempotencyKey field to transaction request.

Used [sparkjava](http://sparkjava.com) for implementing REST service, [gson](https://github.com/google/gson)
for working with JSON, [lombok](https://projectlombok.org/) for slightly easier setters and getters,
[Commons Cli](https://commons.apache.org/proper/commons-cli/) for command line argument passing, 
[Jersey](https://jersey.github.io/) and [JUnit](https://junit.org) for unit and functional testing. 

## Build:
```
mvn clean package
```
## Run:
- Run the app, default port 8080:
```
java -jar target/money-transfer-1.0-SNAPSHOT.jar
```

- Run the app, specify the port:
```
java -jar target/money-transfer-1.0-SNAPSHOT.jar -p 6666
```

- Run Test Cases:
```
mvn test
```

## API

### Available methods

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| POST | /api/v1/accounts | create a new account
| GET | /api/v1/accounts/{accountId} | get account by Id | 
| GET | /api/v1/accounts/ | get all acounts information | 
| POST | /api/transfer | perform transaction between 2 accounts | 

### Methods description
#### Create account:
##### Request: 
```sh
POST /api/v1/accounts
```
```sh
{  
   "id":"someid",
   "balance":100
} 
```

- **id** _(required)_- some unique string with account id.
- **balance** - a positive integer in cents representing start balance on account, default 0.

##### Response:
```sh
Header:
"Location":"http://localhost:8080/api/v1/account/someid"
{
  "status": "ok",
  "message": {}
}
```

#### Transaction:
##### Request:
```sh
{  
   "from":"some-id",
   "to":"someid",
   "amount":100,
   "idempotencyKey":"some-key"
}
```

##### Response:
```sh
{
  "status": "ok",
  "message": {}
}
```

- **from** _(required)_ - id to charge.
- **to** _(required)_ - id to transfer moeny.
- **amount** _(required)_ - a positive integer in cents representing amount to transfer.
- **idempotencyKey** - transfer API supports idempotency for safely retrying requests without accidentally performing
the same operation twice.