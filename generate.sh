#!/bin/sh

if [ ! -d generated-data ]; then
    mkdir generated-data
fi 

python3 generation-scripts/generate_data.py
