Spaghetti Demo
==============

This is a simple demo application. Check it out online: http://prezi.github.io/spaghetti/demo/

The application has three modules:

* `core` is a vanilla JS module
* `layout` is a Haxe module
* `text-renderer` is a TypeScript module
* `app` is simple "Hello World" web application using the above modules

# How to use it

You will need [Haxe](http://haxe.org), [Node JS](http://nodejs.org) and [TypeScript](http://www.typescriptlang.org) installed.

On Mac you can do this simply:

```bash
$ brew install haxe
$ brew install node
$ npm install -g typescript
```

Install the latest version of Spaghetti from the root of the Spaghetti repository:

```bash
$ cd ..
$ ./gradlew install
```

Then you can build the test application:

```bash
$ cd spaghetti-gradle-example
$ ../gradlew packWebApp
$ open app/build/webapp/index.html
```

![Demo application](http://i.imgur.com/uNd1VLT.jpg)
