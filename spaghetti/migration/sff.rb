#!/usr/bin/env ruby
foo = ARGF.read
puts foo.gsub(/\/\*\*~SING /, '/*').gsub(/\/\*\*~LINE (.*?)\*\//, '//\1')
