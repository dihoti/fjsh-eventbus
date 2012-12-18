package org.fjsh.event.config;

import org.fjsh.event.EventBus;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;


public class EventHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class getBeanClass(Element element) {
		return EventBus.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		// this will never be null since the schema explicitly requires that a
		// value be supplied 
//		String pattern = element.getAttribute("pattern");
//		bean.addConstructorArg(pattern);
//		// this however is an optional property
//		String lenient = element.getAttribute("lenient");
//		if (StringUtils.hasText(lenient)) {
//			bean.addPropertyValue("lenient", Boolean.valueOf(lenient));
//		}
	}
}
