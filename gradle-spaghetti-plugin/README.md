Spaghetti Gradle Plugin
=======================

For an example on how to use the plugin, see [the Spaghetti Gradle example here](../spaghetti-gradle-example). If you want to work with one of the supported languages, it's better to use the Spaghetti plugin through its language-specific descendants:

* the [Spaghetti TypeScript plugin](../gradle-spaghetti-typescript-plugin), or
* the [Spaghetti Haxe plugin](../gradle-spaghetti-haxe-plugin).

Gradle 2.0 or newer is required to use the plugins.

## Basic usage

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-plugin:<version>"
    }
}

apply plugin: "spaghetti"
```

For available version numbers, please check [the list of Spaghetti releases](/../../releases).

When applied to a project, the plugin configures a number of things:

* Creates the `modules` configuration -- the main configuration to publish module bundles to
* Creates the `modulesObf` configuration to publish obfuscated module bundles to
* Adds the `spaghetti` extension to the build
* Adds a [`GenerateHeaders`](#generating-headers) task (see below) that looks for definitions in `src/main/spaghetti`

When using either the [Spaghetti Haxe plugin](../gradle-spaghetti-haxe-plugin) or the [Spaghetti TypeScript plugin](../gradle-spaghetti-typescript-plugin), generated code will automatically be included in the compilation of your code, and a [BundleModule](#bundling-the-module) and an [ObfuscateModule](#obfuscation) task is automatically created. The bundled and obfuscated ZIPs are automatically added to the artifacts of the `modules` and `modulesObf` configurations, respectively.

## Extension

```groovy
spaghetti {
    // Pre-configure all Spaghetti tasks to use this language
    language <language>

    // Use this configuration to look for dependent modules and to publish bundles
    configuration <configuration>

    // Use this configuration to publish obfuscated bundles
    obfuscatedConfiguration <configuration>
}
```

## Tasks

### Generating Headers

```groovy
task generateHeaders(type: com.prezi.spaghetti.gradle.GenerateHeaders) {
    // The Spaghetti definition file
    definition <file>

    // The language to generate headers in
    language <language>

    // The configuration holding any dependent modules
    dependentModules <configuration>

    // Additional directly dependent modules
    additionalDirectDependentModules <Set<File>>

    // Additional transitive dependent modules
    additionalTransitiveDependentModules <Set<File>>

    // The location to generate headers into
    outputDirectory <directory>
}
```

### Generating Stubs

### Bundling the Module

```groovy
task bundleModule(type: com.prezi.spaghetti.gradle.BundleModule) {
    // The Spaghetti definition file
    definition <file>

    // The language of the module's implementation
    language <language>
    
    // The compiled JavaScript code of the module
    inputFile <file>

    // The configuration holding any dependent modules
    dependentModules <configuration>

    // Additional directly dependent modules
    additionalDirectDependentModules <Set<File>>

    // Additional transitive dependent modules
    additionalTransitiveDependentModules <Set<File>>

    // The location to create the bundle in
    outputDirectory <directory>
}
```

### Obfuscation

The `ObfuscateModule` task takes the same properties as `BundleModule`, and adds some more:

```groovy
task obfuscateModule(type: com.prezi.spaghetti.gradle.ObfuscateModule) {
    // Extern definition files for Closure (see below)
    closureExterns <files>
    
    // Additional symbols to protect during Closure compilation
    additionalSymbols <symbols>
    
    // Working directory for Closure compiler
    workDir <directory>
}
```

Read more here about [why and how externs can be useful](https://developers.google.com/closure/compiler/docs/api-tutorial3#externs).

### Packaging applications

```groovy
task packageApplication(type: com.prezi.spaghetti.gradle.BundleApplication) {
    // The configuration holding the application's modules
    dependentModules <configuration>

    // Additional directly dependent modules
    additionalDirectDependentModules <Set<File>>

    // Additional transitive dependent modules
    additionalTransitiveDependentModules <Set<File>>
    
    // Name of the main module of the application
    mainModule <name>
    
    // Whether or not to actually call mainModule.main()
    execute <true|false>
    
    // The name of the application (executable name is applicationName + '.js')
    applicationName <name>
    
    // The type of the packaging (see below)
    type <packaging>

    // The location to create the bundle in
    outputDirectory <directory>
}
```

Available packaging types are (case-insensitive):

* `node` or `commonjs` for CommonJS/NodeJS compatible executable
* `amd` or `requirejs` for AMD/RequireJS compatible executable
