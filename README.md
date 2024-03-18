# NBU-Currency-collector

Simple REST service to collect, store and return currency values from NBU

## Features

- Data stored in PostgreSQL
- Mocked currency service

## Run Locally

Clone the project

```bash
  git clone https://github.com/EnzeoX/NBU-Currency-collector
```

Go to the project

```bash
  mvn package -Dspring-boot.run.profiles=*selected_profile*
```
Available profiles:
 - dev
 - mock

Run application

```bash
  java -jar *application_name*.jar
```

## API Reference

### Get all actual currency

```http
  GET api/v1/currency/get
```

Response with JSON body with list of currency data Example of data:

```json
    [{
      "r030": NUMBER,
      "txt": STRING,
      "rate": DOUBLE,
      "cc": STRING,
      "exchangedate": DATE
    }]
```

### Get currency by provided date

```http
  GET api/v1/currency/get/by-date/{date}
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `date` | `LocalDate` | **Required**. Date in format yyyy-MM-dd |
if data is available for provided date - response with JSON body with list of currency data Example of data:

```json
    [{
      "r030": NUMBER,
      "txt": STRING,
      "rate": DOUBLE,
      "cc": STRING,
      "exchangedate": DATE
    }]
```

if not - response with empty JSON "[]"

### Delete currency by provided date

```http
  POST api/v1/currency/delete/by-date/{date}
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `date` | `LocalDate` | **Required**. Date in format yyyy-MM-dd |

In success - Response with JSON body:

```json
    [{
      "status": STRING,
      "message": STRING,
      "time": LocalDateTime
    }]
```
