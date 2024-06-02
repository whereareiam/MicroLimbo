package com.aerittopia.microlimbo.common.protocol;

import com.aerittopia.microlimbo.api.registry.Version;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ProtocolMappings {
	private final Map<Version, PacketRegistry> registry = new HashMap<>();

	public PacketRegistry getRegistry(Version version) {
		return registry.getOrDefault(version, registry.get(Version.getMin()));
	}

	public void register(Supplier<?> packet, Mapping... mappings) {
		for (Mapping mapping : mappings) {
			for (Version ver : mapping.getRange()) {
				PacketRegistry reg = registry.computeIfAbsent(ver, PacketRegistry::new);
				reg.register(mapping.getPacketId(), packet);
			}
		}
	}
}