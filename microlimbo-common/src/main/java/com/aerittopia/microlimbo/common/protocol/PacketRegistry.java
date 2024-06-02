package com.aerittopia.microlimbo.common.protocol;

import com.aerittopia.microlimbo.api.registry.Version;
import com.aerittopia.microlimbo.common.protocol.packet.Packet;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketRegistry {
	@Getter
	private final Version version;
	private final Map<Integer, Supplier<?>> packetsById = new HashMap<>();
	private final Map<Class<?>, Integer> packetIdByClass = new HashMap<>();

	public PacketRegistry(Version version) {
		this.version = version;
	}

	public Packet getPacket(int packetId) {
		Supplier<?> supplier = packetsById.get(packetId);
		return supplier == null ? null : (Packet) supplier.get();
	}

	public int getPacketId(Class<?> packetClass) {
		return packetIdByClass.getOrDefault(packetClass, -1);
	}

	public void register(int packetId, Supplier<?> supplier) {
		packetsById.put(packetId, supplier);
		packetIdByClass.put(supplier.get().getClass(), packetId);
	}
}
