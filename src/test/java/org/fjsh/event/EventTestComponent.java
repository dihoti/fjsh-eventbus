package org.fjsh.event;

import org.fjsh.event.annotation.EventListener;
import org.fjsh.event.matcher.StartsWithMatcher;
import org.fjsh.event.matcher.StringMatcherNullSafe;
import org.springframework.stereotype.Component;

@Component
// @Scope(value = "session")
public class EventTestComponent {

	private TestEvent simpleEvent;
	private String eventString;
	private String eventNullSafeString;
	private String startString;

	@EventListener(type = TestEvent.class)
	public void someMethod(TestEvent event) {
		this.simpleEvent = event;
	}

	@EventListener(type = TestEvent.class, matcher = StringMatcherNullSafe.class, parameters = { "einfach" })
	public void someOtherMethod(String event) {
		this.eventString = event;
	}

	@EventListener(type = TestEvent.class, matcher = StringMatcherNullSafeRefusing.class, parameters = { "nicht leer" })
	public void anotherMethod(String event) {
		this.eventNullSafeString = event;
	}

	@EventListener(type = String.class, matcher = StartsWithMatcher.class, parameters = { "abc" })
	public void justAnotherMethod(String event) {
		this.startString = event;
	}

	public TestEvent getSimpleEvent() {
		return simpleEvent;
	}

	public String getEventString() {
		return eventString;
	}

	public String getEventNullSafeString() {
		return eventNullSafeString;
	}

	public String getStartString() {
		return startString;
	}

}
