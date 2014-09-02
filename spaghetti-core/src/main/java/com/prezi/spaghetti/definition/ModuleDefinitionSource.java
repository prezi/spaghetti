package com.prezi.spaghetti.definition;

public class ModuleDefinitionSource {
	private final String location;
	private final String contents;

	public ModuleDefinitionSource(String location, String contents) {
		this.location = location;
		this.contents = contents;
	}

	public final String getLocation() {
		return location;
	}

	public final String getContents() {
		return contents;
	}

	@Override
	@SuppressWarnings("RedundantIfStatement")
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModuleDefinitionSource that = (ModuleDefinitionSource) o;

		if (contents != null ? !contents.equals(that.contents) : that.contents != null) return false;
		if (location != null ? !location.equals(that.location) : that.location != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = location != null ? location.hashCode() : 0;
		result = 31 * result + (contents != null ? contents.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ModuleDefinitionSource{" +
				"location='" + location + '\'' +
				'}';
	}
}
