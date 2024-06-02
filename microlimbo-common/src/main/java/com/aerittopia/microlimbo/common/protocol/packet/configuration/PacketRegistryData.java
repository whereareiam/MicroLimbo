package com.aerittopia.microlimbo.common.protocol.packet.configuration;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.ByteMessage;
import com.aerittopia.microlimbo.common.protocol.MetadataWriter;
import com.aerittopia.microlimbo.common.protocol.packet.PacketOut;
import com.aerittopia.microlimbo.common.registry.DimensionRegistry;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class PacketRegistryData implements PacketOut {

	private final DimensionRegistry dimensionRegistry;
	private MetadataWriter metadataWriter;

	@Inject
	public PacketRegistryData(DimensionRegistry dimensionRegistry) {
		this.dimensionRegistry = dimensionRegistry;
	}

	@Override
	public void encode(ByteMessage message, Version version) {
		if (metadataWriter != null) {
			if (version.moreOrEqual(Version.V1_20_5)) {
				metadataWriter.writeData(message, version);
				return;
			}
		}
		message.writeNamelessCompoundTag(dimensionRegistry.getCodec_1_20());
	}
}
