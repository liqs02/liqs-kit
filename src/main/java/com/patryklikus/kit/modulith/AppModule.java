package com.patryklikus.kit.modulith;

import org.springframework.core.annotation.AliasFor;
import org.springframework.modulith.ApplicationModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@ApplicationModule
public @interface AppModule {
    @AliasFor(annotation = ApplicationModule.class)
    String[] allowedDependencies() default {};

    @AliasFor(annotation = ApplicationModule.class)
    String id() default "";

    @AliasFor(annotation = ApplicationModule.class)
    String displayName() default "";
}
