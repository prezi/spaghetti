Spaghetti Demo
==============

This is a simple demo application. It has three modules:
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

Then you can build the test application:

```bash
$ gradle packWebApp
$ open app/build/webapp/index.html
```

You will see the following output on the JavaScript console:

```text
[Log] Text rendered with TextRenderer module: [Hello World] (application.js, line 90)
[Log] Internals: Internal implementation stuff here (application.js, line 91)
```
