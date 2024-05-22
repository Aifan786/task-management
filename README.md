# Task Management SaaS Application
## Overview
This is a simplified version of a task management web application built using Scala and the Play Framework. The application allows users to create, update, delete, and list tasks. Each task has a title, description, due date, and status (e.g., "To Do," "In Progress," "Done"). The application supports multiple users, with each user having their own set of tasks.

## Backend
1. Scala: All backend logic is implemented in Scala.
2. Play Framework: Used for handling HTTP requests and responses, routing, and asynchronous processing.
3. Slick: Used for database access and ORM.
4. Database: MySql is used to store task data for simplicity and ease of setup.
5. Functional Programming Principles: The application is built following functional programming principles, emphasizing immutability, pure functions, and avoiding side effects.


## API Endpoints
1. **POST** /users: Create a new user.
2. **POST** /users/{userId}/tasks: Create a new task for the specified user.
3. **GET** /users/{userId}/tasks: Retrieve all tasks for the specified user.
4. **GET** /users/{userId}/tasks/{taskId}: Retrieve a specific task for the specified user.
5. **PUT** /users/{userId}/tasks/{taskId}: Update a specific task for the specified user.
6. **DELETE** /users/{userId}/tasks/{taskId}: Delete a specific task for the specified user.

## Table Design
### User
>CREATE TABLE task_management.users (
id INT AUTO_INCREMENT PRIMARY KEY, 
name VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

### Task
>CREATE TABLE tasks (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT NOT NULL,
title VARCHAR(255) NOT NULL,
description TEXT,
due_date DATE,
status VARCHAR(50) CHECK (status IN ('To Do', 'In Progress', 'Done')) NOT NULL DEFAULT 'To Do',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

## Design Decisions

1. **Functional Programming**: The application leverages Scala's functional programming features to enhance code maintainability and reliability. This includes the use of immutable data structures and pure functions.

2. **Asynchronous Processing**: Using Play Framework’s asynchronous processing capabilities ensures the application can handle multiple requests concurrently without blocking.

3. **Error Handling**: Robust error handling is implemented throughout the application to provide meaningful error messages and ensure smooth operation.

## Setup and Usage
### Prerequisites

`Java 8 or higher`
`SBT (Scala Build Tool)`

### Dependencies
`Guice` 
`Play Slick` 
`Play Slick Evolutions `
`ScalaTestPlus Play `
`MySQL Connector `
