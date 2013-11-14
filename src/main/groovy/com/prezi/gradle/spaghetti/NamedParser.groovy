package com.prezi.gradle.spaghetti

import com.prezi.gradle.spaghetti.parse.Parser
import com.prezi.gradle.spaghetti.parse.ParserContext
import org.gradle.api.Named

/**
 * Created by lptr on 14/11/13.
 */
abstract class NamedParser extends Parser implements Named {
	final String name

	public NamedParser(String name, ParserContext context)
	{
		super(context)
		this.name = name
	}
}
