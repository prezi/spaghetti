package com.prezi.spaghetti.packaging;

import com.google.common.collect.ImmutableList;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.internal.ModuleBundleElement;

import java.util.EnumSet;
import java.util.List;

public class ModulePackageParameters {
	public static final EnumSet<ModuleBundleElement> DEFAULT_ELEMENTS = EnumSet.of(ModuleBundleElement.JAVASCRIPT, ModuleBundleElement.RESOURCES);

	public final ModuleBundle bundle;
	public final List<String> prefixes;
	public final List<String> suffixes;
	public final EnumSet<ModuleBundleElement> elements;

	public ModulePackageParameters(ModuleBundle bundle, Iterable<String> prefixes, Iterable<String> suffixes) {
		this(bundle, prefixes, suffixes, DEFAULT_ELEMENTS);
	}

	public ModulePackageParameters(ModuleBundle bundle, Iterable<String> prefixes, Iterable<String> suffixes, EnumSet<ModuleBundleElement> elements) {
		this.bundle = bundle;
		this.prefixes = ImmutableList.copyOf(prefixes);
		this.suffixes = ImmutableList.copyOf(suffixes);
		this.elements = elements;
	}
}
