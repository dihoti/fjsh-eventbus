package org.fjsh.event.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class EventNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("handler", new EventHandlerBeanDefinitionParser());
		registerBeanDefinitionParser("annotation-processor", new AnnotationProcessorBeanDefinitionParser());
	}

}
