[<-- Back To Read Me file](../readME.md)

# 🔄 Scoped Converter Registry – Safe Usage Guide

The **Scoped Converter Registry** is an enhanced converter management system that provides **multiple isolation levels** for safe and flexible converter usage.

This system allows converters to be scoped to specific contexts, preventing cross-contamination and enabling safe testing, multi-tenant applications, and dynamic converter management.


## 📋 Overview

The Masking Library provides a flexible converter system that allows you to **override or extend** default type conversion behavior. You can create custom converters for specific field types or field names, giving you full control over how masked values are generated.

### 🎯 Why Use Custom Converters?
- **Override default behavior** for specific field types
- **Add field-specific logic** based on field names
- **Implement business-specific masking rules**
- **Handle custom data types** not supported by default
- **Priority-based execution** ensures your converters run first

### **Priority System**
- **Default converters**: Priority = 0
- **User converters**: Should use priority > 0
- **Higher priority executes first**
- The first matching converter that returns non-null wins

## 🎯 Why Scoped Converters?

- ✅ Tests run in isolation.
- ✅ Request-scoped converters.
- ✅ Clean scope management.
- ✅ Thread-local isolation.
- ✅ Clear scope boundaries.

## 🌐 Scope Hierarchy & Priority

```
┌─────────────────────────────────────────────┐
│           CONVERTER EXECUTION ORDER         │
├─────────────────────────────────────────────┤
│ 1. THREAD-SCOPED (Highest Priority)         │
│    • Background jobs                        │
│    • Test isolation                         │
│    • Thread-specific processing             │
│                                             │
│ 2. REQUEST-SCOPED (Web Applications)        │
│    • User-specific converters               │
│    • Session-based rules                    │
│    • Temporary request processing           │
│                                             │
│ 3. GLOBAL-SCOPED (Application-wide)         │
│    • Default application converters         │
│    • Third-party library converters         │
│    • Shared business rules                  │
│                                             │
│ 4. DEFAULT CONVERTERS (Lowest Priority)     │
│    • Built-in type converters               │
│    • Fallback converters                    │
└─────────────────────────────────────────────┘
```

## 🚀 Quick Start Guide
### 📝 Common Use Cases

### **1. Create Your Custom Converter**

### **Case 1: Override String Handling for Specific Fields**
```java
public class CustomEmailConverter implements Converter {
    
    @Override
    public int getPriority() {
        return 10; // Higher priority than defaults (0)
    }
    
    @Override
    public boolean canConvert(Class<?> type) {
        return type == String.class;
    }
    
    @Override
    public Object convert(String maskValue, Class<?> targetType,
                         Object originalValue, Object containingObject,
                         String fieldName) {
        
        // Only handle email fields
        if (!"email".equals(fieldName)) {
            return null; // Let other converters handle it
        }
        
        // Your custom email masking logic
        if (originalValue instanceof String email && email.contains("@")) {
            String[] parts = email.split("@");
            return parts[0].charAt(0) + "***@" + parts[1].charAt(0) + "***.com";
        }
        
        return "[EMAIL PROTECTED]";
    }
}
```

### **Case 1: Override String Handling for Specific Fields**
```java
public class SensitiveDataConverter implements Converter {
    
    @Override
    public int getPriority() {
        return 15;
    }
    
    @Override
    public boolean canConvert(Class<?> type) {
        return type == String.class;
    }
    
    @Override
    public Object convert(String maskValue, Class<?> targetType,
                         Object originalValue, Object containingObject,
                         String fieldName) {

        return switch (fieldName) {
            case "ssn" -> "XXX-XX-" + maskValue.substring(7);
            case "creditCard" -> maskValue.substring(0, 4) + "-****-****-" +
                    maskValue.substring(12);
            case "password" -> "********";
            default -> null; // Let the default converter handle others
        };
    }
}
```

