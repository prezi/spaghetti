package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Preconditions;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;

public final class DefaultLocation implements Location {
	public static final Location INTERNAL = new DefaultLocation(DefaultModuleDefinitionSource.fromString("internal", ""), 0, 0);

	private final ModuleDefinitionSource source;
	private final int line;
	private final int character;

	public DefaultLocation(ModuleDefinitionSource source, int line, int character) {
		this.source = Preconditions.checkNotNull(source, "source");
		this.line = line;
		this.character = character;
	}

	@Override
	public ModuleDefinitionSource getSource() {
		return source;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getCharacter() {
		return character;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		if (character != location.getCharacter()) return false;
		if (line != location.getLine()) return false;
		if (!source.equals(location.getSource())) return false;

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
