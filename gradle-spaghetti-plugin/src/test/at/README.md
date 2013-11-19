Spaghetti Demo
==============

This is a simple demo application. It has three modules:
* `layout` is for simple text handling
* `text-renderer` renders `Text` objects from the `layout` module into `String`s
* `app` is simple "Hello World" web application using these modules

# How to use it

```bash
$ gradle packApplication
$ open app/build/webapp/index.html
```

You will see the output on the JavaScript console:

```text
[Log] Text rendered with TextRenderer module: [Hello World] (application.js, line 90)
[Log] Internals: Internal implementation stuff here (application.js, line 91)
```

# Troubleshooting

You need the latest version of the Spaghetti Gradle plugin to build this. If it doesn't work, first try to install the plugin.

In the plugin's folder do:

```bash
$ gradle install
```

Now you should be able to build and run the demo.
