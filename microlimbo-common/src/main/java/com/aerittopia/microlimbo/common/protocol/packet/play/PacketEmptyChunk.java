package com.aerittopia.microlimbo.common.protocol.packet.play;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import lombok.Setter;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;

@Setter
public class PacketEmptyChunk implements PacketOut {

	private int x;
	private int z;

	@Override
	public void encode(ByteMessage message, Version version) {
		message.writeInt(x);
		message.writeInt(z);

		LongArrayBinaryTag longArrayTag = LongArrayBinaryTag.longArrayBinaryTag(new long[37]);
		CompoundBinaryTag tag = CompoundBinaryTag.builder()
				.put("MOTION_BLOCKING", longArrayTag).build();
		CompoundBinaryTag rootTag = CompoundBinaryTag.builder()
				.put("root", tag).build();
		message.writeNamelessCompoundTag(rootTag);

		byte[] sectionData = new byte[]{0, 0, 0, 0, 0, 0, 1, 0};
		message.writeVarInt(sectionData.length * 16);
		for (int i = 0; i < 16; i++) {
			message.writeBytes(sectionData);
		}

		message.writeVarInt(0);

		byte[] lightData = new byte[]{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, -1, -1, 0, 0};
		message.ensureWritable(lightData.length);
		message.writeBytes(lightData, 1, lightData.length - 1);
	}

}
