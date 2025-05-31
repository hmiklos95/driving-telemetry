package com.example.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hbase")
public class HbaseProperties {

    private String zookeeperQuorum;

    private String zookeeperClientPort;

    private String znodeParent;

}
