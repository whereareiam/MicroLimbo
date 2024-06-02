package com.aerittopia.microlimbo.api.event;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

@Setter
@Getter
public class Event {
	private Component cancelReason;
	private boolean cancelled;
}
