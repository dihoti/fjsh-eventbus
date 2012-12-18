package org.fjsh.event;

public class DefaultEventMatcher implements EventMatcher {

	public boolean match(Object event) {
		return true;
	}

	public Object[] fit(Object event) {
		return new Object[] { event };
	}

	public void setParameter(String[] parameters) {
		// nothing to do
	}

}
