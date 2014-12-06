package com.prezi.spaghetti.ast;

import com.google.common.base.Preconditions;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;

public final class Location {
	public static final Location INTERNAL = new Location(ModuleDefinitionSource.fromString("internal", ""), 0, 0);

	private final ModuleDefinitionSource source;
	private final int line;
	private final int character;

	public Location(ModuleDefinitionSource source, int line, int character) {
		this.source = Preconditions.checkNotNull(source, "source");
		this.line = line;
		this.character = character;
	}

	public ModuleDefinitionSource getSource() {
		return source;
	}

	public int getLine() {
		return line;
	}

	public int getCharacter() {
		return character;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		if (character != location.character) return false;
		if (line != location.line) return false;
		if (!source.equals(location.source)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = source.hashCode();
		result = 31 * result + line;
		result = 31 * result + character;
		return result;
	}

	@Override
	public String toString() {
		return source + ":" + line + ":" + character;
	}
}
