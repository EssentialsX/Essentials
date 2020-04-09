#!/bin/bash
mkdir -p .buildtools
pushd .buildtools

is_installed() {
    mvn dependency:get -q -Dartifact=$1 -DremoteRepositories=file://$HOME/.m2/repository 1>/dev/null 2>&1
    return $?
}

ensure_buildtools() {
    if [ ! -f "BuildTools.jar" ]; then
        echo "Downloading BuildTools..."
        if [[ "$OSTYPE" == "darwin"* ]] || [[ "$OSTYPE" == "msys"* ]]; then
            curl https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar -o BuildTools.jar
        else
            wget -O BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
        fi
    fi
}

run_buildtools() {
    ensure_buildtools
    # Check if env var isnt empty, then run with xmx flag
    if [ ! -z "$BUILDTOOLS_XMX" ]; then
        echo "BUILDTOOLS_XMX Environment variable found. Running BuildTools with -Xmx$BUILDTOOLS_XMX"
        java -Xmx$BUILDTOOLS_XMX -jar BuildTools.jar --rev $1
    else
        java -jar BuildTools.jar --rev $1
    fi
    if [ $? -ne 0 ]; then
        echo "Running BuildTools for CB $1 failed! Aborting."
        popd
        exit 255
    else
        echo "Successfully built version $1"
    fi
}

# Check CB 1.8
is_installed org.bukkit:craftbukkit:1.8-R0.1-SNAPSHOT
is_18=$? # 0 = present, 1 = not present

# Check CB 1.8.3
is_installed org.bukkit:craftbukkit:1.8.3-R0.1-SNAPSHOT
is_183=$?

if [ $is_18 -ne 0 ]; then
    echo "Installing CraftBukkit 1.8..."
    run_buildtools 1.8
else
    echo "CraftBukkit 1.8 installed; skipping BuildTools..."
fi

if [ $is_183 -ne 0 ]; then
    echo "Installing CraftBukkit 1.8.3..."
    run_buildtools 1.8.3
else
    echo "CraftBukkit 1.8.3 installed; skipping BuildTools..."
fi

popd
