package org.fjsh.event.matcher;

import org.fjsh.event.EventMatcher;
import org.fjsh.event.TestEvent;


public class StringMatcherNullSafe implements EventMatcher {

	private String defaultString;

	public boolean match(Object event) {
		return (event != null && event instanceof TestEvent);
	}

	public Object[] fit(Object event) {
		TestEvent test = (TestEvent) event;
		String ret = (test.getTestString()==null) ? defaultString : test.getTestString(); 
		return new Object[] { ret };
	}

	public void setParameter(String[] parameters) {
		if (parameters!=null && parameters.length>0){
			defaultString = parameters[0];
		}
	}

}
