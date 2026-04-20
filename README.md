# hexlet-cv

**Description:**

The project - online service to create and publish CV. It is oriented on IT-specialists.
The platform will help users to create professional CV promptly and get link of it to send employers.

use [ https://github.com/Inertia4J/inertia4j ]

## Docker

```bash
# Сборка образа
docker build -t hexlet-cv .

# Запуск (dev, H2)
docker run -p 8080:8080 hexlet-cv

# Запуск (prod, PostgreSQL)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JDBC_DATABASE_URL=jdbc:postgresql://host:5432/db \
  -e USERNAME=user \
  -e PASSWORD=secret \
  hexlet-cv

# Проверка healthcheck
curl http://localhost:8080/actuator/health
 
