#!/bin/sh

echo "[Host] creating input directory for Flume"
if [ ! -d input ]; then
    mkdir input
fi
echo

./compile.sh
echo

./generate.sh
