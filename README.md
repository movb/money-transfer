# Money transfer

Simple money transfer rest service.

## Run:
- Run the app, default port 8080:
```
mvn exec:java
```

- Run the app, specify the port:
```
mvn exec:java -p 6666
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
- **idempotencyKey** - transfer API supports idempotency for safely retrying requests without accidentally performing the same operation twice.