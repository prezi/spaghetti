Spaghetti Haxe Gradle Plugin
============================

## Basic usage

You can apply the Spaghetti Haxe plugin by:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-haxe-plugin:1.4.1"
    }
}

apply plugin: "spaghetti-haxe"
```

It does the following things:

* Applies the [gradle-spaghetti-plugin](../gradle-spaghetti-plugin)
* Applies the [gradle-haxe-plugin](https://github.com/prezi/gradle-haxe-plugin) without creating tasks
* Sets Spaghetti's target language to `haxe`
* Creates the `js` Haxe target platform
* Makes sure the all `test` configurations have access to dependent modules
* Configures the Haxe plugin to include generated Spaghetti sources when compiling and testing artifacts, and when creating the source bundles
* Creates special tasks for compilation and MUnit testing that work with Spaghetti modules
