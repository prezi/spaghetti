package com.prezi.spaghetti.gradle

import org.gradle.language.base.Binary

/**
 * Created by lptr on 11/02/14.
 */
interface SpaghettiCompatibleJavaScriptBinary extends Binary {
	File getJavaScriptFile()
	File getSourceMapFile()
}
