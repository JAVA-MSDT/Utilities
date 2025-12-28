# Field Masking

## 📋 Overview

The Field Masking is a modern, annotation-based solution for dynamically masking sensitive data in Java objects. It supports both regular Java classes and Java Records, with conditional masking based on runtime inputs and Spring integration.

## 🚀 Features

- ✅ **Annotation-based masking** - Simple `@MaskMe` annotation
- ✅ **Conditional masking** – Mask based on runtime conditions
- ✅ **Spring Integration** - Full Spring Context support with dependency injection
- ✅ **Type-safe** - Comprehensive type conversion system
- ✅ **Framework-agnostic** - Works with any Java project
- ✅ **Thread-safe** - Proper handling of concurrent requests
- ✅ **No modification of originals** – Returns new masked instances
- ✅ **Supports both Classes and Records**
- ✅ **Original Value Manipulation** - Transform original values when mask is blank
- ✅ **Modular Converter Architecture** - Clean, extensible type conversion
- ✅ **Field-Specific Logic** - Context-aware masking based on field names
- ✅ **Placeholder Support** - Dynamic field referencing with `[fieldName]` syntax

## 📦 Installation
- Not there yet, but maybe in the future :) 
### Maven
```xml
<dependency>
    <groupId>com.masking</groupId>
    <artifactId>masking-library</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.masking:masking-library:2.0.0'
```

## 🎨 Architecture

### Core Components

```
com.javamsdt.masking.maskme.api/
├── MaskMe.java                  # Annotation for marking fields
├── MaskCondition.java           # Interface for masking conditions
├── MaskProcessor.java           # Main processing engine
├── MaskConditionFactory.java    # Spring-aware condition factory
└── MaskingException.java        # Custom exception handling

com.javamsdt.masking.maskme.api.converter/
├── Converter.java               # Base converter interface
├── ConverterFactory.java        # Orchestrates type conversion
├── NumberConverter.java         # Numeric types (BigDecimal, Integer, etc.)
├── DateTimeConverter.java       # Temporal types (LocalDate, Instant, etc.)
├── PrimitiveConverter.java      # Basic types (String, Boolean, Character)
├── SpecialTypeConverter.java    # Special types (UUID, URL, Enum, etc.)
├── FallbackConverter.java       # Reflection-based fallback
└── FieldAccessUtil.java         # Field access and placeholder utilities

com.javamsdt.masking.maskme.implemintation/
├── AlwaysMaskCondition.java     # Always masks fields
├── MaskOnInput.java             # Input-based conditional masking
└── MaskPhone.java               # Phone number masking condition

com.javamsdt.masking.config/
└── MaskingConfiguration.java    # Spring auto-configuration
```

### Type Conversion System

The library uses a modular converter architecture with specialized converters:

- **Chain of Responsibility** - Tries converters in order until one succeeds
- **Spring Integration** - Converters can access Spring-managed beans
- **Original Value Access** - Converters can manipulate original field values
- **Switch Expressions** - Modern Java syntax for clean type mapping

## 🎯 Core Components

### 1. `@MaskMe` Annotation

```java
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskMe {
    Class<? extends MaskCondition>[] conditions();
    String maskValue() default "****";
}
```

### 2. `MaskCondition` Interface

```java
public interface MaskCondition {
    boolean shouldMask(Object fieldValue, Object containingObject);
    
    default void setInput(Object input) {
        // Default implementation
    }
}
```

### 3. `MaskProcessor` Class

The main processing class that handles masking logic.

## 📖 Basic Usage

### 1. Define Your DTO with `@MaskMe` Annotations

