package com.example.dummyapp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class DummyJob {
    public static void main(String[] args) {
        SparkConf spark = new SparkConf().setAppName("JD Word Counter");

        JavaSparkContext jsc = new JavaSparkContext(spark);

        JavaRDD<Long> parallelize = jsc.parallelize(LongStream.range(0l, 1000000l).boxed().collect(Collectors.toList()));

        System.out.println(parallelize.count());

        jsc.close();
    }
}
