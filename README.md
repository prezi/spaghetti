Spaghetti
=========

Type-safe APIs in JavaScript.

## What is this?

Spaghetti lets you create large-scale modular JavaScript applications with modules written in different compile-to-JS languages communicating with each other in a type-safe way.

## How does it work?

When you create a Spaghetti module, you start by defining the module's API in the Spaghetti [IDL](http://en.wikipedia.org/wiki/Interface_description_language). Something like this:

```
module com.example.module as MyModule

/**
 * A tool to greet guests.
 */
interface Greeter {

	/**
	 * Greets a guest and returns the greeting.
	 */
	string sayHello(string guest)
}

/**
 * Creates a Greeter.
 */
Greeter createGreeter()
```

From this definition Spaghetti can generate interfaces for its supported platforms (currently Haxe and TypeScript are available).

You then implement the generated interfaces, and compile your code into a JavaScript file. In Haxe you would write the following:

```haxe
package com.example.module;

// Spaghetti looks for a class called "<ModuleName>Impl"
class MyModuleImpl implements MyModule {
	public function createGreeter():Greeter {
		return new DefaultGreeter();
	}
}

class DefaultGreeter implements Greeter {
	public function sayHello(guest:String):String {
		return 'Hello ${guest}!';
	}
}
```

Once you have your module compiled into a single JS file, Spaghetti will wrap it into a [RequireJs](http://requirejs.org/)-compatible module, and bundle it into a ZIP file together with the original module definition (the IDL you wrote above). It is also possible to include resources (images, CSS etc.) in a module bundle.

When someone wants to use your module, they only need to provide Spaghetti with module definition (or the bundle ZIP that contains it). Spaghetti can then generate proxy classes that will allow caling your module in a type-safe way. To continue with the example, you could call your Haxe module from TypeScript:

```typescript
var greeter = com.example.module.MyModule.createGreeter();
console.log(greeter.sayHello("World"));
```

There is an example project under [in the source code of the plugin](tree/master/gradle-spaghetti-plugin/src/test/at).

## Gradle support

Spaghetti comes with a Gradle plugin that makes it very easy to integrate Spaghetti into your workflow.

Read more about in the [plugin's readme](gradle-spaghetti-plugin/README.md).