#### For Records:
```java
public record UserDto(
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "1000")
        Long id,
        
        @MaskMe(conditions = {MaskOnInput.class}, maskValue = "[id]-[genderId]")
        String name,
        
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "[name] it is me")
        String email,
        
        @MaskMe(conditions = {AlwaysMaskCondition.class})
        String password,
        
        @MaskMe(conditions = {MaskPhone.class}, maskValue = "[PHONE_MASKED]")
        String phone,
        
        AddressDto address,
        
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "01/01/1800")
        LocalDate birthDate,
        
        String genderId,
        String genderName,
        
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "")
        BigDecimal balance,
        
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "1900-01-01T00:00:00.00Z")
        Instant createdAt
) {}
```

#### For Regular Classes:
```java
public class User {
    @MaskMe(conditions = {MaskOnInput.class}, maskValue = "*****")
    private String name;
    
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    private String email;
    
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    private LocalDate birthDate;
    
    // Getters and setters
}
```

### 2. Implement Mask Conditions

#### Spring-Managed Condition (Recommended):
```java
@Component
public class MaskOnInput implements MaskCondition {
    
    @Autowired
    private UserService userService; // Can inject Spring beans
    
    private String input;
    
    @Override
    public void setInput(Object input) {
        if (input instanceof String) {
            this.input = (String) input;
        }
    }
    
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        // Can use injected services
        if (userService != null) {
            // Business logic using service
        }
        return input != null && input.equalsIgnoreCase("MaskMe");
    }
}
```

#### Always Mask Condition:
```java
public class AlwaysMaskCondition implements MaskCondition {
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return true;
    }
}
```

#### Legacy Non-Spring Condition:
```java
public class MaskOnInput implements MaskCondition {
    
    private String input;
    
    public MaskOnInput() {
        this.input = "";
    }
    
    @Override
    public void setInput(Object input) {
        if (input instanceof String) {
            this.input = (String) input;
        }
    }
    
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return input != null && input.equalsIgnoreCase("MaskMe");
    }
}
```

#### Phone Masking Condition:
```java
public class MaskPhone implements MaskCondition {
    
    private String maskPhoneFlag;
    
    @Override
    public void setInput(Object input) {
        if (input instanceof String) {
            this.maskPhoneFlag = (String) input;
        }
    }
    
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return "YES".equalsIgnoreCase(maskPhoneFlag) || 
               "TRUE".equalsIgnoreCase(maskPhoneFlag);
    }
}
```

### 3. Use in Controller

```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    private final MaskProcessor processor;
    
    @GetMapping("/masked/{id}")
    public UserDto getMaskedUserById(@PathVariable final Long id,
                                     @RequestHeader("Mask-Input") String maskInput,
                                     @RequestHeader("Mask-Phone") String maskPhone) {
        
        try {
            processor.setConditionInput(MaskOnInput.class, maskInput);
            processor.setConditionInput(MaskPhone.class, maskPhone);
            return processor.process(userMapper.toDto(userService.findUserById(id)));
        } finally {
            processor.clearInputs();
        }
    }
    
    @GetMapping
    public List<UserDto> getUsers(@RequestHeader("Mask-Input") String maskInput) {
        try {
            processor.setConditionInput(MaskOnInput.class, maskInput);
            return userService.findUsers().stream()
                    .map(user -> processor.process(userMapper.toDto(user)))
                    .toList();
        } finally {
            processor.clearInputs();
        }
    }
}
```

## 🔧 Advanced Features

### Field-Specific Processing

The library includes intelligent field-specific processing:

- **Name fields**: Automatically append "[][]" to mask values for identification
- **Email fields**: Domain replacement when original contains '@' symbol
- **Placeholder support**: Use `[fieldName]` to reference other field values

```java
public record UserDto(
    @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "****")
    String name, // Results in "****[][]"
    
    @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "[name] it is me")
    String email, // Results in "john@[name] it is me.domain" if original is "john@company.com"
    
    @MaskMe(conditions = {MaskOnInput.class}, maskValue = "[id]-[genderId]")
    String displayName // Results in "123-M" if id=123 and genderId="M"
) {}
```

### Original Value Manipulation

When `maskValue` is blank/empty, converters can manipulate the original field value:

