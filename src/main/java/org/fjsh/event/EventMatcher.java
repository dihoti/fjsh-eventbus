package org.fjsh.event;

public interface EventMatcher {

	boolean match(Object event);
	
	Object[] fit(Object event);

	void setParameter(String[] parameters);
	
}
