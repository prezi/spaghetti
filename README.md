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

Future plan for the interface language looks a bit more like this:

```groovy
module LayoutModule {
	namespace "prezi.text"
	type Text {
	}
	type Style {
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
