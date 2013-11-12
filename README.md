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
