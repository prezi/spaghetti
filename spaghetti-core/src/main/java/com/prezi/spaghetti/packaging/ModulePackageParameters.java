package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;

import java.util.Collection;
import java.util.EnumSet;

public class ModulePackageParameters {
	public static final EnumSet<ModuleBundleElement> DEFAULT_ELEMENTS = EnumSet.of(ModuleBundleElement.javascript, ModuleBundleElement.resources);

	public final ModuleBundle bundle;
	public final Collection<String> prefixes;
	public final Collection<String> suffixes;
	public final EnumSet<ModuleBundleElement> elements;

	public ModulePackageParameters(ModuleBundle bundle, Collection<String> prefixes, Collection<String> suffixes) {
		this(bundle, prefixes, suffixes, DEFAULT_ELEMENTS);
	}

	public ModulePackageParameters(ModuleBundle bundle, Collection<String> prefixes, Collection<String> suffixes, EnumSet<ModuleBundleElement> elements) {
		this.bundle = bundle;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.elements = elements;
	}
}
