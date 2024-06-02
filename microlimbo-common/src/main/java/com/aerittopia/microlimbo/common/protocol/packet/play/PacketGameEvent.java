package com.aerittopia.microlimbo.common.protocol.packet.play;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketGameEvent implements PacketOut {
	private byte type;
	private float value;

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeByte(type);
		message.writeFloat(value);
	}
}