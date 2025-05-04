package com.example.dummyapp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class DummyJob {


    public static void main(String[] args) {
        SparkConf spark = new SparkConf().setMaster("local[*]").setAppName("JD Word Counter");

        JavaSparkContext jsc = new JavaSparkContext(spark);



        jsc.close();
    }
}
