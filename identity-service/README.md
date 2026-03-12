# Identity Service

The Identity Service is a core component of the English Learning App microservices architecture. It handles user authentication, authorization, and profile management.

## Features

### Administrative Features (ADMIN role only)
- **Login**: Secured login specifically for administrative access.
- **User Management**:
  - View list of all system users.
  - Add new users with assigned roles and permissions.
  - Edit user profiles and credentials.
  - **Deactivate Users**: Instead of permanent deletion, users are deactivated to maintain data integrity.
- **Statistics**: Dashboard showing user growth metrics.

### User Features
- **Registration & Login**: standard email/username based authentication.
- **Google Login**: Authentication via Google OAuth2.
- **Profile Management**: Update personal information and avatar (integrated with Cloudinary).
- **Security**:
  - Change password.
  - Reset password via email with OTP (One-Time Password) verification.
  - OTP generation and validation logic.

## Technology Stack

- **Core**: Java 21, Spring Boot 3.5.4.
- **Database**: MySQL, Spring Data JPA.
- **Security**: Spring Security (Form-based & JWT), Nimbus OAuth2.
- **Templates**: Thymeleaf with Spring Security extras.
- **Cloud Integration**: Cloudinary (for avatar storage).
- **Communication**: Spring Mail (for OTP delivery).
- **Mappers**: MapStruct for DTO/Entity conversion.
- **Utilities**: Lombok, Validation API.

## API Endpoints

### Authentication
- `POST /api/login`: Standard login.
- `POST /api/auth/google-signin`: Google OAuth2 sign-in.
- `POST /api/register`: New user registration.

### User Security
- `POST /api/reset-password`: Request password reset OTP.
- `POST /api/verified-otp`: Verify OTP code.
- `POST /api/change-password`: Update password using verified email.

### Secure Profile
- `GET /api/secure/profile`: Retrieve current user profile.
- `PUT /api/secure/profile`: Update current user profile.

## Setup and Configuration

1. **Database**: Ensure MySQL is running and configure `application.properties` with correct credentials.
2. **Cloudinary**: Set up Cloudinary account and provide `CLOUDINARY_URL` or configuration in properties.
3. **Mail**: Configure SMTP settings for sending OTP emails.
4. **Google OAuth**: Provide client ID and secret for Google Sign-in.

---
Developed by TQT.
