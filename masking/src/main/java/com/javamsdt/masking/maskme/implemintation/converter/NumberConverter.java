/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation.converter;

import com.javamsdt.masking.maskme.api.converter.Converter;
import com.javamsdt.masking.maskme.api.converter.FieldAccessUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Converter for all numeric types including primitives, wrappers, and big numbers.
 * Provides special handling for BigDecimal with original value manipulation
 * when maskme value is blank or empty.
 * 
 * <p>Supported types:
 * - Primitive numbers: byte, short, int, long, float, double
 * - Wrapper classes: Byte, Short, Integer, Long, Float, Double
 * - Big numbers: BigInteger, BigDecimal
 * 
 * <p>Special BigDecimal behavior:
 * - Blank maskme value triggers rounding to nearest 50
 * - Example: 123.45 becomes 100.00, 175.30 becomes 200.00
 * 
 * <p>Use cases:
 * - Convert "0" to Integer.valueOf(0)
 * - Convert "123.45" to new BigDecimal("123.45")
 * - Round sensitive amounts when maskme is empty
 * - Handle numeric field masking in financial applications
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class NumberConverter implements Converter {

    private static final BigDecimal ROUNDER = new BigDecimal("50");

    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
        Byte.class, byte.class,
        Short.class, short.class,
        Integer.class, int.class,
        Long.class, long.class,
        Float.class, float.class,
        Double.class, double.class,
        BigInteger.class, BigDecimal.class
    );
    
    @Override
    public boolean canConvert(Class<?> type) {
        return SUPPORTED_TYPES.contains(type);
    }
    
    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName) {
        // Handle context placeholders if needed for future enhancements
        String processedValue = FieldAccessUtil.hasFieldPlaceholders(value) ?
            FieldAccessUtil.replaceFieldPlaceholders(value, containingObject) : value;
            
        // Handle null/blank maskme value - manipulate original value
        if (processedValue != null && processedValue.isBlank() && targetType == BigDecimal.class && originalValue instanceof BigDecimal original) {
            return roundToNearest50(original);
        }
        
        try {
            return switch (targetType.getName()) {
                case "java.lang.Byte", "byte" -> Byte.parseByte(processedValue);
                case "java.lang.Short", "short" -> Short.parseShort(processedValue);
                case "java.lang.Integer", "int" -> Integer.parseInt(processedValue);
                case "java.lang.Long", "long" -> Long.parseLong(processedValue);
                case "java.lang.Float", "float" -> Float.parseFloat(processedValue);
                case "java.lang.Double", "double" -> Double.parseDouble(processedValue);
                case "java.math.BigInteger" -> new BigInteger(processedValue);
                case "java.math.BigDecimal" -> handleBigDecimalConversion(processedValue, targetType, originalValue);
                default -> null;
            };
        } catch (NumberFormatException e) {
            return getDefaultValue(targetType);
        }
    }

    /**
     * Handles BigDecimal conversion with special blank maskme behavior.
     * When maskme value is blank, rounds original BigDecimal to nearest 50.
     * 
     * @param value the string maskme value
     * @param targetType the BigDecimal target type
     * @param originalValue the original BigDecimal value
     * @return converted BigDecimal or rounded original
     */
    private BigDecimal handleBigDecimalConversion(String value, Class<?> targetType, Object originalValue) {
        if (value.isBlank() && targetType == BigDecimal.class && originalValue instanceof BigDecimal original) {
            return roundToNearest50(original);
        }
        return new BigDecimal(value);
    }

    /**
     * Rounds BigDecimal value to the nearest multiple of 50.
     * Used for financial data masking to obscure exact amounts.
     * 
     * <p>Examples:
     * - 123.45 → 100.00
     * - 175.30 → 200.00
     * - 49.99 → 50.00
     * 
     * @param value the BigDecimal to round
     * @return rounded value or ZERO if input is null
     */
    private BigDecimal roundToNearest50(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.divide(ROUNDER, 0, RoundingMode.HALF_UP).multiply(ROUNDER);
    }
    
    /**
     * Provides default values for primitive numeric types when conversion fails.
     * Returns appropriate zero values for each primitive type.
     * 
     * @param type the primitive or wrapper numeric type
     * @return zero value for primitives, null for wrappers
     */
    private Object getDefaultValue(Class<?> type) {
        return switch (type.getName()) {
            case "byte" -> (byte) 0;
            case "short" -> (short) 0;
            case "int" -> 0;
            case "long" -> 0L;
            case "float" -> 0.0f;
            case "double" -> 0.0;
            default -> null;
        };
    }
}