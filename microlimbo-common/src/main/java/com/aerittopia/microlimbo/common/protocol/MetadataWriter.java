package com.aerittopia.microlimbo.common.protocol;

import com.aerittopia.microlimbo.api.registry.Version;

@FunctionalInterface
public interface MetadataWriter {
	void writeData(ByteMessage message, Version version);
}