```java
public record FinancialDto(
    @MaskMe(conditions = {SensitiveDataCondition.class}, maskValue = "") // Empty mask value
    BigDecimal amount
) {}
```

**Result**: If `amount = 123.45`, it becomes `100.00` (rounded to nearest 50)

---

## Custom Converter
### 📋 Overview
The Masking Library provides a flexible converter system that allows you to **override or extend** default type conversion behavior. You can create custom converters for specific field types or field names, giving you full control over how masked values are generated.

### 🎯 Why Use Custom Converters?
- **Override default behavior** for specific field types
- **Add field-specific logic** based on field names
- **Implement business-specific masking rules**
- **Handle custom data types** not supported by default
- **Priority-based execution** ensures your converters run first

### 🔧 How It Works

```java
public class CustomNumberConverter implements Converter {
    
    @Override
    public boolean canConvert(Class<?> type) {
        return type == BigDecimal.class;
    }
    
    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue) {
        if (value.isBlank() && originalValue instanceof BigDecimal original) {
            // Custom manipulation logic
            return original.multiply(new BigDecimal("0.5")); // Half the value
        }
        return new BigDecimal(value);
    }
}
```

### [🔄 Scoped Converter Registry – Safe Usage Guide](documentation/Converter.md)

### Spring Integration Benefits

- **Dependency Injection**: Mask conditions can use `@Autowired` services
- **Auto-Configuration**: Automatic setup via `MaskingConfiguration`
- **Bean Management**: Spring manages condition lifecycle
- **Fallback Support**: Works in non-Spring environments

## 🛠 Advanced Usage

### Multiple Conditions

```java
public record SensitiveDataDto(
    @MaskMe(conditions = {AdminOnlyCondition.class, AuditLogCondition.class})
    String secretData,
    
    @MaskMe(conditions = {TimeBasedCondition.class, LocationBasedCondition.class})
    String locationData
) {}
```

### Custom Condition with Complex Logic

```java
public class RoleBasedCondition implements MaskCondition {
    
    private UserRole requiredRole;
    
    @Override
    public void setInput(Object input) {
        if (input instanceof UserRole) {
            this.requiredRole = (UserRole) input;
        }
    }
    
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        if (containingObject instanceof UserDto user) {
            return !user.getRoles().contains(requiredRole);
        }
        return true;
    }
}
```

### Using with Spring Boot Auto-Configuration

```java
@Configuration
public class MaskingConfig {
    // MaskProcessor is automatically configured as @Component
    // No manual bean configuration needed
}
```

## 🔧 Configuration

### Setting Default Mask Values

```java
@Component
public class MaskingInitializer {
    
    private final MaskProcessor processor;
    
    public MaskingInitializer(MaskProcessor processor) {
        this.processor = processor;
    }
    
    @PostConstruct
    public void initMasking() {
        processor.setConditionInput(AlwaysMaskCondition.class, true);
    }
}
```

## 📝 Examples

### Example 1: Basic Masking

```java
@RestController
public class UserController {
    
    private final UserService userService;
    private final MaskProcessor processor;
    
    public UserController(UserService userService, MaskProcessor processor) {
        this.userService = userService;
        this.processor = processor;
    }
    
    @GetMapping("/user/{id}")
    public UserDto getUser(@PathVariable Long id) {
        UserDto dto = userService.getUserDto(id);
        return processor.process(dto);
    }
}

// DTO
public record UserDto(
    @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "CONFIDENTIAL")
    String ssn
) {}
```

### Example 2: Conditional Masking with Request Parameters

```java
@RestController
public class UserController {
    
    private final UserService userService;
    private final MaskProcessor processor;
    
    public UserController(UserService userService, MaskProcessor processor) {
        this.userService = userService;
        this.processor = processor;
    }
    
    @GetMapping("/user/{id}/conditional")
    public UserDto getConditionalUser(@PathVariable Long id,
                                      @RequestParam boolean maskEmail,
                                      @RequestParam boolean maskPhone) {
        
        try {
            processor.setConditionInput(EmailMaskCondition.class, maskEmail);
            processor.setConditionInput(PhoneMaskCondition.class, maskPhone);
            
            UserDto dto = userService.getUserDto(id);
            return processor.process(dto);
        } finally {
            processor.clearInputs();
        }
    }
}
```

