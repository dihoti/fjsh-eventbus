package org.fjsh.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.fjsh.event.annotation.EventExecutable;
import org.fjsh.event.annotation.EventListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

@Slf4j
public abstract class AbstractAnnotationEventProcessor implements EventProcessor {
	private static final int NUMBER_OF_THREADS = 5;

	private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public AbstractAnnotationEventProcessor() {
		super();
	}

	@Override
	public boolean processEvent(final Object event) {
		Map<Class<?>, List<EventExecutable>> registeredListeners = retrieveListeners();
		boolean processedSuccessfully = true;
		final List<EventExecutable> executables = registeredListeners.get(event.getClass());
		if (executables != null) {
			for (final EventExecutable executable : executables) {
				try {
					if (executable.isAsynchronous()) {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								executable.execute(event);
							}
						});
					} else {
						executable.execute(event);
					}
				} catch (Throwable e) {
					log.error("Event processing failed!", e);
					processedSuccessfully = false;
				}
			}
		}
		return processedSuccessfully;
	}

	protected abstract Map<Class<?>, List<EventExecutable>> retrieveListeners();

	protected Map<Class<?>, List<EventExecutable>> generateExecutables(final Object bean) {
		final Map<Class<?>, List<EventExecutable>> executables = new HashMap<Class<?>, List<EventExecutable>>();
		final Class<?> targetClass = AopUtils.getTargetClass(bean);
		ReflectionUtils.doWithMethods(targetClass, new MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				EventListener annotation = AnnotationUtils.getAnnotation(method, EventListener.class);
				if (annotation != null) {
					Assert.isTrue(void.class.equals(method.getReturnType()), "Only void-returning methods may be annotated with @EventListener.");
					if (AopUtils.isJdkDynamicProxy(bean)) {
						try {
							method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
						} catch (SecurityException ex) {
							ReflectionUtils.handleReflectionException(ex);
						} catch (NoSuchMethodException ex) {
							throw new IllegalStateException(String.format("No method '%s' found on bean target class '%s'!", method.getName(), targetClass.getSimpleName()));
						}
					}
					Class<? extends EventMatcher> matcherClass = annotation.matcher();
					EventMatcher matcher;
					try {
						matcher = matcherClass.newInstance();
					} catch (InstantiationException e) {
						throw new IllegalStateException(e);
					}
					matcher.setParameter(annotation.parameters());
					EventExecutable executable = new EventExecutable(bean, method, matcher, annotation.asynchronous());
					Class<?> key = annotation.type();
					if (!executables.containsKey(key)) {
						executables.put(key, new ArrayList<EventExecutable>());
					}
					List<EventExecutable> typeExecutables = executables.get(key);
					typeExecutables.add(executable);
				}
			}
		});
		return executables;
	}

	protected Map<Class<?>, List<EventExecutable>> mergeExecutables(Map<Class<?>, List<EventExecutable>> a, Map<Class<?>, List<EventExecutable>> b) {
		Map<Class<?>, List<EventExecutable>> result = new HashMap<Class<?>, List<EventExecutable>>();
		for (Class<?> classA : a.keySet()) {
			List<EventExecutable> typeExecutables = new ArrayList<EventExecutable>();
			typeExecutables.addAll(a.get(classA));
			result.put(classA, typeExecutables);
		}
		for (Class<?> classB : b.keySet()) {
			if (!result.containsKey(classB)) {
				result.put(classB, new ArrayList<EventExecutable>());
			}
			List<EventExecutable> typeExecutables = result.get(classB);
			typeExecutables.addAll(b.get(classB));
		}
		return result;
	}

}