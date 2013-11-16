**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Gradle Spaghetti Plugin](#gradle-spaghetti-plugin)
- [What is this?](#what-is-this)
	- [Workflow to write a module](#workflow-to-write-a-module)
	- [Workflow to use a module](#workflow-to-use-a-module)
- [Module System](#module-system)
- [Interface Language](#interface-language)
- [Versioning](#versioning)
- [Next steps](#next-steps)
	- [OOP-style](#oop-style)
	- [More like functional-style](#more-like-functional-style)
- [Challenges](#challenges)

Gradle Spaghetti Plugin
=======================

JS modularization prototype

# What is this?

This is a proof-of-concept implementation of a Gradle plugin that helps in modularizing JS applications. This is how it's supposed to work:

## Workflow to write a module

* write IDL
* choose an implementation language (only Haxe now, Typescript coming)
* generate interfaces: IDL->implementation language (use Gradle plugin)
* write implementation
* deploy artifacts to artifactory.prezi.com (use Gradle plugin)

## Workflow to use a module

* declare dependency on module in Gradle
* choose an implementation language (only Haxe now, Typescript coming)
* generate interfaces: IDL->implementation language (use Gradle plugin)
* generate client proxy code in the implementation language (use Gradle plugin)
* use client proxy code to access module code

# Module System

Reference-style: 
![Module System](http://prezi.github.io/gradle-spaghetti-plugin/images/Module System.png "Module System")

# Interface Language

Language defined in [ANTLR](http://antlr.org/), grammar can be reused without modification if needed in creating an Xtext editor.

```
/**
 * Layout module.
 */
module prezi.graphics.text.Layout {

    /**
     * Describes a block of text.
     */
    type Text {
        /**
         * Inserts the given String at <code>offset</code>.
         */
        void insert(int offset, String text)
        void delete(int offset, int end)
        String getRawText()
    }

    Text createText()
}
```

You can generate Haxe externs for your application to use this module:

```groovy
task generateClient(type: com.prezi.gradle.spaghetti.GenerateClient) {
	platform "haxe"
	outputDirectory "$buildDir/spaghetti-client"
}

task compile(type: com.prezi.gradle.haxe.CompileHaxe) {
	targetPlatform "js"
	source "$buildDir/spaghetti-client"
	source "src/main/haxe"
}
```

Clients can then use this from another module something like this:

```haxe
class MyApp {
	public static function main() {
		var adder = AdderModule.createAdder();
		var result = adder.add(100, 200);
	}
}
```

# Versioning

![Versioning](http://prezi.github.io/gradle-spaghetti-plugin/images/Versioning.png "Versioning")


# Challenges

* Should we define objects as live things or just placeholders, and let services do everything?
	* Advantage of the former is that things work like OOP, so it's simple for everybody to understand.
	* Advantage of the latter is that things are simpler on the tooling side, and we can create APIs that work across platforms (think of Ruby calling a JS module, like with Cucumber). It also means that we can support non-OOP languages like Elm easier at the cost of clumsier clients in OOP languages.
* How to define FRP signals? Should they be first-class citizens of the DSL, or should we add methods to services like `subscribe(signal:Signal)`?
* How to publish constants and enums?
* How to define dependencies on other modules?
