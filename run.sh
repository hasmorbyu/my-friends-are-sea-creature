#!/usr/bin/env bash
# Runs the desktop build of "My Friends Are Sea Creatures".
# Usage:
#   ./run.sh              -> launches the graphical desktop game
#   ./run.sh --terminal   -> launches the text-only terminal mode
set -e
cd "$(dirname "$0")"

if [ "$1" == "--terminal" ] || [ "$1" == "-t" ]; then
    ./gradlew -q lwjgl3:terminal
else
    ./gradlew -q lwjgl3:run
fi
