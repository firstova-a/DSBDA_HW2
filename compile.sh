#!/bin/sh

echo "[Host] Compiling jar"
sbt clean compile assembly
