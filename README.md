# APISec Lab

Educational platform for learning API security through guided exercises and a deliberately vulnerable service.

Day 1 delivers a working vertical slice:

Landing → Sign up → Login → JWT → Student dashboard → Profile → Logout

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8 running locally

Create the database if your user cannot auto-create it:

```sql
CREATE DATABASE IF NOT EXISTS apisec_lab;
```

Default connection in `src/main/resources/application.properties`:

- URL: `jdbc:mysql://localhost:3306/apisec_lab`
- Username: `root`
- Password: `root`

Override with environment variables `DB_USERNAME` and `DB_PASSWORD` if needed.

## Run

```bash
mvn spring-boot:run
```

Open http://localhost:8080

## REST endpoints

| Method | Path | Auth |
| --- | --- | --- |
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/profile` | JWT cookie or `Authorization: Bearer` |

Register body:

```json
{
  "name": "Ada Student",
  "email": "ada@example.com",
  "password": "secret12",
  "confirmPassword": "secret12"
}
```

Login body:

```json
{
  "email": "ada@example.com",
  "password": "secret12"
}
```

Passwords are stored with BCrypt. Day 1 accounts are always `STUDENT`.
