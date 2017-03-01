package com.prezi.spaghetti.definition.internal;

import com.prezi.spaghetti.bundle.ModuleFormat;
import com.prezi.spaghetti.definition.EntityWithModuleMetaData;

import java.util.Comparator;

public class DefaultEntityWithModuleMetaData<T> implements EntityWithModuleMetaData<T> {
	private final T entity;
	private final ModuleFormat format;

	public DefaultEntityWithModuleMetaData(T entity, ModuleFormat format) {
		this.entity = entity;
		this.format = format;
	}

	@Override
	public T getEntity() {
		return entity;
	}

	@Override
	public ModuleFormat getFormat() {
		return format;
	}

	public static <S extends Comparable<? super S>> Comparator<EntityWithModuleMetaData<S>> mkComparator() {
		return new Comparator<EntityWithModuleMetaData<S>>() {
			@Override
			public int compare(EntityWithModuleMetaData<S> e1, EntityWithModuleMetaData<S> e2) {
				return e1.getEntity().compareTo(e2.getEntity());
			}
		};
	}

}
