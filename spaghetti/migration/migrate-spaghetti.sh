#!/usr/bin/env bash

SPAGHETTI_BIN=../build/install/spaghetti/bin/spaghetti
ROOT_FOLDER=$1

# 1. Find all .module files except for scaffold templates
MODULES=$(find $ROOT_FOLDER -regex '[^{]*\.module')

for MODULE in $MODULES
do
  TMP_FILE=$(mktemp -t module)
  # 2. Translate all simple comments so they'll be saved
  ./ffs.rb $MODULE > $TMP_FILE
  # 3. Use Spaghetti to migrate
  $SPAGHETTI_BIN migrate --definition $TMP_FILE > $MODULE
  # 4. Convert all translated comments back to simple comments
  ./sff.rb $MODULE > $TMP_FILE
  # TMP_FILE ping pong to MODULE
  mv $TMP_FILE $MODULE
done

# 5. Print instructions for converting scaffold templates
echo "Don't forget to convert your code generators!"
