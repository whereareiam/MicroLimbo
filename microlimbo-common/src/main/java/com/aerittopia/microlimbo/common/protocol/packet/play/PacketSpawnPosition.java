package com.aerittopia.microlimbo.common.protocol.packet.play;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PacketSpawnPosition implements PacketOut {
	private long x;
	private long y;
	private long z;

	private static long encodePosition(long x, long y, long z) {
		return ((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF);
	}

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeLong(encodePosition(x, y, z));
		message.writeFloat(0);
	}
}
