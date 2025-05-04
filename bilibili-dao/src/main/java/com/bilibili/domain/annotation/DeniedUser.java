package com.bilibili.domain.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeniedUser {
    String[] value() default {};
}
