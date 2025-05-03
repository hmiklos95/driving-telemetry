package com.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class DriverSpeedJob {
    public static void main(String[] args) {

        SparkConf spark = new SparkConf().setMaster("local[*]").setAppName("JD Word Counter");

        JavaSparkContext jsc = new JavaSparkContext(spark);

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("zookeeper.znode.parent", "/hbase");
        config.set(TableInputFormat.INPUT_TABLE, "telemetry_data");

        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("data"));

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = jsc.newAPIHadoopRDD(
                config,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class
        );

        JavaRDD<Tuple2<String, Double>> driverSpeedRDD = hBaseRDD.map(tuple -> {
            Result result = tuple._2;
            String rowKey = Bytes.toString(result.getRow());

            String[] parts = rowKey.split("_", 2);
            String driverId = parts.length > 1 ? parts[1] : "unknown";

            byte[] speedBytes = result.getValue(Bytes.toBytes("data"), Bytes.toBytes("speed"));
            double speed = 0.0;
            if (speedBytes != null) {
                speed = Bytes.toDouble(speedBytes);
            }

            return new Tuple2<>(driverId, speed);
        });

        JavaRDD<Tuple2<String, Double>> exceedingSpeeds = driverSpeedRDD.filter(tuple -> tuple._2 > 3.2);

        JavaPairRDD<String, Integer> driverExceedCount = exceedingSpeeds
                .mapToPair(tuple -> new Tuple2<>(tuple._1, 1))
                .reduceByKey(Integer::sum);

        driverExceedCount.collect().forEach(entry ->
                System.out.println("Driver: " + entry._1 + ", Exceed count: " + entry._2)
        );

        jsc.close();
    }
}