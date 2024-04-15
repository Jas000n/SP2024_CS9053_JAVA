#!/bin/bash

# Run from root directory, like so:
# $ bash src/main/resources/Scripts/git-ignore-conf.sh

# tells git to ignore conf.properties file from now on
# note that since this is a tracked file, adding it to .gitignore doesn't work

git update-index --assume-unchanged src/main/resources/conf.properties

# to start tracking again, run the below
# git update-index --no-assume-unchanged src/main/resources/conf.properties
