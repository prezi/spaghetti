Spaghetti Gradle Plugin
=======================

## Generating headers

You can generate Haxe interfaces and proxies from dependent modules, and interfaces to implement from the current module:

```groovy
task generateHeaders(type: com.prezi.spaghetti.gradle.GenerateHeaders) {
	definition "Layout.module"
	platform "haxe"
	outputDirectory "${buildDir}/spaghetti-module"
}
```

## Bundling the module

You can then implement these interfaces, and compile all your code to a JavaScript file. Now you only have to bundle your code into a Spaghetti-compatible module, and you're all set:

```groovy
task bundleModule(type: com.prezi.spaghetti.gradle.BundleModule) {
	dependsOn compileHaxe
	definition "Layout.module"
	inputFile compileHaxe.outputFile
	outputFile "${buildDir}/module.zip"
}
```

## Generating an application

Build your application, and then bundle it for [RequireJS](http://requirejs.org/):

```groovy
task bundleApplication(type: com.prezi.spaghetti.gradle.BundleApplication) {
	dependsOn compileHaxe
	configuration configurations.modules
	platform "haxe"
	inputFile compileHaxe.outputFile
	outputFile "${buildDir}/app.js"
}
```

## Extracting modules to be used with an application:

You can also extract all modules to a directory so that they are readily available to RequireJS:

```groovy
task packApplication(type: com.prezi.spaghetti.gradle.ExtractModules) {
	dependsOn bundleApplication
	configuration configurations.modules
	def testWebappDir = file("${buildDir}/webapp")
	outputDirectory testWebappDir
}
```
