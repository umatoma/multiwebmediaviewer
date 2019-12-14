SHELL=/bin/bash

run:
	ANDROID_HOME=$$HOME/Library/Android/sdk \
	JAVA_HOME=/Applications/"Android Studio.app"/Contents/jre/jdk/Contents/Home \
	PATH=$$JAVA_HOME/bin:$$PATH \
	npx appium

test:
	ANDROID_HOME=$$HOME/Library/Android/sdk \
	JAVA_HOME=/Applications/"Android Studio.app"/Contents/jre/jdk/Contents/Home \
	PATH=$$JAVA_HOME/bin:$$PATH \
	npx mocha --require ts-node/register index.ts