package com.javamsdt.masking.maskme.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FieldAccessUtil Tests")
class FieldAccessUtilTest {

    @Nested
    @DisplayName("hasFieldPlaceholders method")
    class HasFieldPlaceholdersMethod {

        @Test
        @DisplayName("should return true when value contains placeholders")
        void shouldReturnTrueWhenValueContainsPlaceholders() {
            // Given
            String value = "[name]@company.com";

            // When
            boolean result = FieldAccessUtil.hasFieldPlaceholders(value);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when value has no placeholders")
        void shouldReturnFalseWhenValueHasNoPlaceholders() {
            // Given
            String value = "***";

            // When
            boolean result = FieldAccessUtil.hasFieldPlaceholders(value);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when value is null")
        void shouldReturnFalseWhenValueIsNull() {
            // Given
            String value = null;

            // When
            boolean result = FieldAccessUtil.hasFieldPlaceholders(value);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("replaceFieldPlaceholders method")
    class ReplaceFieldPlaceholdersMethod {

        @Test
        @DisplayName("should replace single placeholder with field value")
        void shouldReplaceSinglePlaceholderWithFieldValue() {
            // Given
            String maskValue = "[name]@company.com";
            TestObject containingObject = new TestObject("John", "john@test.com");

            // When
            String result = FieldAccessUtil.replaceFieldPlaceholders(maskValue, containingObject);

            // Then
            assertEquals("John@company.com", result);
        }

        @Test
        @DisplayName("should replace multiple placeholders")
        void shouldReplaceMultiplePlaceholders() {
            // Given
            String maskValue = "[name]-[email]";
            TestObject containingObject = new TestObject("John", "john@test.com");

            // When
            String result = FieldAccessUtil.replaceFieldPlaceholders(maskValue, containingObject);

            // Then
            assertEquals("John-john@test.com", result);
        }

        @Test
        @DisplayName("should return original value when no placeholders")
        void shouldReturnOriginalValueWhenNoPlaceholders() {
            // Given
            String maskValue = "***";
            TestObject containingObject = new TestObject("John", "john@test.com");

            // When
            String result = FieldAccessUtil.replaceFieldPlaceholders(maskValue, containingObject);

            // Then
            assertEquals("***", result);
        }

        @Test
        @DisplayName("should return original value when containing object is null")
        void shouldReturnOriginalValueWhenContainingObjectIsNull() {
            // Given
            String maskValue = "[name]@company.com";
            TestObject containingObject = null;

            // When
            String result = FieldAccessUtil.replaceFieldPlaceholders(maskValue, containingObject);

            // Then
            assertEquals("[name]@company.com", result);
        }
    }

    @Nested
    @DisplayName("getFieldValue method")
    class GetFieldValueMethod {

        @Test
        @DisplayName("should get field value from regular class")
        void shouldGetFieldValueFromRegularClass() {
            // Given
            TestObject containingObject = new TestObject("John", "john@test.com");
            String fieldName = "name";

            // When
            Object result = FieldAccessUtil.getFieldValue(containingObject, fieldName);

            // Then
            assertEquals("John", result);
        }

        @Test
        @DisplayName("should get field value from record")
        void shouldGetFieldValueFromRecord() {
            // Given
            TestRecord containingObject = new TestRecord("John", "john@test.com");
            String fieldName = "name";

            // When
            Object result = FieldAccessUtil.getFieldValue(containingObject, fieldName);

            // Then
            assertEquals("John", result);
        }

        @Test
        @DisplayName("should return null when field not found")
        void shouldReturnNullWhenFieldNotFound() {
            // Given
            TestObject containingObject = new TestObject("John", "john@test.com");
            String fieldName = "nonExistentField";

            // When
            Object result = FieldAccessUtil.getFieldValue(containingObject, fieldName);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should return null when containing object is null")
        void shouldReturnNullWhenContainingObjectIsNull() {
            // Given
            TestObject containingObject = null;
            String fieldName = "name";

            // When
            Object result = FieldAccessUtil.getFieldValue(containingObject, fieldName);

            // Then
            assertNull(result);
        }
    }

    // Test helper classes
    private static class TestObject {
        private String name;
        private String email;

        public TestObject(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    private record TestRecord(String name, String email) {}
}