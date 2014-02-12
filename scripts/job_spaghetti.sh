#!/bin/bash -lex

# Sanitize Environment
# TODO: Better cleanup after failures
rm -f ${WORKSPACE}/.gradle/1.7/taskArtifacts/cache.properties.lock
rm -f ${HOME}/.config/chromium/SingletonLock

export JAVA_HOME=/usr/lib/jvm/java-7-oracle

if [ -e /etc/profile.d/gradle.sh ]; then
    . /etc/profile.d/gradle.sh
fi

export PATH=$PATH:/usr/share/haxe
export HAXE_STD_PATH=/usr/share/haxe/std
export HAXE_LIBRARY_PATH=/usr/lib/haxe

export DISPLAY=0.0.0.0:1

gradle clean uploadArchives --info --stacktrace --refresh-dependencies -Pmunit.kill-browser -Pmunit.browser=chromium-browser