### **Case 2: Custom Number Formatting**
```java
public class FinancialConverter implements Converter {
    
    @Override
    public int getPriority() {
        return 20;
    }
    
    @Override
    public boolean canConvert(Class<?> type) {
        return type == BigDecimal.class;
    }
    
    @Override
    public Object convert(String maskValue, Class<?> targetType,
                         Object originalValue, Object containingObject,
                         String fieldName) {
        
        if ("salary".equals(fieldName) || "amount".equals(fieldName)) {
            if (maskValue.isEmpty() && originalValue instanceof BigDecimal) {
                // Round original value when mask is empty
                return ((BigDecimal) originalValue)
                    .setScale(2, RoundingMode.HALF_UP);
            }
            return new BigDecimal("0.00");
        }
        
        return null; // Default converter handles other BigDecimal fields
    }
}
```

### **Case 3: Custom Date Formatting**
```java
public class CustomDateConverter implements Converter {
    
    @Override
    public int getPriority() {
        return 5;
    }
    
    @Override
    public boolean canConvert(Class<?> type) {
        return type == LocalDate.class;
    }
    
    @Override
    public Object convert(String maskValue, Class<?> targetType,
                         Object originalValue, Object containingObject,
                         String fieldName) {
        
        if ("birthDate".equals(fieldName)) {
            return LocalDate.of(1900, 1, 1); // Always use 1900-01-01 for birth dates
        }
        
        if ("hiringDate".equals(fieldName)) {
            return LocalDate.now().minusYears(1); // Show as hired 1 year ago
        }
        
        // For other date fields, use library default
        return null;
    }
}
```
### 🎪 Converter Chain Execution Flow

```
1. User calls MaskProcessor.process(dto)
   ↓
2. For each field with @Mask annotation:
   ↓
3. Check shouldMask() conditions
   ↓
4. If masking needed:
   ↓
5. Call ConverterRegistry.convertToFieldType()
   ↓
6. Execute converters by priority:
   ├── User Converter 1 (Priority: 20) ← First match wins!
   ├── User Converter 2 (Priority: 15)
   ├── User Converter 3 (Priority: 10)
   ├── PrimitiveConverter (Priority: 0)
   ├── NumberConverter (Priority: 0)
   ├── DateTimeConverter (Priority: 0)
   ├── SpecialTypeConverter (Priority: 0)
   └── FallbackConverter (Priority: 0) ← Last resort
   ↓
7. Return converted value to field
```

### 🚨 Important Rules

### **1. Priority Matters**
- **Higher priority** executes first
- The first converter that returns **non-null** wins
- Set priority > 0 to override defaults

### **2. Return null to Pass Control**
```java
@Override
public Object convert(...) {
    if (!shouldHandleThisField(fieldName)) {
        return null; // Let the next converter handle it
    }
    // Your logic here
}
```

### **3. Field Context Available**
You get access to:
- `fieldName` - Name of the field being processed
- `originalValue` - Original field value
- `containingObject` - The entire object being masked
- `maskValue` - The value from `@Mask(maskValue="...")`

### **2. Choose Your Scope & Register**

**Option A: Global Scope (Application-wide)**
```java
// Affects ALL threads and requests
ConverterRegistry.registerGlobal(new CustomEmailConverter());
```

**Option B: Thread-Local Scope (Thread isolation)**
```java
// Affects ONLY current thread
ConverterRegistry.registerThreadLocal(new CustomEmailConverter());
```

**Option C: Request Scope (Web requests)**
```java
// Affects ONLY current HTTP request
ConverterRegistry.startRequestScope("request-123");
ConverterRegistry.registerRequestScoped(new CustomEmailConverter());
```

## 🏗️ Usage by Framework

### **📦 Spring Boot Application**

#### **Configuration Class**

```java
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;
import org.springframework.context.annotation.Bean;

@Configuration
public class MaskingConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MaskingConfiguration.class);

    @Bean
    public MaskProcessor processor() {
        return new MaskProcessor();
    }

    @PostConstruct
    public void initializeConverters() {
        LOG.info("Initializing masking converters...");

        // ✅ SAFE: Clear global converters at startup
        ConverterRegistry.clearGlobal();

        // Register application-wide converters
        ConverterRegistry.registerGlobal(new CustomEmailConverter());
        ConverterRegistry.registerGlobal(new SensitiveDataConverter());
        ConverterRegistry.registerGlobal(new FinancialConverter());

        LOG.info("Registered {} global converters",
                ConverterRegistry.getRegisteredConvertersByScope().get("GLOBAL").size());
    }

    @PreDestroy
    public void cleanup() {
        LOG.info("Cleaning up masking converters...");

        // Clean up at shutdown to prevent memory leaks
        ConverterRegistry.clearGlobal();
        ConverterRegistry.clearThreadLocal();
    }
}
```

