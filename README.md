Gradle Spaghetti Plugin
=======================

JS modularization prototype

# What is this?

This is a proof-of-concept implementation of a Gradle plugin that helps in modularizing JS applications. This is how it's supposed to work:

* You start with an interface definition (see below)
* You generate interfaces in the language of your choice (can be anything as long as it is Haxe, for now)
* Then you impelment them in your module
* When you want to use your module in another module/application, you take the interface definition and generate some classes/whathaveyou in the language of your choice (again, anything as long as it's Haxe for now)
* You use the generated client code to interact with your module

# Interface Language

Currently it's pretty rudimentary:

```groovy
module "AdderModule", {
	namespace "prezi.test"
	service "Adder", {
		define Int, "add", [a:Int, b:Int]
	}
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

# Next steps

Future plan for the interface language looks a bit more like this:

## OOP-style

```groovy
module prezi.text.LayoutModule {
	dependsOn prezi.network.NetworkModule
	type Text {
		define getRawText():String
		define insert(index:Int, text:Text, styles:Style[]):Void
		define delete(index:Int, length:Int):Void
		// ...
	}
	type Style {
		define getType():String
		define getValue():String
	}
	service Layout {
		define createText():Text
	}
}
```

...or...

## More like functional-style

```groovy
module prezi.text.LayoutModule {
	dependsOn prezi.network.NetworkModule
	types {
		Text
		Style
	}
	service Layout {
		define createText():Text
		define getRawText(object:Text):String
		define insertText(object:Text, index:Int, text:Text, styles:Style[]):Void
		define deleteText(object:Text, index:Int, length:Int)
		// ...
	}
}
```

# Challenges

* Should we define objects as live things or just placeholders, and let services do everything?
	* Advantage of the former is that things work like OOP, so it's simple for everybody to understand.
	* Advantage of the latter is that things are simpler on the tooling side, and we can create APIs that work across platforms (think of Ruby calling a JS module, like with Cucumber). It also means that we can support non-OOP languages like Elm easier at the cost of clumsier clients in OOP languages.
* How to define FRP signals? Should they be first-class citizens of the DSL, or should we add methods to services like `subscribe(signal:Signal)`?
* How to publish constants and enums?
* How to define dependencies on other modules?
