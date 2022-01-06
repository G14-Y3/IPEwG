#!/bin/sh
export EXE_NAME=IPEwG
export EXE_PATH=./build/bin
export JAR_NAME=IPEwG-1.0-SNAPSHOT.jar
export JAR_PATH=./build/libs
export JAR=$JAR_PATH/$JAR_NAME

echo "Clean previous builds"

# ./gradlew clean

echo "Compile and pack .jar"
# ./gradlew shadowJar

echo "Pack native-image"
native-image \
  --no-fallback \
  -cp "$JAR" \
  -Djava.library.path=./lib/libtorch/lib \
  -H:+StaticExecutable \
  -H:Class="ImageProcessorKt" \
  -H:Name="$EXE_NAME" \
  -H:Path="$EXE_PATH" \
  -H:ResourceConfigurationFiles=./resource-config.json \
  --libc \
  --static \
  --target \
  -jar "$JAR"

