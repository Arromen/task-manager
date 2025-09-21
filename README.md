# Task Manager (Ktor->Spring) — Интеграционные тесты (Вариант 2)

## О проекте
Минималистичный сервис управления задачами (Task Manager) на **Spring Boot**, реализующий:
- CRUD операций для задач (title, description, deadline, priority, status, assignee)
- Фильтрация задач по статусу и приоритету
- История изменений (audit) для каждой задачи
- Экспорт отчёта по выполненным задачам в Excel
- Логирование и поддержка одновременной работы нескольких пользователей
- Автоматические интеграционные тесты

## Технологии
- Java 17
- Spring Boot 3.2.x
- Spring Data JPA, H2 (in-memory)
- Apache POI (Excel export)
- JUnit + Spring Boot Test

## Как собрать и запустить
1. Склонировать/скопировать репозиторий
2. Собрать:
```bash
mvn clean package
