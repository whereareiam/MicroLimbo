package com.aerittopia.microlimbo.api.event.handshake;

import com.aerittopia.microlimbo.api.connection.Player;
import com.aerittopia.microlimbo.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HandshakeEvent extends Event {
	private final Player player;
}
