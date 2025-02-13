#!/bin/bash

# Check if two arguments are passed
if [ "$#" -lt 1 ]; then
  echo "Usage: $0 [--compile]"
  echo "--compile: if set, will recompile the project before running."
  echo "PROFILE: The Spring profile to use (e.g., dev, uat, prod)"
  echo "EXAMPLE: ./run_backed.sh --compile PROFILE"
  exit 1
fi

# Check if the --compile flag is set
COMPILE=false
if [ "$1" = "--compile" ]; then
  COMPILE=true
  shift # Remove the flag from arguments
fi

# Assign arguments to variables
PROFILE=$1

# Stop any running instances of JWTAuthentication2-1.0-SNAPSHOT.jar
echo "Stopping any running instances of JWTAuthentication2-1.0-SNAPSHOT.jar..."
pkill -f 'JWTAuthentication2-1.0-SNAPSHOT.jar' || echo "No instances of JWTAuthentication2-1.0-SNAPSHOT.jar were running."

# Switch to Java 21
# echo "Switching to Java 21..."
#sudo update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java
#sudo update-alternatives --set javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac

#cd /Users/jameswang9311/projects/byHey/systems-team-repo/JWTAuthetication || exit
cd /home/ubuntu/git/projects/totluc/systems-team-repo/JWTAuthetication || exit

# Compile if --compile flag is set
if [ "$COMPILE" = true ]; then
    echo "Compiling the project..."
    mvn clean package -DskipTests
fi

echo "Running JWTAuthetication with profile: $PROFILE"
nohup java -jar target/JWTAuthentication2-1.0-SNAPSHOT.jar --spring.config.location=file:./configuration/ --spring.profiles.active="$PROFILE" &
