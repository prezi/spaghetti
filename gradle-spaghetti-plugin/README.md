Spaghetti Gradle Plugin
=======================

## Basic usage

You can apply the Spaghetti plugin by:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-plugin:1.4.1"
    }
}

apply plugin: "spaghetti"
```

It does a number of things:

* Creates the `modules` configuration -- the main configuration to publish module bundles to
* Creates the `modulesObf` configuration to publish obfuscated module bundles to
* Adds the `spaghetti` extension to the build
* Specifies the default location for Spaghetti module definitions as `src/main/spaghetti`
* Adds a [`GenerateHeaders`](#generating-headers) task (see below) that looks for definitions in the default location
* If you are using a language that can generate Spaghetti compatible JavaScript (only the [Gradle Haxe plugin](https://github.com/prezi/gradle-haxe-plugin) is capable of this right now), it will also add a [`BundleModule`](#bundling-the-module) and an [`ObfuscateModule`](#obfuscation) task for each of these binaries
	* if you are working with a non-compatible language, you will have to create these tasks manually

### Generating headers

You can generate Haxe interfaces and proxies from dependent modules, and interfaces to implement from the current module:

```groovy
task generateHeaders(type: com.prezi.spaghetti.gradle.GenerateHeaders) {
	definition "Layout.module"
	language "haxe"
	outputDirectory "${buildDir}/spaghetti-module"
}
```

### Bundling the module

You can then implement these interfaces, and compile all your code to a JavaScript file. Now you only have to bundle your code into a Spaghetti-compatible module, and you're all set:

```groovy
task bundleModule(type: com.prezi.spaghetti.gradle.BundleModule) {
	// Depend on the compile task
	dependsOn compileHaxe
	// The module definition to include in the bundle
	definition "src/main/spaghetti/MyModule.module"
	// Wrap and bundle the compiled JS
	inputFile compileHaxe.outputFile
	outputFile "${buildDir}/mymodule.zip"
}
```

## Advanced topics

### Obfuscation


```groovy
task obfuscateModule(type: com.prezi.spaghetti.gradle.ObfuscateModule) {
	// Depend on the compile task
	dependsOn compileHaxe
	// The module definition to include in the bundle
	definition "src/main/spaghetti/MyModule.module"
	// Wrap and bundle the compiled JS
	inputFile compileHaxe.outputFile
	outputFile "${buildDir}/mymodule-obfuscated.zip"
}
```

### Generating an application

Build your application, and then bundle it for [RequireJS](http://requirejs.org/):

```groovy
task bundleApplication(type: com.prezi.spaghetti.gradle.BundleApplication) {
	dependsOn compileHaxe
	configuration configurations.modules
	language "haxe"
	inputFile compileHaxe.outputFile
	outputFile "${buildDir}/app.js"
}
```
