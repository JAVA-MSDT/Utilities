# Field Masking

## 📋 Overview

The Field Masking is a lightweight, annotation-based solution for dynamically masking sensitive data in Java objects. It supports both regular Java classes and Java Records, with conditional masking based on runtime inputs.

## 🚀 Features

- ✅ **Annotation-based masking** - Simple `@Mask` annotation
- ✅ **Conditional masking** – Mask based on runtime conditions
- ✅ **Type-safe** - Handles various data types (String, LocalDate, custom objects)
- ✅ **Framework-agnostic** - Works with any Java project
- ✅ **Thread-safe** - Proper handling of concurrent requests
- ✅ **No modification of originals** – Returns new masked instances
- ✅ **Supports both Classes and Records**

## 📦 Installation
- Not there yet, but maybe in the future :) 
### Maven
```xml
<dependency>
    <groupId>com.masking</groupId>
    <artifactId>masking-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.masking:masking-library:1.0.0'
```

## 🎯 Core Components

### 1. `@Mask` Annotation

```java
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mask {
    Class<? extends MaskCondition>[] conditions();
    String maskValue() default "***";
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

### 1. Define Your DTO with `@Mask` Annotations

#### For Records:
```java
public record UserDto(
        @Mask(conditions = {AlwaysMaskCondition.class})
        Long id,
        
        @Mask(conditions = {MaskOnInput.class}, maskValue = "[USER_NAME]")
        String name,
        
        String email,
        
        @Mask(conditions = {AlwaysMaskCondition.class})
        String password,
        
        @Mask(conditions = {MaskPhone.class}, maskValue = "[PHONE_MASKED]")
        String phone,
        
        @Mask(conditions = {AlwaysMaskCondition.class})
        AddressDto address,
        
        @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "1900-01-01")
        LocalDate birthDate,
        
        String genderId,
        String genderName
) {}
```

#### For Regular Classes:
```java
public class User {
    @Mask(conditions = {MaskOnInput.class}, maskValue = "*****")
    private String name;
    
    @Mask(conditions = {AlwaysMaskCondition.class})
    private String email;
    
    @Mask(conditions = {AlwaysMaskCondition.class})
    private LocalDate birthDate;
    
    // Getters and setters
}
```

### 2. Implement Mask Conditions

#### Always Mask Condition:
```java
public class AlwaysMaskCondition implements MaskCondition {
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return true;
    }
}
```

#### Input-Based Condition:
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
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/masked/{id}")
    public UserDto getMaskedUserById(@PathVariable final Long id,
                                     @RequestHeader("Mask-Input") String maskInput,
                                     @RequestHeader("Mask-Phone") String maskPhone) {
        
        // Get the processor instance
        MaskProcessor processor = MaskProcessor.getInstance();
        
        // Set condition inputs from request headers
        processor.setConditionInput(MaskOnInput.class, maskInput);
        processor.setConditionInput(MaskPhone.class, maskPhone);
        
        try {
            // Get the original user and convert to DTO
            User user = userService.findUserById(id);
            UserDto userDto = userMapper.toDto(user);
            
            // Apply masking
            return processor.process(userDto);
        } finally {
            // Clear inputs to prevent memory leaks
            // Also, it is already cleared in finally in MaskProcessor
            processor.clearInputs();
        }
    }
}
```

## 🛠 Advanced Usage

### Multiple Conditions

```java
public record SensitiveDataDto(
    @Mask(conditions = {AdminOnlyCondition.class, AuditLogCondition.class})
    String secretData,
    
    @Mask(conditions = {TimeBasedCondition.class, LocationBasedCondition.class})
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
    
    @Bean
    public MaskProcessor maskProcessor() {
        return MaskProcessor.getInstance();
    }
}
```

## 🔧 Configuration

### Setting Default Mask Values

```java
// In your application configuration
@PostConstruct
public void initMasking() {
    MaskProcessor processor = MaskProcessor.getInstance();
    processor.setConditionInput(AlwaysMaskCondition.class, true);
}
```

## 📝 Examples

### Example 1: Basic Masking

```java
// Controller
@GetMapping("/user/{id}")
public UserDto getUser(@PathVariable Long id) {
    UserDto dto = userService.getUserDto(id);
    return MaskProcessor.getInstance().process(dto);
}

// DTO
public record UserDto(
    @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "CONFIDENTIAL")
    String ssn
) {}
```

### Example 2: Conditional Masking with Request Parameters

```java
@GetMapping("/user/{id}/conditional")
public UserDto getConditionalUser(@PathVariable Long id,
                                  @RequestParam boolean maskEmail,
                                  @RequestParam boolean maskPhone) {
    
    MaskProcessor processor = MaskProcessor.getInstance();
    processor.setConditionInput(EmailMaskCondition.class, maskEmail);
    processor.setConditionInput(PhoneMaskCondition.class, maskPhone);
    
    try {
        UserDto dto = userService.getUserDto(id);
        return processor.process(dto);
    } finally {
        processor.clearInputs();
    }
}
```

### Example 3: Complex Object Masking

```java
public record OrderDto(
    Long id,
    
    @Mask(conditions = {CustomerVisibleCondition.class})
    CustomerDto customer,
    
    @Mask(conditions = {PriceMaskCondition.class})
    BigDecimal totalPrice,
    
    List<@Mask(conditions = {ProductMaskCondition.class}) ProductDto> products
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
    @Mask(conditions = {AlwaysMaskCondition.class})
    String email
) {}

// ✗ Incorrect - won't work
public record UserDto(String email) {
    @Mask(conditions = {AlwaysMaskCondition.class})
    public String email() {
        return email;
    }
}
```

### 4. Type Conversion
The library automatically converts mask values to the appropriate type:

| Field Type     | Mask Value      | Result                     |
|----------------|-----------------|----------------------------|
| `String`       | `"***"`         | `"***"`                    |
| `LocalDate`    | `"1900-01-01"`  | `LocalDate.of(1900, 1, 1)` |
| `Integer`      | `"0"`           | `0`                        |
| Custom Object  | Any             | `null`                     |

## 🧪 Testing

### Unit Test Example

```java
@Test
public void testMaskingWithInput() {
    // Given
    UserDto original = new UserDto(1L, "John Doe", "john@email.com");
    MaskProcessor processor = MaskProcessor.getInstance();
    
    // When
    processor.setConditionInput(MaskOnInput.class, "MaskMe");
    UserDto masked = processor.process(original);
    
    // Then
    assertNotEquals(original, masked);
    assertEquals("[USER_NAME]", masked.name());
    assertEquals("john@email.com", masked.email());
    
    processor.clearInputs();
}
```

### Integration Test Example

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Test
    public void testMaskedEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/masked/1")
                .header("Mask-Input", "MaskMe")
                .header("Mask-Phone", "YES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("[USER_NAME]"))
                .andExpect(jsonPath("$.phone").value("[PHONE_MASKED]"));
    }
}
```

## 🔍 Troubleshooting

### Common Issues

1. **Annotations not working on Records**
    - Ensure `@Target` includes `ElementType.RECORD_COMPONENT`
    - Annotations must be on record components, not accessor methods

2. **Processor called multiple times**
    - Check for duplicate `@Mask` annotations
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

## 🔮 Future Enhancements
- Working on that :) 
1. **Spring Boot Starter**: Auto-configuration support
2. **Jackson Integration**: Direct JSON serialization support
3. **Annotation Inheritance**: Support for inherited annotations
4. **Expression Language**: SpEL support in conditions
5. **Performance caching**: Optional caching for reflection

## 📄 License

This library is open-source and available under the MIT License.

---
**Happy Masking! 🔒**