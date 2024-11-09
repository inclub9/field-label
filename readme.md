# Field Label

A Java annotation processor that automatically generates constant classes for field labels. This library helps manage field labels in a centralized way and provides type-safe access to them.

## Features

- Generates constant classes from field annotations
- Converts field names from camelCase to UPPER_CASE for constants
- Supports Java 22
- Automatic processor registration (no need to create `javax.annotation.processing.Processor` file)

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.inclub9</groupId>
    <artifactId>field-label</artifactId>
    <version>1.0.7</version>
</dependency>
```

## Usage

### 1. Annotate Your Entity Fields

```java
public class User {
    @FieldLabel("Username")
    private String username;

    @FieldLabel("Password")
    private String password;

    @FieldLabel("Email Address")
    private String email;

    // getters and setters
}
```

### 2. Compile Your Code

After compilation, the processor will automatically generate a new class with the suffix "Label". For example, for the `User` class above, it will generate:

```java
public final class UserLabel {
    private UserLabel() {}

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String EMAIL = "Email Address";
}
```

### 3. Use the Generated Constants

```java
public class UserService {
    public void validateUser(User user) {
        if (user.getUsername() == null) {
            throw new ValidationException(UserLabel.USERNAME + " is required");
        }
    }

    public void displayUserInfo(User user) {
        System.out.println(UserLabel.USERNAME + ": " + user.getUsername());
        System.out.println(UserLabel.EMAIL + ": " + user.getEmail());
    }
}
```

## Benefits

- Centralized management of field labels
- Type-safe access to labels
- Prevents typos through compile-time checking
- Easy label updates and maintenance
- Suitable for internationalization (i18n)
- No manual creation of constant classes required

## Best Practices

1. Include generated sources directory in your `.gitignore`
2. Remember to recompile after changing field names or labels
3. Update references when renaming fields
4. Use meaningful and consistent labels across your application

## Requirements

- Java 22 or higher
- Maven 3.x

## Building from Source

```bash
mvn clean install
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and feature requests, please use the GitHub issue tracker.

## Acknowledgments

- Built with [Google Auto Service](https://github.com/google/auto/tree/main/service) for annotation processor registration