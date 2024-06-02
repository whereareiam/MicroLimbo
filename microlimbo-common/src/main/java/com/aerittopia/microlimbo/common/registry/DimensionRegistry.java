/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aerittopia.microlimbo.common.registry;

import com.aerittopia.microlimbo.api.LimboServer;
import com.aerittopia.microlimbo.api.LoggingHelper;
import com.aerittopia.microlimbo.api.model.dimension.Dimension;
import com.aerittopia.microlimbo.api.model.dimension.DimensionInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Getter
@Singleton
public final class DimensionRegistry {
	private final LimboServer server;
	private final LoggingHelper loggingHelper;

	private DimensionInfo defaultDimension_Info_1_16;
	private DimensionInfo defaultDimension_Info_1_18_2;
	private DimensionInfo dimension_Info_1_20_5;
	// TODO Make by Version enum

	private CompoundBinaryTag codec_1_16;
	private CompoundBinaryTag codec_1_18_2;
	private CompoundBinaryTag codec_1_19;
	private CompoundBinaryTag codec_1_19_1;
	private CompoundBinaryTag codec_1_19_4;
	private CompoundBinaryTag codec_1_20;
	private CompoundBinaryTag oldCodec;
	// TODO Make by Version enum

	@Inject
	public DimensionRegistry(LimboServer server, LoggingHelper loggingHelper) {
		this.server = server;
		this.loggingHelper = loggingHelper;
	}

	public void load(Dimension dimension) {
		try {
			codec_1_16 = readCodecFile("/dimension/codec_1_16.snbt");
			codec_1_18_2 = readCodecFile("/dimension/codec_1_18_2.snbt");
			codec_1_19 = readCodecFile("/dimension/codec_1_19.snbt");
			codec_1_19_1 = readCodecFile("/dimension/codec_1_19_1.snbt");
			codec_1_19_4 = readCodecFile("/dimension/codec_1_19_4.snbt");
			codec_1_20 = readCodecFile("/dimension/codec_1_20.snbt");
			// On 1.16-1.16.1 different codec format
			oldCodec = readCodecFile("/dimension/codec_old.snbt");
		} catch (IOException e) {
			loggingHelper.severe("Cannot load dimension registry: " + e);
			return;
		}

		defaultDimension_Info_1_16 = getDefaultDimension(dimension, codec_1_16);
		defaultDimension_Info_1_18_2 = getDefaultDimension(dimension, codec_1_18_2);
		dimension_Info_1_20_5 = getModernDimension(dimension, codec_1_20);
	}

	private DimensionInfo getDefaultDimension(Dimension dimension, CompoundBinaryTag tag) {
		ListBinaryTag dimensions = tag.getCompound("minecraft:dimension_type").getList("value");

		CompoundBinaryTag overWorld = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(0)).get("element");
		CompoundBinaryTag nether = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(2)).get("element");
		CompoundBinaryTag theEnd = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(3)).get("element");

		return switch (dimension) {
			case OVERWORLD -> new DimensionInfo(0, dimension, overWorld);
			case THE_NETHER -> new DimensionInfo(-1, dimension, nether);
			case THE_END -> new DimensionInfo(1, dimension, theEnd);
		};
	}

	private DimensionInfo getModernDimension(Dimension dimension, CompoundBinaryTag tag) {
		return switch (dimension) {
			case OVERWORLD -> new DimensionInfo(0, dimension, tag);
			case THE_NETHER -> new DimensionInfo(2, dimension, tag);
			case THE_END -> new DimensionInfo(3, dimension, tag);
		};
	}

	private CompoundBinaryTag readCodecFile(String resPath) throws IOException {
		try (InputStream in = server.getClass().getResourceAsStream(resPath)) {
			if (in == null)
				throw new FileNotFoundException("Cannot find dimension registry file");

			return TagStringIO.get().asCompound(streamToString(in));
		}
	}

	private String streamToString(InputStream in) throws IOException {
		try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

			return bufReader.lines()
					.collect(Collectors.joining("\n"));
		}
	}
}
