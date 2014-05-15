package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.Copy
import org.gradle.language.jvm.internal.SimpleStaleClassCleaner
import org.gradle.language.jvm.internal.StaleClassCleaner

/**
 * Created by lptr on 15/05/14.
 */
class ProcessSpaghettiResources extends Copy {
	@Override
	protected void copy() {
		StaleClassCleaner cleaner = new SimpleStaleClassCleaner(getOutputs())
		cleaner.setDestinationDir(getDestinationDir())
		cleaner.execute()
		super.copy()
	}
}
