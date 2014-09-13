Spaghetti Command-Line Tool
===========================

You can build it like this, from the root of the repository:

```bash
$ ./gradlew installApp
$ export PATH=`pwd`/spaghetti/build/install/spaghetti/bin:$PATH
$ spaghetti version
Spaghetti version 2.0
```

### Usage

The Spaghetti command-line tool has a comprehensive help system (thanks to the awesome [Airline](https://github.com/airlift/airline) library):

```bash
$ spaghetti help
usage: spaghetti <command> [<args>]

The most commonly used spaghetti commands are:
    bundle     Create a module bundle.
    generate   Generate source code
    help       Display help information
    package    Package an application

See 'spaghetti help <command>' for more information on a specific command.
```

### Tutorial

Check out the [tutorial](wiki/tutorial) for a ste-by-step introduction to each Spaghetti command.
