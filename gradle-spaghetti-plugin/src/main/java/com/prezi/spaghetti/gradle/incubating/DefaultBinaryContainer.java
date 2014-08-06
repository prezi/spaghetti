package com.prezi.spaghetti.gradle.incubating;

import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultBinaryContainer extends DefaultPolymorphicDomainObjectContainer<Binary> implements BinaryContainer {
	public DefaultBinaryContainer(Instantiator instantiator) {
        super(Binary.class, instantiator);
    }
}
