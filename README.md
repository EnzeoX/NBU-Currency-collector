# NBU Currency collector

Simple REST service to collect, store and return currency values from NBU (National Bank of Ukraine)

## Features

- Data stored in PostgreSQL
- Mocked currency service

## Run Locally

### Clone the project

```bash
  git clone https://github.com/EnzeoX/NBU-Currency-collector
```

### Go to the project

```bash
  mvn package
```


### Run application

```bash
  java -jar *application_name*.jar --spring.profiles.active=*selected_profile*
```
_Available profiles:_
- _dev_
- _mock_

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

if data is available for provided date - response with list of JSON currency data.

Example of data:
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

In success - Response with list of JSON objects:

```json
    [{
      "status": STRING,
      "message": STRING,
      "time": LocalDateTime
    }]
```

### Error responses

If any error occurred while requesting for a currency data - service will return a json with status, error message and time.

Example of response json:

```json
    [{
      "status": STRING,
      "message": STRING,
      "time": LocalDateTime
    }]
```