#### **Web Request Filter (Spring Web)**
```java
@Component
public class RequestScopeFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        String requestId = UUID.randomUUID().toString();
        
        // Start request scope
        ConverterRegistry.startRequestScope(requestId);
        
        // Add user-specific converters based on authentication
        if (request instanceof HttpServletRequest httpRequest) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                ConverterRegistry.registerRequestScoped(new UserAwareConverter(user));
            }
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            // ✅ SAFE: Clean up request scope
            ConverterRegistry.endRequestScope();
        }
    }
}
```

#### **REST Controller Example**

```java
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final MaskProcessor processor; // Declared as a bean in your configuration

    public UserController(MaskProcessor processor) {
        this.processor = processor;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        // Converters will automatically use:
        // 1. Request-scoped converters (user-specific)
        // 2. Global converters (application-wide)
        // 3. Default converters (built-in)

        User user = userService.findById(id);
        UserDto dto = userMapper.toDto(user);

        return processor.process(dto);
    }
}
```

### **☕ Regular Java Application**

#### **Command-Line Application**
```java
public class CommandLineApp {
    
    static void main(String[] args) {
        // Initialize converters
        ConverterRegistry.clearGlobal();
        ConverterRegistry.registerGlobal(new CustomEmailConverter());
        
        // Process data
        List<UserDto> users = loadUsers();
        MaskProcessor processor = new MaskProcessor();
        
        List<UserDto> maskedUsers = users.stream()
            .map(processor::process)
            .collect(Collectors.toList());
        
        // Clean up
        ConverterRegistry.clearGlobal();
    }
}
```

#### **Background Job Processing**
```java
public class BackgroundJobService {
    
    public void processBatchJob(List<UserDto> batch) {
        // Add job-specific converters (thread-local)
        ConverterRegistry.registerThreadLocal(new BatchJobConverter());
        
        try {
            MaskProcessor processor = new MaskProcessor();
            batch.forEach(processor::process);
            
            // Job-specific converters only affect this thread
        } finally {
            // ✅ SAFE: Clean up thread-local converters
            ConverterRegistry.clearThreadLocal();
        }
    }
}
```

### **⚡ Quarkus Application**

#### **Quarkus Configuration**
```java
@ApplicationScoped
public class MaskingInitializer {

    @Produces
    public MaskProcessor processor() {
        return new MaskProcessor();
    }
    
    @PostConstruct
    public void init() {
        // Quarkus handles hot reload - always clear first
        ConverterRegistry.clearGlobal();
        
        // Register converters
        ConverterRegistry.registerGlobal(new CustomEmailConverter());
        ConverterRegistry.registerGlobal(new QuarkusSpecificConverter());
    }
    
    @PreDestroy
    public void destroy() {
        ConverterRegistry.clearGlobal();
    }
}
```

#### **Quarkus REST Endpoint**
```java
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @Inject
    UserService userService;

    @Inject
    MaskProcessor processor; // Declared as a bean in your configuration
    
    @GET
    @Path("/{id}")
    public UserDto getUser(@PathParam("id") Long id,
                          @Context HttpServletRequest request) {
        
        // Start request scope for Quarkus
        ConverterRegistry.startRequestScope(request.getRequestId());
        
        try {
            // Add request-specific converters
            ConverterRegistry.registerRequestScoped(new UserContextConverter());
            
            User user = userService.findById(id);
            UserDto dto = convertToDto(user);
            
            return processor.process(dto);
        } finally {
            ConverterRegistry.endRequestScope();
        }
    }
}
```

## 🧪 Testing with Scoped Converters

