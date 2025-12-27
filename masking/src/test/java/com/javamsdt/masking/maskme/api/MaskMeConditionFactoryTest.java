package com.javamsdt.masking.maskme.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.javamsdt.masking.maskme.implemintation.AlwaysMaskCondition;
import com.javamsdt.masking.maskme.implemintation.MaskPhone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaskConditionFactory Tests")
class MaskMeConditionFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Nested
    @DisplayName("createCondition method")
    class CreateConditionMethod {

        @Test
        @DisplayName("should create condition using Spring context when available")
        void shouldCreateConditionUsingSpringContext() {
            // Given
            MaskConditionFactory.setApplicationContext(applicationContext);
            AlwaysMaskCondition expectedCondition = new AlwaysMaskCondition();
            when(applicationContext.getBean(AlwaysMaskCondition.class)).thenReturn(expectedCondition);

            // When
            AlwaysMaskCondition result = MaskConditionFactory.createCondition(AlwaysMaskCondition.class);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("should create condition using reflection when Spring context fails")
        void shouldCreateConditionUsingReflectionWhenSpringFails() {
            // Given
            MaskConditionFactory.setApplicationContext(applicationContext);
            when(applicationContext.getBean(AlwaysMaskCondition.class)).thenThrow(new RuntimeException("Bean not found"));

            // When
            AlwaysMaskCondition result = MaskConditionFactory.createCondition(AlwaysMaskCondition.class);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("should create condition using reflection when no Spring context")
        void shouldCreateConditionUsingReflectionWhenNoSpringContext() {
            // Given
            MaskConditionFactory.setApplicationContext(null);

            // When
            MaskPhone result = MaskConditionFactory.createCondition(MaskPhone.class);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("should throw exception when condition cannot be created")
        void shouldThrowExceptionWhenConditionCannotBeCreated() {
            // Given
            MaskConditionFactory.setApplicationContext(null);

            // When & Then
            assertThrows(MaskingException.class, () -> 
                MaskConditionFactory.createCondition(AbstractCondition.class));
        }
    }

    private abstract static class AbstractCondition implements MaskCondition {
        // Abstract class to test exception scenario
    }
}