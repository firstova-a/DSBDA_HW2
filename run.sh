#!/bin/sh

set -e

echo "--- CREATING INPUT FILES STAGE ---"
echo "[Spark] Delering JAR directory"
docker exec spark-master rm -r /root/jars/ || true

# echo "[Host] Compiling jar"
# sbt clean compile assembly

echo "[Hadoop] Checking input directory existence"
if docker exec namenode hdfs dfs -test -d /user/root/input/; then
    echo "[HDFS] Removing input directory"
    docker exec namenode hdfs dfs -rm -r /user/root/input/
    echo
fi

echo "[Host] Move data file to flume listen directory"
cp generated-data/data.csv input/data-$(date +%s).csv
cp generated-data/catalog.csv input/catalog-$(date +%s).csv

echo "[Spark] Creating directory for jars"
docker exec spark-master mkdir -p /root/jars/

echo "[Spark] Copying jar"
docker cp target/scala-2.12/hw2-assembly-0.1.0.jar spark-master:/root/jars/

echo "[Cassandra] Creating keyspace and table"
docker exec cassandra cqlsh -e "DROP TABLE IF EXISTS hw2.output;"
docker exec cassandra cqlsh -e "CREATE KEYSPACE IF NOT EXISTS hw2 WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };"
docker exec cassandra cqlsh -e "CREATE TABLE IF NOT EXISTS hw2.output (hour varint, origin text, destination text, number varint, PRIMARY KEY (hour, origin, destination));"

echo "--- SPARK APPLICATION RUNNING STAGE ---"
echo "[Spark] Submitting application"
docker exec spark-master /spark/bin/spark-submit --class hw2.Main --master spark://spark-master:7077 /root/jars/hw2-assembly-0.1.0.jar

echo "[Cassandra] Viewing cassandra output"
docker exec cassandra cqlsh -e "SELECT * FROM hw2.output;"

# -c com.datastax.driver.USE_NATIVE_CLOCK=false