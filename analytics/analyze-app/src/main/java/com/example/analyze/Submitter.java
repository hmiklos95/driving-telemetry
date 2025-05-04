package com.example.analyze;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Submitter {

    private final JobRegistry jobRegistry;

    @PostConstruct
    public void init() {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            Map<String, Object> sparkApp = new HashMap<>();
            sparkApp.put("apiVersion", "sparkoperator.k8s.io/v1beta2");
            sparkApp.put("kind", "SparkApplication");

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("name", "spark-pi");
            metadata.put("namespace", "default");
            sparkApp.put("metadata", metadata);

            Map<String, Object> spec = new HashMap<>();
            spec.put("type", "Scala");
            spec.put("mode", "cluster");
            spec.put("image", "spark:3.5.5");
            spec.put("imagePullPolicy", "IfNotPresent");
            spec.put("mainClass", "org.apache.spark.examples.SparkPi");
            spec.put("mainApplicationFile", "local:///opt/spark/examples/jars/spark-examples.jar");
            spec.put("sparkVersion", "3.5.5");

            spec.put("arguments", List.of("5000"));

            Map<String, Object> driver = new HashMap<>();
            driver.put("cores", 1);
            driver.put("memory", "512m");
            driver.put("serviceAccount", "spark-operator-spark");

            Map<String, String> driverLabels = new HashMap<>();
            driverLabels.put("version", "3.5.5");
            driver.put("labels", driverLabels);

            spec.put("driver", driver);

            Map<String, Object> executor = new HashMap<>();
            executor.put("instances", 1);
            executor.put("cores", 1);
            executor.put("memory", "512m");

            Map<String, String> executorLabels = new HashMap<>();
            executorLabels.put("version", "3.5.5");
            executor.put("labels", executorLabels);

            spec.put("executor", executor);

            sparkApp.put("spec", spec);

            String json = Serialization.asJson(sparkApp);
            GenericKubernetesResource resource = Serialization.unmarshal(json, GenericKubernetesResource.class);

            CustomResourceDefinitionContext context = new CustomResourceDefinitionContext.Builder()
                    .withGroup("sparkoperator.k8s.io")
                    .withVersion("v1beta2")
                    .withScope("Namespaced")
                    .withPlural("sparkapplications")
                    .build();

            client.genericKubernetesResources(context)
                    .inNamespace("default")
                    .create(resource);
            System.out.println("SparkApplication created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
