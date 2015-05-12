package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.internal.ModuleBundleElement;

import java.util.EnumSet;

public class ModulePackageParameters {
	public static final EnumSet<ModuleBundleElement> DEFAULT_ELEMENTS = EnumSet.of(ModuleBundleElement.JAVASCRIPT, ModuleBundleElement.RESOURCES);

	public final ModuleBundle bundle;
	public final Iterable<String> prefixes;
	public final Iterable<String> suffixes;
	public final EnumSet<ModuleBundleElement> elements;

	public ModulePackageParameters(ModuleBundle bundle, Iterable<String> prefixes, Iterable<String> suffixes) {
		this(bundle, prefixes, suffixes, DEFAULT_ELEMENTS);
	}

	public ModulePackageParameters(ModuleBundle bundle, Iterable<String> prefixes, Iterable<String> suffixes, EnumSet<ModuleBundleElement> elements) {
		this.bundle = bundle;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
		this.elements = elements;
	}
}
