# Request management system

- [Overview](#technology-stack).
- [System architecture](#system-architecture).
- [Technology stack](#technology-stack).
- [Installation and launch](#installation-and-launch).

## Overview

The project is a web application for managing applications, implemented using Spring Boot and Vaadin. The system is designed for customers and operators, allowing customers to create and track their applications, and operators to process and manage applications.

The main features of the system:

* User registration and authentication.
* Create, view, and update applications.
* Differentiation of access rights for clients and operators.
* Real-time notification of application changes.

## System architecture

The system is based on a multi-layered architecture that includes the following levels:

* UI Layer: Implemented using Vaadin and includes various views (LoginView, RegisterView, RequestsView) for user interaction.
* Service Layer: It contains the business logic of the application implemented in the service classes (SecurityServiceImpl, UserServiceImpl, RequestServiceImpl).
* Data Access Layer: Uses Spring Data JPA to interact with the database by defining repositories for entities.
* Configuration Layer: Configures security and other aspects of the application through configuration classes (SecurityConfig).
* Broadcasting Components: Implement a real-time notification mechanism using the Broadcaster class.

## Technology stack

* Java
* Spring Boot
* Spring Data
* Spring Security
* PostgreSQL
* Vaadin v24
* Docker

## Installation and launch

1. Change all optional things in:
   * docker-compose-EXAMPLE.yml (delete -EXAMPLE)
   * default-example.conf (delete -example)
   * SecurityConfig.java
2. Make build and .jar file of the project
```bash
mvn clean package
```
3. Run:
```bash
docker-compose build
docker-compose up -d
```
4. Go to localhost:8080 (if you haven't changed anything)
5. That's all!