#!/usr/bin/env bash

SPAGHETTI_BIN=../build/install/spaghetti/bin/spaghetti
ROOT_FOLDER=$1

# 1. Find all .module files except for scaffold templates
MODULES=$(find $ROOT_FOLDER -regex '[^{]*\.module')

for MODULE in $MODULES
do
  TMP_FILE=$(mktemp -t module)
  # 2. Translate all simple comments so they'll be saved
  sed 's/\/\*\([^*]\)/\/**~SING \1/g' $MODULE > $TMP_FILE
  sed 's/\/\/\([^*]\+\)$/\/**~LINE \1*\//g' $TMP_FILE > $MODULE
  # 3. Use Spaghetti to migrate
  $SPAGHETTI_BIN migrate --definition $MODULE > $TMP_FILE
  # 4. Convert all translated comments back to simple comments
  sed 's/\/\*\*~SING /\/*/g' $TMP_FILE > $MODULE
  sed 's/\/\*\*~LINE \(.*\)\*\/$/\/\/\1/g' $MODULE > $TMP_FILE
  # TMP_FILE ping pong to MODULE
  mv $TMP_FILE $MODULE
done

# 5. Print instructions for converting scaffold templates
echo "Don't forget to convert your code generators!"
