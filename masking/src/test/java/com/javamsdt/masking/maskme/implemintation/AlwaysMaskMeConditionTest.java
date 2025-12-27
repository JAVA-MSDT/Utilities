package com.javamsdt.masking.maskme.implemintation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AlwaysMaskCondition Tests")
class AlwaysMaskMeConditionTest {

    @Nested
    @DisplayName("shouldMask method")
    class ShouldMaskMeMethod {

        @Test
        @DisplayName("should always return true regardless of field value")
        void shouldAlwaysReturnTrue() {
            // Given
            AlwaysMaskCondition condition = new AlwaysMaskCondition();
            Object fieldValue = "any value";
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when field value is null")
        void shouldReturnTrueWhenFieldValueIsNull() {
            // Given
            AlwaysMaskCondition condition = new AlwaysMaskCondition();
            Object fieldValue = null;
            Object containingObject = new Object();

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when containing object is null")
        void shouldReturnTrueWhenContainingObjectIsNull() {
            // Given
            AlwaysMaskCondition condition = new AlwaysMaskCondition();
            Object fieldValue = "test";
            Object containingObject = null;

            // When
            boolean result = condition.shouldMask(fieldValue, containingObject);

            // Then
            assertTrue(result);
        }
    }
}