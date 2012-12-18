package org.fjsh.event;

import org.fjsh.event.EventMatcher;


public class StringMatcherNullSafeRefusing implements EventMatcher {

	private String defaultString;

	public boolean match(Object event) {
		return (event != null && event instanceof TestEvent && ((TestEvent)event).getTestString()!=null);
	}

	public Object[] fit(Object event) {
		TestEvent test = (TestEvent) event;
		String ret = (test.getTestString().isEmpty()) ? defaultString : test.getTestString(); 
		return new Object[] { ret };
	}

	public void setParameter(String[] parameters) {
		if (parameters!=null && parameters.length>0){
			defaultString = parameters[0];
		}
	}

}
