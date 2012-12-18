package org.fjsh.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fjsh.event.DefaultEventMatcher;
import org.fjsh.event.EventMatcher;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
	Class<?> type();

	Class<? extends EventMatcher> matcher() default DefaultEventMatcher.class;

	String[] parameters() default {};

	boolean asynchronous() default false;
}