### **JUnit 5 – Safe Test Isolation**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    private UserService userService;
    private MaskProcessor processor;
    
    @BeforeEach
    void setUp() {
        userService = new UserService();
        processor = new MaskProcessor();
        
        // ✅ SAFE: Thread-local converters for test isolation
        ConverterRegistry.clearThreadLocal();
        ConverterRegistry.registerThreadLocal(new TestEmailConverter());
        ConverterRegistry.registerThreadLocal(new TestPhoneConverter());
    }
    
    @AfterEach
    void tearDown() {
        // ✅ SAFE: Clean up only this thread's converters
        ConverterRegistry.clearThreadLocal();
    }
    
    @Test
    void testUserMasking() {
        UserDto user = new UserDto("test@example.com", "123-456-7890");
        UserDto masked = processor.process(user);
        
        assertThat(masked.email()).isEqualTo("[TEST-MASKED]");
        assertThat(masked.phone()).isEqualTo("[TEST-PHONE]");
    }
    
    @Test
    void testAnotherTest() {
        // This test has a CLEAN converter state 
        // Previous test's converters don't affect this one
    }
}
```

### **Spring Boot Test**
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        // Clear any previous converters
        ConverterRegistry.clearThreadLocal();
        
        // Register test-specific converters
        ConverterRegistry.registerThreadLocal(new IntegrationTestConverter());
    }
    
    @AfterEach
    void tearDown() {
        ConverterRegistry.clearThreadLocal();
    }
    
    @Test
    void getUser_shouldApplyTestConverters() throws Exception {
        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("[INTEGRATION-TEST]"));
    }
}
```

## 🔄 Advanced Scenarios

### **Multi-Tenant Application**
```java
@Component
public class TenantConverterManager {
    
    private final Map<String, List<Converter>> tenantConverters = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void loadTenantConverters() {
        // Load all tenant converters at startup
        tenantConverters.put("tenant-a", loadConvertersForTenant("tenant-a"));
        tenantConverters.put("tenant-b", loadConvertersForTenant("tenant-b"));
    }
    
    @Component
    public class TenantFilter implements Filter {
        
        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
            
            String tenantId = extractTenantId(request);
            
            // Start request scope
            ConverterRegistry.startRequestScope(request.getRequestId());
            
            // Add tenant-specific converters
            List<Converter> converters = tenantConverters.get(tenantId);
            if (converters != null) {
                converters.forEach(ConverterRegistry::registerRequestScoped);
            }
            
            try {
                chain.doFilter(request, response);
            } finally {
                ConverterRegistry.endRequestScope();
            }
        }
    }
}
```

### **Dynamic Converter Loading**
```java
@Service
public class DynamicConverterService {
    
    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    public void reloadConverters() {
        // Load fresh converters from database/config
        List<Converter> newConverters = loadLatestConverters();
        
        // Swap converters atomically
        synchronized (ConverterRegistry.class) {
            ConverterRegistry.clearGlobal();
            newConverters.forEach(ConverterRegistry::registerGlobal);
        }
        
        LOG.info("Reloaded {} converters", newConverters.size());
    }
}
```

### **Blue-Green Deployment**
```java
@Configuration
@Profile("blue")
public class BlueDeploymentConfig {
    
    @PostConstruct
    public void init() {
        ConverterRegistry.clearGlobal();
        ConverterRegistry.registerGlobal(new BlueDeploymentConverter());
        ConverterRegistry.registerGlobal(new CommonConverter());
    }
}

@Configuration
@Profile("green")
public class GreenDeploymentConfig {
    
    @PostConstruct
    public void init() {
        ConverterRegistry.clearGlobal();
        ConverterRegistry.registerGlobal(new GreenDeploymentConverter());
        ConverterRegistry.registerGlobal(new CommonConverter());
    }
}
```

## ⚠️ Safety Rules & Best Practices

### **DOs ✅**
```java
// ✅ SAFE: Clear global converters at startup
@PostConstruct
public void init() {
    ConverterRegistry.clearGlobal();
    registerApplicationConverters();
}

// ✅ SAFE: Use thread-local for tests
@BeforeEach
void setUp() {
    ConverterRegistry.clearThreadLocal();
    ConverterRegistry.registerThreadLocal(new TestConverter());
}

// ✅ SAFE: Request scope for web apps
public void handleRequest(HttpServletRequest request) {
    ConverterRegistry.startRequestScope(request.getId());
    try {
        // Process request
    } finally {
        ConverterRegistry.endRequestScope();
    }
}
```

