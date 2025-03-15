# User Authentication Service

A Spring Boot application with OAuth2 authentication and MongoDB for user management.

## Features

- User registration and login with JWT authentication
- OAuth2 social login integration (Google & GitHub)
- Role-based authorization with Spring Security
- MongoDB for data persistence
- Docker integration for easy deployment

## Prerequisites

- JDK 17
- Docker and Docker Compose
- Google and GitHub OAuth credentials

## Running the Application

### Environment Variables

Create a `.env` file in the root directory with the following variables:

```
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
TOKEN_SECRET=your_jwt_token_secret
```

### Using Docker Compose

To start the application and MongoDB:

```bash
docker-compose up -d
```

This will start:
- MongoDB on port 27017
- MongoDB Express (admin UI) on port 8081
- User Auth Service on port 8080

### Using Maven (for development)

```bash
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

- `POST /api/auth/signup`: Register a new user
- `POST /api/auth/login`: Authenticate and get JWT token

### User Operations

- `GET /api/users/me`: Get current user profile
- `PUT /api/users/me/profile`: Update current user profile

### Admin Operations

- `GET /api/admin/users`: Get all users (Admin only)
- `PUT /api/admin/users/{userId}/roles`: Update user roles (Admin only)
- `DELETE /api/admin/users/{userId}`: Delete a user (Admin only)

## OAuth2 Login

OAuth2 login is configured for Google and GitHub:

- Google login: `/oauth2/authorize/google`
- GitHub login: `/oauth2/authorize/github`

Redirect URI: `http://localhost:3000/oauth2/redirect`

## Project Structure

The application follows a standard Spring Boot project structure:

- `controller`: REST API controllers
- `model`: MongoDB entity models
- `repository`: MongoDB repositories
- `service`: Business logic services
- `security`: Security configurations and JWT utilities
- `dto`: Data Transfer Objects
- `exception`: Custom exceptions and error handling

## Technologies

- Spring Boot 3.4.3
- Spring Security
- Spring Data MongoDB
- JWT Authentication
- OAuth2 Client
- Lombok
- Docker & Docker Compose