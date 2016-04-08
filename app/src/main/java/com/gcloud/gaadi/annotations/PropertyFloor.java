package com.gcloud.gaadi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ankitgarg on 5/3/14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyFloor {
    int order();

    boolean trim() default true;

    int minLength() default 0;

    //String message()    default Constants.PROPERTY_FLOOR_ERROR_MESSAGE;
    int messageResId() default 0;
}
