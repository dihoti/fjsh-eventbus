package org.fjsh.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
// loader=TestWebSessionContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class EventPublisherTest {

	// @Autowired
	// private EventPublisher ep;
	@Autowired
	private EventTestComponent eventTestComponent;
	@Autowired
	private AnnotatedObjecEventProcessor objecEventProcessor;
	private EventTestObject eventTestObject = new EventTestObject();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFireObject() throws Exception {
		TestEvent event = new TestEvent(null);
		assertNull(eventTestComponent.getSimpleEvent());
		EventPublisher.fire(event);
		assertEquals(event, eventTestComponent.getSimpleEvent());
		assertEquals("einfach", eventTestComponent.getEventString());
		assertEquals(null, eventTestComponent.getEventNullSafeString());
		EventPublisher.fire(new TestEvent("blabla"));
		assertEquals("blabla", eventTestComponent.getSimpleEvent().getTestString());
		assertEquals("blabla", eventTestComponent.getEventString());
		assertEquals("blabla", eventTestComponent.getEventNullSafeString());
		EventPublisher.fire(new TestEvent(""), true);
		Thread.sleep(200);
		assertEquals("", eventTestComponent.getSimpleEvent().getTestString());
		assertEquals("", eventTestComponent.getEventString());
		assertEquals("nicht leer", eventTestComponent.getEventNullSafeString());
		EventPublisher.fire("aaaaaaa");
		assertEquals(null, eventTestComponent.getStartString());
		EventPublisher.fire("abcaaaaaa");
		assertEquals("abcaaaaaa", eventTestComponent.getStartString());
	}

	@Test
	public void testFireObjectBoolean() {
		EventPublisher.fire("abcaaaaaa");
		assertEquals("abcaaaaaa", eventTestComponent.getStartString());
		assertEquals(null, eventTestObject.getStartString());
		objecEventProcessor.register(eventTestObject);
		eventTestObject.reset();
		EventPublisher.fire("abcaaaaaa");
		assertEquals("abcaaaaaa", eventTestComponent.getStartString());
		assertEquals("abcaaaaaa", eventTestObject.getStartString());
		objecEventProcessor.deregister(eventTestObject);
		eventTestObject.reset();
		EventPublisher.fire("abcaaaaaa");
		assertEquals("abcaaaaaa", eventTestComponent.getStartString());
		assertEquals(null, eventTestObject.getStartString());
	}

}
