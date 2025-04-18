package com.rumor.tracing.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    String value() default "";
    String module() default "";
    String description() default "";
} 