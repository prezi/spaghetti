Spaghetti TypeScript Gradle Plugin
==================================

For an example on how to use the plugin, see [the Spaghetti Gradle example here](../spaghetti-gradle-example). Gradle 2.0 or newer is required to use the plugin.

## Basic usage

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-typescript-plugin:<version>"
    }
}

apply plugin: "spaghetti-haxe"
```

The plugin configures the following things on the project:

* Applies the [Spaghetti plugin](../gradle-spaghetti-plugin)
* Applies the [TypeScript plugin](https://github.com/prezi/gradle-typescript-plugin)
* Sets Spaghetti's target language to `typescript`
* Configures the TypeScript plugin to include generated Spaghetti sources when compiling artifacts
