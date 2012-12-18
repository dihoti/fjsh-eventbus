package org.fjsh.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

public class EventBus {

	private static final int NUMBER_OF_THREADS = 5;
	@Autowired
	private List<EventProcessor> processors = new ArrayList<EventProcessor>();
	private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public void submitEvent(final Object event, boolean asynchronous) {
		if (asynchronous) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					processEvent(event);

				}
			});
		} else {
			processEvent(event);
		}

	}

	private void processEvent(final Object event) {
		for (EventProcessor processor : processors) {
			processor.processEvent(event);
		}
	}

}
