Spaghetti Haxe Gradle Plugin
============================

For an example on how to use the plugin, see [the Spaghetti Gradle example here](../spaghetti-gradle-example). Gradle 2.0 or newer is required to use the plugin.

## Basic usage

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-haxe-plugin:<version>"
    }
}

apply plugin: "spaghetti-haxe"
```

For available version numbers, please check [the list of Spaghetti releases](/../../releases).

It does the following things:

* Applies the [Spaghetti plugin](../gradle-spaghetti-plugin)
* Applies the [Haxe plugin](https://github.com/prezi/gradle-haxe-plugin) without creating tasks
* Sets Spaghetti's target language to `haxe`
* Configures the `js` Haxe target platform
* Makes sure the all `test` configurations have access to dependent modules
* Configures the Haxe plugin to include generated Spaghetti sources when compiling and testing artifacts, and when creating source bundles
