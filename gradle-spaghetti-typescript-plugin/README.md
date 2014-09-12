Spaghetti TypeScript Gradle Plugin
==================================

## Basic usage

You can apply the Spaghetti TypeScript plugin by:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "com.prezi.spaghetti:gradle-spaghetti-typescript-plugin:1.4.1"
    }
}

apply plugin: "spaghetti-typescript"
```

It does the following things:

* Applies the [gradle-spaghetti-plugin](../gradle-spaghetti-plugin)
* Applies the [gradle-typescript-plugin](https://github.com/prezi/gradle-typescript-plugin)
* Sets Spaghetti's target language to `typescript`
* Configures the TypeScript plugin to include generated Spaghetti sources when compiling artifacts
