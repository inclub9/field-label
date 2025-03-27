# Field Label

A Java annotation processor that automatically generates label constant classes from field annotations. This library streamlines label management by providing type-safe access and maintaining consistency between code and business terminology.

## Features

- Generates two types of constants:
    - Original field name format (camelCase)
    - Uppercase format with underscores (UPPER_CASE)
- Includes class name constant for easy reference
- Supports Java 22
- Zero configuration with automatic processor registration
- Thread-safe processing with concurrent collections
- Memory-efficient with optimized string builders

## Installation

Add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.inclub9</groupId>
    <artifactId>field-label</artifactId>
    <version>2.5.5</version>
</dependency>
```

## Usage

### 1. Annotate Your Fields

```java
public class User {
    @FieldLabel("Username")
    private String username;

    @FieldLabel("Email Address")
    private String emailAddress;
    
    // getters and setters
}
```

### 2. Generated Output

After compilation, a new class with the "Label" suffix is automatically generated:

```java
public final class UserLabel {
    public static final String CLASS_NAME = "User";
    
    private UserLabel() {}

    // Original field names
    public static final String username = "Username";
    public static final String emailAddress = "Email Address";

    // Uppercase constants
    public static final String USERNAME = "Username";
    public static final String EMAIL_ADDRESS = "Email Address";
}
```

### 3. Using the Constants

```java
public class UserService {
    // Using original field name format
    public void validateUser(User user) {
        if (user.getUsername() == null) {
            throw new ValidationException(UserLabel.username + " is required");
        }
    }

    // Using uppercase format
    public Map<String, String> toMap(User user) {
        Map<String, String> map = new HashMap<>();
        map.put(UserLabel.USERNAME, user.getUsername());
        map.put(UserLabel.EMAIL_ADDRESS, user.getEmailAddress());
        return map;
    }
}
```

## Key Benefits

1. **Business-Code Alignment**
    - Maintains consistency between code and business terminology
    - Centralizes label management
    - Supports clear communication between technical and business teams

2. **Development Efficiency**
    - Eliminates manual constant class creation
    - Provides compile-time type safety
    - Reduces naming inconsistencies
    - Supports rapid updates to business terminology

3. **Flexibility**
    - Supports both camelCase and UPPER_CASE formats
    - Easy integration with i18n frameworks
    - Thread-safe processing for large codebases

## Best Practices

1. **Naming Conventions**
    - Use clear, descriptive labels that match business terminology
    - Maintain consistency in label formatting across related fields
    - Consider i18n requirements when choosing labels

2. **Code Organization**
    - Group related fields together
    - Use meaningful field names that reflect their purpose
    - Document any special label requirements or conventions

3. **Maintenance**
    - Recompile after changing field annotations
    - Update all references when renaming fields
    - Review generated classes to ensure expected output

## Requirements

- Java 22 or higher
- Maven 3.x

## Building

```bash
mvn clean install
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -m 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## Support

For issues and feature requests, please use the [GitHub issue tracker](https://github.com/inclub9/field-label/issues).

## License

Licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Google Auto Service](https://github.com/google/auto/tree/main/service) for annotation processor registration
- Contributors and users who provide valuable feedback