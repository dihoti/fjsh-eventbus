package org.fjsh.event.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fjsh.event.AbstractAnnotationEventProcessor;
import org.fjsh.event.EventMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class EventListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	@Autowired
	private AbstractAnnotationEventProcessor eventProcessor;

	private ApplicationContext applicationContext;

	private final Map<Class<?>, List<EventExecutable>> executables = new HashMap<Class<?>, List<EventExecutable>>();

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	public Object postProcessAfterInitialization(final Object bean, String beanName) {
		final Class<?> targetClass = AopUtils.getTargetClass(bean);
		ReflectionUtils.doWithMethods(targetClass, new MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				EventListener annotation = AnnotationUtils.getAnnotation(method, EventListener.class);
				if (annotation != null) {
					Assert.isTrue(void.class.equals(method.getReturnType()), "Only void-returning methods may be annotated with @Scheduled.");
					if (AopUtils.isJdkDynamicProxy(bean)) {
						try {
							// found a @Scheduled method on the target class for
							// this JDK proxy -> is it
							// also present on the proxy itself?
							method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
						} catch (SecurityException ex) {
							ReflectionUtils.handleReflectionException(ex);
						} catch (NoSuchMethodException ex) {
							throw new IllegalStateException(String.format("@Scheduled method '%s' found on bean target class '%s', " + "but not found in any interface(s) for bean JDK proxy. Either "
									+ "pull the method up to an interface or switch to subclass (CGLIB) " + "proxies by setting proxy-target-class/proxyTargetClass " + "attribute to 'true'",
									method.getName(), targetClass.getSimpleName()));
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
		return bean;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != this.applicationContext) {
			return;
		}

		// eventProcessor.setRegisteredListeners(executables);
		// Map<String, SpringBeanEventProcessor> processors =
		// applicationContext.getBeansOfType(SpringBeanEventProcessor.class);

	}

	public void destroy() throws Exception {

	}
}