### Example 3: Complex Object Masking

```java
public record OrderDto(
    Long id,
    
    @MaskMe(conditions = {CustomerVisibleCondition.class})
    CustomerDto customer,
    
    @MaskMe(conditions = {PriceMaskCondition.class})
    BigDecimal totalPrice,
    
    List<@MaskMe(conditions = {ProductMaskCondition.class}) ProductDto> products // needs validation.
) {}
```

## ⚠️ Important Notes

### 1. Thread Safety
The library uses `ThreadLocal` for condition inputs, making it thread-safe for concurrent requests.

### 2. Memory Management
Always use `try-finally` to clear inputs:

```java
try {
    processor.setConditionInput(SomeCondition.class, input);
    return processor.process(dto);
} finally {
    processor.clearInputs();
}
```

### 3. Record Support
For Java Records, annotations must be placed on the record components:

```java
// ✓ Correct
public record UserDto(
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    String email
) {}

// ✗ Incorrect - won't work
public record UserDto(String email) {
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    public String email() {
        return email;
    }
}
```

### 4. Type Conversion

The library uses a modular converter system that automatically converts mask values to appropriate types:

| Field Type     | Mask Value      | Result                     | Converter Used       |
|----------------|-----------------|----------------------------|----------------------|
| `String`       | `"***"`         | `"***"`                    | PrimitiveConverter   |
| `LocalDate`    | `"1900-01-01"`  | `LocalDate.of(1900, 1, 1)` | DateTimeConverter    |
| `Integer`      | `"0"`           | `0`                        | NumberConverter      |
| `BigDecimal`   | `""` (blank)    | Rounded to nearest 50      | NumberConverter      |
| `UUID`         | `"uuid-string"` | `UUID.fromString(...)`     | SpecialTypeConverter |
| Custom Object  | Any             | `null` or reflection       | FallbackConverter    |

### 5. Supported Types

#### NumberConverter
- All numeric primitives and wrappers (byte, int, long, float, double)
- BigDecimal, BigInteger
- **Special**: BigDecimal with blank mask value rounds to nearest 50

#### DateTimeConverter  
- LocalDate, LocalDateTime, LocalTime
- Instant, ZonedDateTime, OffsetDateTime
- Year, YearMonth, MonthDay
- Legacy java.util.Date, java.sql types

#### SpecialTypeConverter
- UUID, URL, URI
- File, Path
- Enums (case-insensitive)
- Locale, Currency, Class
- Arrays (basic support)

#### PrimitiveConverter
- String, Character, Boolean
- Handles primitive and wrapper types
- **Special**: Field-specific logic for "name" and "email" fields
- **Name fields**: Appends "[][]" to mask values
- **Email fields**: Domain replacement when original contains '@'

## 🧪 Testing

### Unit Test Example

```java
@SpringBootTest
class MaskingTest {
    
    @Autowired
    private MaskProcessor processor;
    
    @Test
    public void testMaskingWithInput() {
        // Given
        UserDto original = new UserDto(1L, "John Doe", "john@email.com");
        
        try {
            // When
            processor.setConditionInput(MaskOnInput.class, "MaskMe");
            UserDto masked = processor.process(original);
            
            // Then
            assertNotEquals(original, masked);
            assertEquals("[id]-[genderId]", masked.name());
            assertEquals("john@email.com", masked.email());
        } finally {
            processor.clearInputs();
        }
    }
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testMaskedEndpoint() throws Exception {
        mockMvc.perform(get("/users/masked/1")
                .header("Mask-Input", "MaskMe")
                .header("Mask-Phone", "YES"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.name").value("[id]-[genderId]"))
                .andExpected(jsonPath("$.phone").value("[PHONE_MASKED]"));
    }
}
```

