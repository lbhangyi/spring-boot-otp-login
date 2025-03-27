package com.codingwithtashi.springsecurityjwt.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LongRunningExecution {
    boolean request() default false;
    boolean response() default false;
    long maxExecutionTime() default -1L;
}
