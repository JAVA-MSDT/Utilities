package com.javamsdt.masking.maskme.implemintation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MaskPhone Tests")
class MaskMePhoneTest {

    @Nested
    @DisplayName("shouldMask method")
    class ShouldMaskMeMethod {

        @Test
        @DisplayName("should return true when flag is YES")
        void shouldReturnTrueWhenFlagIsYes() {
            // Given
            MaskPhone condition = new MaskPhone("YES");
            Object fieldValue = "123-456-7890";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when flag is TRUE")
        void shouldReturnTrueWhenFlagIsTrue() {
            // Given
            MaskPhone condition = new MaskPhone("TRUE");
            Object fieldValue = "123-456-7890";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when flag is yes in lowercase")
        void shouldReturnTrueWhenFlagIsYesLowercase() {
            // Given
            MaskPhone condition = new MaskPhone("yes");
            Object fieldValue = "123-456-7890";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when flag is NO")
        void shouldReturnFalseWhenFlagIsNo() {
            // Given
            MaskPhone condition = new MaskPhone("NO");
            Object fieldValue = "123-456-7890";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when flag is null")
        void shouldReturnFalseWhenFlagIsNull() {
            // Given
            MaskPhone condition = new MaskPhone();
            Object fieldValue = "123-456-7890";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("setInput method")
    class SetInputMethod {

        @Test
        @DisplayName("should set input when parameter is string")
        void shouldSetInputWhenParameterIsString() {
            // Given
            MaskPhone condition = new MaskPhone();
            String input = "YES";

            // When
            condition.setInput(input);
            boolean result = condition.shouldMask("phone", new Object());

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should not set input when parameter is not string")
        void shouldNotSetInputWhenParameterIsNotString() {
            // Given
            MaskPhone condition = new MaskPhone();
            Integer input = 123;

            // When
            condition.setInput(input);
            boolean result = condition.shouldMask("phone", new Object());

            // Then
            assertFalse(result);
        }
    }
}