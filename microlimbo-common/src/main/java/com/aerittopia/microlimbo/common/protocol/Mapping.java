package com.aerittopia.microlimbo.common.protocol;

import com.aerittopia.microlimbo.api.registry.Version;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class Mapping {

	private final int packetId;
	private final Version from;
	private final Version to;

	public Mapping(int packetId, Version from, Version to) {
		this.from = from;
		this.to = to;
		this.packetId = packetId;
	}

	public static Mapping map(int packetId, Version from, Version to) {
		return new Mapping(packetId, from, to);
	}

	public Collection<Version> getRange() {
		Version curr = to;

		if (curr == from)
			return Collections.singletonList(from);

		List<Version> versions = new LinkedList<>();

		while (curr != from) {
			versions.add(curr);
			curr = curr.getPrev();
		}

		versions.add(from);

		return versions;
	}
}
