package com.javamsdt.masking.maskme.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.javamsdt.masking.maskme.implemintation.AlwaysMaskCondition;
import com.javamsdt.masking.maskme.implemintation.MaskPhone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaskProcessor Tests")
class MaskMeProcessorTest {

    private MaskProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new MaskProcessor();
    }

    @Nested
    @DisplayName("process method")
    class ProcessMethod {

        @Test
        @DisplayName("should process regular class with maskme annotations")
        void shouldProcessRegularClassWithMaskAnnotations() {
            // Given
            TestClass testClass = new TestClass("John", "john@test.com");

            // When
            TestClass result = processor.process(testClass);

            // Then
            assertNotNull(result);
            assertNotEquals(testClass, result);
            assertEquals("****[][]", result.getName()); // AlwaysMaskCondition returns true, PrimitiveConverter adds [][]
            assertEquals("john@test.com", result.getEmail()); // Not masked
        }

        @Test
        @DisplayName("should handle circular references")
        void shouldHandleCircularReferences() {
            // Given
            TestClass testClass = new TestClass("John", "john@test.com");

            // When
            TestClass result1 = processor.process(testClass);
            TestClass result2 = processor.process(result1); // Process the same object again

            // Then
            assertNotNull(result1);
            assertNotNull(result2);
        }

        @Test
        @DisplayName("should process nested objects recursively")
        void shouldProcessNestedObjectsRecursively() {
            // Given
            NestedTestClass nested = new NestedTestClass("Nested", new TestClass("John", "john@test.com"));

            // When
            NestedTestClass result = processor.process(nested);

            // Then
            assertNotNull(result);
            assertEquals("****[][]", result.getName()); // Masked with default value
            assertNotNull(result.getTestClass());
            assertEquals("****[][]", result.getTestClass().getName()); // Nested object also masked
        }
    }

    @Nested
    @DisplayName("setConditionInput method")
    class SetConditionInputMethod {

        @Test
        @DisplayName("should set condition input for processing")
        void shouldSetConditionInputForProcessing() {
            // Given
            TestRecordWithPhone record = new TestRecordWithPhone("John", "123-456-7890");
            processor.setConditionInput(MaskPhone.class, "YES");

            try (MockedStatic<MaskConditionFactory> factory = mockStatic(MaskConditionFactory.class)) {
                MaskPhone mockCondition = mock(MaskPhone.class);
                when(mockCondition.shouldMask(any(), any())).thenReturn(true);
                factory.when(() -> MaskConditionFactory.createCondition(MaskPhone.class))
                       .thenReturn(mockCondition);

                // When
                TestRecordWithPhone result = processor.process(record);

                // Then
                verify(mockCondition).setInput("YES");
            }
        }
    }

    @Nested
    @DisplayName("clearInputs method")
    class ClearInputsMethod {

        @Test
        @DisplayName("should clear condition inputs")
        void shouldClearConditionInputs() {
            // Given
            processor.setConditionInput(MaskPhone.class, "YES");

            // When
            processor.clearInputs();

            // Then - No exception should be thrown and inputs should be cleared
            assertDoesNotThrow(() -> processor.clearInputs());
        }
    }

    // Test helper classes
    private record TestRecord(
        @MaskMe(conditions = {AlwaysMaskCondition.class}) String name,
        String email
    ) {}

    private record TestRecordWithPhone(
        String name,
        @MaskMe(conditions = {MaskPhone.class}) String phone
    ) {}

    private static class TestClass {
        @MaskMe(conditions = {AlwaysMaskCondition.class})
        private String name;
        private String email;

        public TestClass() {}

        public TestClass(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    private static class NestedTestClass {
        @MaskMe(conditions = {AlwaysMaskCondition.class})
        private String name;
        private TestClass testClass;

        public NestedTestClass() {}

        public NestedTestClass(String name, TestClass testClass) {
            this.name = name;
            this.testClass = testClass;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TestClass getTestClass() { return testClass; }
        public void setTestClass(TestClass testClass) { this.testClass = testClass; }
    }
}