## 🔍 Troubleshooting

### Common Issues

1. **Annotations not working on Records**
    - Ensure `@Target` includes `ElementType.RECORD_COMPONENT`
    - Annotations must be on record components, not accessor methods

2. **Processor called multiple times**
    - Check for duplicate `@MaskMe` annotations
    - Ensure you're not calling `process()` multiple times
    - Check for ResponseBodyAdvice interceptors

3. **Type conversion errors**
    - Provide valid mask values for the field type
    - For custom objects, consider returning `null`

4. **Memory leaks**
    - Always use `clearInputs()` in finally block
    - Don't store a processor instance as a bean with a scope other than singleton

## 📊 Performance Considerations

- **No caching**: The library doesn't cache reflection results to avoid memory leaks
- **Lightweight**: Minimal overhead for processing
- **Thread-local storage**: Condition inputs are stored per thread

## 📊 Performance & Best Practices

### Performance Optimizations

- **Switch Expressions**: Modern Java syntax for efficient type matching
- **Chain of Responsibility**: Early exit when converter found
- **ThreadLocal Management**: Proper cleanup prevents memory leaks
- **Spring Bean Caching**: Reuses managed instances when available

### Best Practices

1. **Use Spring Components**: Prefer `@Component` conditions for dependency injection
2. **Clear Inputs**: Always use try-finally blocks to clear ThreadLocal inputs
3. **Blank Mask Values**: Use empty strings to trigger original value manipulation
4. **Type Safety**: Leverage the converter system for automatic type conversion

```java
// ✅ Good - Spring managed with proper cleanup
@Component
public class BusinessLogicCondition implements MaskCondition {
    @Autowired private BusinessService service;
    
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return service.shouldMaskField(fieldValue);
    }
}

// ✅ Good - Proper controller usage
@GetMapping("/users")
public List<UserDto> getUsers(@RequestHeader("MaskMe-Input") String input) {
    try {
        processor.setConditionInput(MaskOnInput.class, input);
        return users.stream().map(processor::process).toList();
    } finally {
        processor.clearInputs(); // Always clear
    }
}
```

## 🔮 Future Enhancements

1. **Spring Boot Starter**: Auto-configuration support
2. **Jackson Integration**: Direct JSON serialization support  
3. **Custom Converter Registration**: Plugin system for custom converters
4. **Expression Language**: SpEL support in conditions
5. **Performance Caching**: Optional metadata caching
6. **Annotation Inheritance**: Support for inherited annotations
7. **Async Processing**: Non-blocking masking operations

## 📝 Changelog

### v1.0.0
- ✨ Basic annotation-based masking, `@MaskMe` annotation.
- ✨ Record and class support
- ✨ ThreadLocal condition inputs
- ✨ **Spring Integration**: Full ApplicationContext support
- ✨ **Modular Converters**: Specialized converter architecture
- ✨ **Original Value Manipulation**: Transform values when mask is blank
- ✨ **Switch Expressions**: Modern Java syntax
- ✨ **Enhanced Type Support**: Comprehensive type conversion
- ✨ **Field-Specific Processing**: Context-aware masking based on field names
- ✨ **Enhanced Placeholder Support**: Dynamic field referencing with `[fieldName]`
- ✨ **Improved Converter Architecture**: ConverterFactory with 5-parameter convert method
- ✨ **Name Field Logic**: Automatic "[][]" appending for name fields
- ✨ **Email Domain Replacement**: Smart email masking with domain substitution
- ✨ **Updated Header Names**: Changed from "MaskMe-*" to "Mask-*" format
- ✨ **Enhanced Type Support**: Better BigDecimal, Instant, and complex type handling
- ✨ **Comprehensive Test Coverage**: Full test suite for all components

## 📄 License

This library is open-source and available under the MIT License.

---
**Happy Masking! 🔒**