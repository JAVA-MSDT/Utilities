package com.javamsdt.masking.maskme.api;

import com.javamsdt.masking.maskme.api.masking.MaskMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("MaskMeException Tests")
class MaskMeExceptionTest {

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            // Given
            String message = "Test error message";
            RuntimeException cause = new RuntimeException("Root cause");

            // When
            MaskMeException exception = new MaskMeException(message, cause);

            // Then
            assertEquals(message, exception.getMessage());
            assertSame(cause, exception.getCause());
        }

        @Test
        @DisplayName("should create exception with null cause")
        void shouldCreateExceptionWithNullCause() {
            // Given
            String message = "Test error message";
            Throwable cause = null;

            // When
            MaskMeException exception = new MaskMeException(message, cause);

            // Then
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }
}