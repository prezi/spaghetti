#!/usr/bin/env ruby
foo = ARGF.read
puts foo.gsub(/\/\*([^*])/, '/**~SING \1').gsub(/\/\/(.*)/, '/**~LINE \1*/')
