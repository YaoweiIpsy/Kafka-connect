package com.bfa.kafka.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.kafka.common.config.ConfigDef.Importance;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {
  String name() default "";
  Importance importance() default Importance.LOW;
  String document() default "";
  boolean required() default false;
}
