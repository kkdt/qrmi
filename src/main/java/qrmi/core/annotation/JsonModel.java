package qrmi.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonModel {

    /**
     * Custom attribute.
     *
     * @return
     */
    String source();

    /**
     *
     * @return
     * @see com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES
     */
    boolean failOnUnknownProperties() default false;

    /**
     *
     * @return
     * @see com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_INVALID_SUBTYPE
     */
    boolean failOnInvalidSubtype() default false;

    /**
     *
     * @return
     * @see com.fasterxml.jackson.databind.DeserializationFeature#USE_JAVA_ARRAY_FOR_JSON_ARRAY
     */
    boolean arraySafe() default true;

    /**
     *
     * @return
     * @see com.fasterxml.jackson.databind.DeserializationFeature#ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
     */
    boolean nullEmptyString() default true;
}

