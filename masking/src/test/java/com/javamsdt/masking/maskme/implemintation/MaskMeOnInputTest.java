package com.javamsdt.masking.maskme.implemintation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.javamsdt.masking.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaskOnInput Tests")
class MaskMeOnInputTest {

    @Mock
    private UserService userService;

    @Nested
    @DisplayName("shouldMask method")
    class ShouldMaskMeMethod {

        @Test
        @DisplayName("should return true when input is MaskMe")
        void shouldReturnTrueWhenInputIsMaskMe() {
            // Given
            MaskOnInput condition = new MaskOnInput(userService);
            condition.setInput("MaskMe");
            Object fieldValue = "sensitive data";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when input is maskme in lowercase")
        void shouldReturnTrueWhenInputIsMaskmeLowercase() {
            // Given
            MaskOnInput condition = new MaskOnInput(userService);
            condition.setInput("maskme");
            Object fieldValue = "sensitive data";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when input is not MaskMe")
        void shouldReturnFalseWhenInputIsNotMaskMe() {
            // Given
            MaskOnInput condition = new MaskOnInput(userService);
            condition.setInput("DontMask");
            Object fieldValue = "sensitive data";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when input is null")
        void shouldReturnFalseWhenInputIsNull() {
            // Given
            MaskOnInput condition = new MaskOnInput(userService);
            Object fieldValue = "sensitive data";
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
            MaskOnInput condition = new MaskOnInput(userService);
            String input = "MaskMe";

            // When
            condition.setInput(input);
            boolean result = condition.shouldMask("data", new Object());

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should not set input when parameter is not string")
        void shouldNotSetInputWhenParameterIsNotString() {
            // Given
            MaskOnInput condition = new MaskOnInput(userService);
            Integer input = 123;

            // When
            condition.setInput(input);
            boolean result = condition.shouldMask("data", new Object());

            // Then
            assertFalse(result);
        }
    }
}