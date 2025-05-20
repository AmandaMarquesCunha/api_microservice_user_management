# 📦 API REST - Gerenciamento de Usuários e Endereços

Este projeto consiste em uma aplicação **Java Spring Boot** com **JPA/Hibernate**, **PostgreSQL** e **Spring Security com JWT**, que realiza o gerenciamento de **usuários** e **endereços** com consumo do serviço externo de CEP ([ViaCEP](https://viacep.com.br/)).

Além da API, há um **frontend** acoplado que consome os endpoints REST para interações com o usuário.

---

## 🎯 Objetivo

Desenvolver uma API REST robusta e segura com funcionalidades completas de CRUD, autenticação e autorização, tratamento de erros e integração com serviço externo, com uma interface frontend conectada à API.

---

## 🛠️ Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Hibernate
- REST API
- ViaCEP (serviço externo)
- JUnit e Mockito
- React.js ou outra biblioteca no frontend (caso tenha usado uma específica, substitua aqui)

---

## 🔐 Funcionalidades da API

### 👤 Gerenciamento de Usuários e Endereços

- **CRUD completo de Usuários**
  - Campos: `id`, `nome`, `email` (único), `senha` (criptografada)
- **CRUD de Endereços**
  - Campos: `id`, `logradouro`, `número`, `complemento`, `bairro`, `cidade`, `estado`, `cep`, `usuário`
- **Relacionamento**
  - Um usuário pode ter vários endereços (OneToMany)

### 🔑 Autenticação e Autorização

- **Spring Security + JWT**
- Dois perfis:
  - `ADMIN`: acesso total
  - `USER`: acesso apenas aos próprios dados

### 🌐 Consumo de API Externa (ViaCEP)

- Busca de dados via `https://viacep.com.br/ws/{cep}/json/`
- Validação e armazenamento no banco de dados

### 📄 Paginação e Ordenação

- Listagem paginada e ordenável por `nome`, `email` e `data de criação`

### 🧨 Tratamento de Erros

- **Handler Global** para erros de:
  - Validação de campos
  - Autenticação e autorização
  - Requisições inválidas

### ✅ Testes

- **Testes unitários** com JUnit e Mockito
- **Testes de integração** nos principais endpoints

---

## 🧪 Instalação e Execução

### 📋 Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL
- Node.js (caso use React ou outro frontend moderno)

### ⚙️ Configuração do Banco de Dados

Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE user_address_manager;
````

### 🔧 Configuração

Atualize o arquivo `application.properties` ou `application.yml` com as credenciais do seu banco de dados:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/user-address-manager
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
api.users.addresses.app.jwtSecret=h5Oxtq9GeyY24+fOt0tRe6M0TnIUiHZnFLx9bRnlXwHzWVDQ1XzRGHdT7afPO5DRXjRwNzGwK3bOq+W6lWqMkzw==
api.users.addresses.app.jwtExpirationMs=86400000
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
app.users.addresses.url.viacep=https://viacep.com.br
````


