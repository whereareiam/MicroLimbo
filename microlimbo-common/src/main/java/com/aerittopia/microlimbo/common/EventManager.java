package com.aerittopia.microlimbo.common;

import com.aerittopia.microlimbo.api.event.Event;
import com.aerittopia.microlimbo.api.event.EventListener;
import com.aerittopia.microlimbo.api.event.order.Order;
import com.aerittopia.microlimbo.api.event.order.Priority;
import com.google.inject.Singleton;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
@SuppressWarnings("unchecked")
public class EventManager implements com.aerittopia.microlimbo.api.event.EventManager {
	private final Map<Class<? extends Event>, NavigableMap<Order, Set<EventListener<?>>>> listeners = new HashMap<>();
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	public <T extends Event> void registerListener(EventListener<T> listener) {
		Class<T> eventType = (Class<T>) ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Order order = Order.NORMAL;

			if (method.isAnnotationPresent(Priority.class)) {
				Priority priorityAnnotation = method.getAnnotation(Priority.class);
				order = priorityAnnotation.value();
			}

			listeners.computeIfAbsent(eventType, o -> new TreeMap<>())
					.computeIfAbsent(order, o -> new HashSet<>())
					.add(listener);
		}
	}

	public <T extends Event> void unregisterListener(EventListener<T> listener) {
		Class<T> eventType = (Class<T>) ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		NavigableMap<Order, Set<EventListener<?>>> priorityMap = listeners.get(eventType);

		if (priorityMap != null) {
			for (Set<EventListener<?>> eventListeners : priorityMap.values()) {
				eventListeners.remove(listener);
			}
		}
	}

	public <T extends Event> void fireEvent(T event) {
		NavigableMap<Order, Set<EventListener<?>>> priorityMap = listeners.get(event.getClass());

		if (priorityMap != null) {
			for (Order order : Order.values()) {
				Set<EventListener<?>> eventListeners = priorityMap.get(order);

				if (eventListeners != null) {
					for (EventListener<?> listener : eventListeners) {
						executorService.submit(() -> {
							if (event.isCancelled()) {
								return;
							}
							((EventListener<T>) listener).onEvent(event);
						});
					}
				}

				if (event.isCancelled()) {
					break;
				}
			}
		}
	}
}
