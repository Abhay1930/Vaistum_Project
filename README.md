# Interview Scheduler

A small Spring Boot service for interview slot scheduling. Uses MySQL (Flyway migrations), cursor-based pagination for listing slots, and optimistic locking for race safety.

## Running locally
- Java 21, Maven, MySQL 8+.
- Create DB `interview_scheduler` and set `spring.datasource` in `application.yml` (dev profile).
- Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`.

## Notes
- Time zone is UTC. All timestamps are stored/served in UTC.
- Schema is under `src/main/resources/db/migration`.
- Tests use H2 in MySQL mode.
