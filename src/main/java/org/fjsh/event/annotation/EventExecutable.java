package org.fjsh.event.annotation;

import java.lang.reflect.Method;

import lombok.Getter;

import org.fjsh.event.EventMatcher;
import org.springframework.util.ReflectionUtils;

public class EventExecutable {

	private final Object target;
	private final Method method;
	private final EventMatcher matcher;
	@Getter
	private final boolean asynchronous;

	public EventExecutable(Object target, Method method, EventMatcher matcher, boolean asynchronous) {
		this.target = target;
		this.method = method;
		this.matcher = matcher;
		this.asynchronous = asynchronous;
	}

	public void execute(Object event) {
		if (matcher.match(event)) {
			ReflectionUtils.makeAccessible(this.method);
			ReflectionUtils.invokeMethod(method, target, matcher.fit(event));
		}
	}
}
