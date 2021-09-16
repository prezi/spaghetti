package com.prezi.spaghetti.gradle.internal.incubating;

import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultBinaryContainer extends DefaultPolymorphicDomainObjectContainer<Binary> implements BinaryContainer {
	public DefaultBinaryContainer(Instantiator instantiator, CollectionCallbackActionDecorator collectionCallbackActionDecorator) {
        super(Binary.class, instantiator, collectionCallbackActionDecorator);
    }
}
