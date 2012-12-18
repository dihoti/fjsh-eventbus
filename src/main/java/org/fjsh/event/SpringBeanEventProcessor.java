package org.fjsh.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.fjsh.event.annotation.EventExecutable;
import org.fjsh.event.annotation.EventListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

@Component
@Slf4j
public class SpringBeanEventProcessor extends AbstractAnnotationEventProcessor implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	private final List<String> relevantBeans = new ArrayList<String>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	@PostConstruct
	public void findRelevantBeans() {
		Set<String> beanNames = new LinkedHashSet<String>(applicationContext.getBeanDefinitionCount());
		beanNames.addAll(Arrays.asList(applicationContext.getBeanDefinitionNames()));
		for (String beanName : beanNames) {
			if (hasBeanAnnotation(beanName, EventListener.class)) {
				relevantBeans.add(beanName);
			}
		}

		for (String bean : relevantBeans) {
			log.debug(bean + "::: " + applicationContext.getType(bean));
		}
	}

	@Override
	protected Map<Class<?>, List<EventExecutable>> retrieveListeners() {
		Map<Class<?>, List<EventExecutable>> executables = new HashMap<Class<?>, List<EventExecutable>>();
		for (String beanName : relevantBeans) {
			Object bean = applicationContext.getBean(beanName);
			executables = mergeExecutables(executables, generateExecutables(bean));
		}
		return executables;
	}

	private boolean hasBeanAnnotation(String beanName, final Class type) {
		Class<?> targetClass = applicationContext.getType(beanName);
		final List<Boolean> hasAnno = new ArrayList<Boolean>();
		ReflectionUtils.doWithMethods(targetClass, new MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				if (AnnotationUtils.getAnnotation(method, type) != null) {
					hasAnno.add(true);
				}
			}
		});
		// return applicationContext.findAnnotationOnBean(beanName, type) !=
		// null;
		return !hasAnno.isEmpty();
	}

}
