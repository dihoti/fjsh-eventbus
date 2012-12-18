package org.fjsh.event.matcher;

import org.fjsh.event.EventMatcher;

public class StartsWithMatcher implements EventMatcher {

	private String start;

	public boolean match(Object event) {
		return (event != null && event instanceof String && ((String)event).startsWith(start));
	}

	public Object[] fit(Object event) {
		return new Object[] { event };
	}

	public void setParameter(String[] parameters) {
		if (parameters!=null && parameters.length>0){
			start = parameters[0];
		}
	}

}
