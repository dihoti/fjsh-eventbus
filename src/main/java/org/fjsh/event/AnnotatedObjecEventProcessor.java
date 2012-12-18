package org.fjsh.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.fjsh.event.annotation.EventExecutable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnnotatedObjecEventProcessor extends AbstractAnnotationEventProcessor {

	private Map<Object, Map<Class<?>, List<EventExecutable>>> registeredObjects = new HashMap<Object, Map<Class<?>, List<EventExecutable>>>();

	public void register(Object listener) {
		Map<Class<?>, List<EventExecutable>> executables = generateExecutables(listener);
		if (executables.isEmpty()) {
			log.debug("No executables registered for listener {} of type {}.", listener, listener.getClass());
		} else {
			registeredObjects.put(listener, executables);
			Integer number = 0;
			for (Object key : executables.keySet()) {
				number = number + executables.get(key).size();
			}
			log.debug("{} executables registered for listener {} of type {}.", new Object[] { number.toString(), listener, listener.getClass().toString() });
		}
	}

	public void deregister(Object listener) {
		registeredObjects.remove(listener);
		log.debug("Executables deregistered for listener {} of type {}.", listener, listener.getClass());
	}

	@Override
	protected Map<Class<?>, List<EventExecutable>> retrieveListeners() {
		Map<Class<?>, List<EventExecutable>> executables = new HashMap<Class<?>, List<EventExecutable>>();
		for (Object registered : registeredObjects.keySet()) {
			executables = mergeExecutables(executables, registeredObjects.get(registered));
		}
		return executables;
	}

}
