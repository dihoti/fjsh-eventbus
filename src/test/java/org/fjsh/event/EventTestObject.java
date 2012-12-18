package org.fjsh.event;

import lombok.Getter;

import org.fjsh.event.annotation.EventListener;
import org.fjsh.event.matcher.StartsWithMatcher;

@Getter
public class EventTestObject {

	private String startString;

	public void reset() {
		startString = null;
	}

	@EventListener(type = String.class, matcher = StartsWithMatcher.class, parameters = { "abc" })
	public void justAnotherMethod(String event) {
		this.startString = event;
	}

}
