package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultProjectSourceSet extends AbstractNamedDomainObjectContainer<FunctionalSourceSet> implements ProjectSourceSet {
	public DefaultProjectSourceSet(Instantiator instantiator) {
        super(FunctionalSourceSet.class, instantiator);
    }

    @Override
    protected FunctionalSourceSet doCreate(String name) {
        return getInstantiator().newInstance(DefaultFunctionalSourceSet.class, name, getInstantiator());
    }
}