### **DON'Ts ❌**
```java
// ❌ DANGEROUS: Clear global during request processing
public void someControllerMethod() {
    ConverterRegistry.clearGlobal(); // Breaks other requests!
}

// ❌ DANGEROUS: Forget to clean up
public void backgroundJob() {
    ConverterRegistry.registerThreadLocal(new JobConverter());
    // Forgot clearThreadLocal() - MEMORY LEAK!
}

// ❌ DANGEROUS: Mix scopes incorrectly
public void confusingMethod() {
    ConverterRegistry.registerGlobal(new Converter()); // Affects everyone
    ConverterRegistry.registerThreadLocal(new Converter()); // Affects only this thread
    // Which one will be used? Confusing!
}
```

## 🔍 Debugging & Monitoring

### **View Active Converters**
```java
// Get all converters by scope
Map<String, List<String>> converters = ConverterRegistry.getRegisteredConvertersByScope();

converters.forEach((scope, converterList) -> {
    System.out.println("Scope: " + scope);
    converterList.forEach(System.out::println);
});

// Output:
// Scope: GLOBAL
// CustomEmailConverter (Priority: 10)
// FinancialConverter (Priority: 20)
// 
// Scope: THREAD
// TestConverter (Priority: 100)
// 
// Scope: REQUEST
// UserAwareConverter (Priority: 15)
```

### **Monitor Scope Activity**
```java
@Aspect
@Component
public class ConverterMonitoringAspect {
    
    @Around("execution(* com.yourpackage..*.*(..))")
    public Object monitorConverterUsage(ProceedingJoinPoint joinPoint) throws Throwable {
        String scopeInfo = ConverterRegistry.getCurrentScopeInfo();
        LOG.debug("Converter scope before {}: {}", 
            joinPoint.getSignature().getName(), scopeInfo);
        
        Object result = joinPoint.proceed();
        
        LOG.debug("Converter scope after {}: {}", 
            joinPoint.getSignature().getName(), 
            ConverterRegistry.getCurrentScopeInfo());
        
        return result;
    }
}
```

## 🚨 Troubleshooting Guide

| Problem                       | Solution                                                                |
|-------------------------------|-------------------------------------------------------------------------|
| **Converter not executing**   | Check scope: Is converter registered in correct scope?                  |
| **Wrong converter executing** | Check priority: Higher priority converters execute first                |
| **Memory leak**               | Always call `clearThreadLocal()` in finally block                       |
| **Test contamination**        | Use `@BeforeEach` to clear thread-local converters                      |
| **Request scope not working** | Ensure `startRequestScope()` is called before `registerRequestScoped()` |
| **Performance issues**        | Limit converters in request/thread scope; prefer global                 |

## 📊 Scope Decision Tree

```
                           Start
                             │
                    ┌────────┴────────┐
                    │ Need converter? │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
    Affects all     Affects specific    Affects single
    application?        users?            request?
        │                    │                    │
        ▼                    ▼                    ▼
   ┌──────────┐       ┌────────────┐      ┌─────────────┐
   │  GLOBAL  │       │  REQUEST   │      │   THREAD    │
   │  SCOPE   │       │   SCOPE    │      │    SCOPE    │
   └──────────┘       └────────────┘      └─────────────┘
        │                    │                    │
   registerGlobal()   registerRequestScoped()  registerThreadLocal()
        │                    │                    │
   clearGlobal()      endRequestScope()      clearThreadLocal()
```

## 🎯 Summary

The **Scoped Converter Registry** provides:

1. **✅ Safe Isolation** – Converters don't leak between contexts
2. **✅ Memory Safety** – Proper cleanup prevents memory leaks
3. **✅ Flexible Deployment** – Works with Spring, Java SE, and Quarkus
4. **✅ Easy Testing** - Thread-local isolation for tests
5. **✅ Production Ready** - Battle-tested scope management

**Choose your scope wisely:**
- **Global**: Application defaults, shared libraries
- **Thread**: Tests, background jobs, isolated processing
- **Request**: Web applications, user-specific rules
- **Test**: JUnit test isolation

**Remember:** To always clean up your scope when done!

[<-- Back To Read Me file](../readME.md)