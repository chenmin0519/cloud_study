package com.cm.cloud.commons.util;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 判断 并扔出异常
 */
public class Assert {

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new ParameterNotValidException(message);
        }
    }

    /**
     * Assert that an object is {@code null}.
     * <pre class="code">Assert.isNull(value, "The value must be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws ParameterNotValidException if the object is not {@code null}
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new ParameterNotValidException(message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws ParameterNotValidException if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ParameterNotValidException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">Assert.hasLength(name, "Name must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws ParameterNotValidException if the text is empty
     */
    public static void notBlank(String text, String message) {
        if (!org.apache.commons.lang3.StringUtils.isNotBlank(text)) {
            throw new ParameterNotValidException(message);
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ParameterNotValidException(message);
        }
    }

}
