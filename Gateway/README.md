# Gateway Microservice (MS-gateway)

## Описание

**MS-gateway** — шлюзовый микросервис, агрегирующий обращения к микросервисам кредитования (**MS-statement**) и сделок (**MS-deal**).  
Обеспечивает единый REST-интерфейс для фронтенда и сторонних систем.

---

## Описание API

- **POST `/statements`**  
  Получение списка кредитных предложений по анкете клиента.  
  **Request:** `LoanStatementRequestDto`  
  **Response:** `List<LoanOfferDto>`

- **POST `/statements/select`**  
  Выбор одного из кредитных предложений.  
  **Request:** `LoanOfferDto`  
  **Response:** `void`

- **POST `/statements/registration/{statementId}`**  
  Завершение регистрации клиента.  
  **Request:** `FinishRegistrationRequestDto`  
  **Response:** `void`

- **POST `/statements/{statementId}/documents`**  
  Создание документов.  
  **Response:** `void`

- **POST `/statements/{statementId}/documents/sign`**  
  Подписание документов.  
  **Response:** `void`

- **POST `/statements/{statementId}/documents/verify/{sesCode}`**  
  Подтверждение подписания документов по коду из СМС.  
  **Response:** `void`

- **GET `/statements`**  
  Получение всех заявок.  
  **Response:** `List<StatementDto>`

- **GET `/statements/{statementId}`**  
  Получение заявки по ID.  
  **Response:** `StatementDto`

---

## Стек технологий

- **Java 21**
- **Spring Boot 3**
- **Spring Web + RestClient**
- **Lombok**
- **Jakarta Validation** — валидация входящих данных
- **SLF4J + Logback** — логирование (info/debug/warn/error)
- **Swagger (springdoc-openapi)** — автогенерация документации
- **JUnit 5 + Mockito** — тестирование
- **MapStruct** — маппинг DTO/Entity (по мере необходимости)
- **OpenAPI** — описание интерфейсов

---

## Структура проекта

- **/controller** — REST-контроллеры
- **/dto** — DTO-запросы и ответы
- **/model/enums** — перечисления ошибок и статусов
- **/service** — интерфейсы бизнес-логики
- **/service/impl** — реализации логики и интеграций
- **/exception** — обработка ошибок
- **/config** — RestClient, Swagger, properties
- **/util** — константы, паттерны и сообщения об ошибках

---

## Swagger

Документация доступна по адресу:  
[`/swagger-ui.html`](http://localhost:9000/swagger-ui.html)  
или  
[`/swagger-ui/index.html`](http://localhost:9000/swagger-ui/index.html)

---

## Работа с ошибками

- Ошибки возвращаются в формате `ErrorResponseDto`:
    - `code` — код ошибки (`ErrorCode`)
    - `message` — текст ошибки
    - `details` — карта подробностей (если есть)
- Глобальный хендлинг реализован через `GlobalExceptionHandler`
- Ошибки логируются и централизованно обрабатываются

---

## Взаимодействие с внешними сервисами

- **MS-deal** — регистрация, документы, подпись, подтверждение, заявки
- **MS-statement** — получение и выбор кредитных предложений
- Все вызовы к микросервисам инкапсулированы в `DealClientService` и `StatementClientService`
- Вызовы реализованы через `RestClient` и обёртку `BaseRestClient` с логгированием и обработкой ошибок

---

## Как запустить

1. Убедитесь, что запущены сервисы:
    - **MS-deal**
    - **MS-statement**
    - **MS-calculator** (нужен для работы MS-deal)
2. Запустите MS-gateway. Например, через IDE или команду `mvn spring-boot:run`.
3. Проверьте доступность по адресу:  
   [`http://localhost:9000`](http://localhost:9000)
