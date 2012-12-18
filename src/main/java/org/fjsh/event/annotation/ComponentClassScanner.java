package org.fjsh.event.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ClassUtils;

public class ComponentClassScanner<E> extends ClassPathScanningCandidateComponentProvider {

	public ComponentClassScanner() {
		super(false);
	}

	@SuppressWarnings("unchecked")
	public final Collection<?> getComponentClasses(String basePackage) {
		basePackage = basePackage == null ? "" : basePackage;
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (BeanDefinition candidate : findCandidateComponents(basePackage)) {
			try {
				Class cls = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
				classes.add((Class) cls);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		return classes;
	}
}