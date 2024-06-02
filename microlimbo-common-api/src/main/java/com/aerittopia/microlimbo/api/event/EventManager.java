package com.aerittopia.microlimbo.api.event;

@SuppressWarnings("unused")
public interface EventManager {
	<T extends Event> void registerListener(EventListener<T> listener);

	<T extends Event> void unregisterListener(EventListener<T> listener);

	<T extends Event> void fireEvent(T event);
}
