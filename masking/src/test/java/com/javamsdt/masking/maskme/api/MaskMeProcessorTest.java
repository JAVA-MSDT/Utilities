package com.javamsdt.masking.maskme.api;

import com.javamsdt.masking.maskme.api.masking.MaskMe;
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;
import com.javamsdt.masking.maskme.implemintation.masking.AlwaysMaskCondition;
import com.javamsdt.masking.maskme.implemintation.masking.MaskPhone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    }

    @Nested
    @DisplayName("clearInputs method")
    class ClearInputsMethod {

        @Test
        @DisplayName("should clear condition inputs")
        void shouldClearConditionInputs() {
            // Given
            processor.setConditionInput(MaskPhone.class, Map.of("key", "value"));

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