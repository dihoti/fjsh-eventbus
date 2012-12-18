package org.fjsh.event;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

	private static EventPublisher	publisher;

	@PostConstruct
	public void init() {
		publisher = this;
	}

	@Autowired
	private EventBus	eventHandler;

	public static void fire(Object event) {
		fire(event, false);
	}

	public static void fire(Object event, boolean asynchronously) {
		publisher.eventHandler.submitEvent(event, asynchronously);
	}
}
