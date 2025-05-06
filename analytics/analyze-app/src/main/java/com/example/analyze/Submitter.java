package com.example.analyze;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClient;
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
    private final KubernetesClient client;

    @PostConstruct
    public void init() {
        extracted(jobRegistry.getJobMetaDataList().entrySet().stream().findFirst().get());
    }

    private void extracted(Map.Entry<String, JobMetaData> entry) {
        Map<String, Object> sparkApp = new HashMap<>();
        sparkApp.put("apiVersion", "sparkoperator.k8s.io/v1beta2");
        sparkApp.put("kind", "SparkApplication");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", entry.getValue().jobName);
        metadata.put("namespace", "default");
        sparkApp.put("metadata", metadata);

        Map<String, Object> spec = new HashMap<>();
        spec.put("type", "Java");
        spec.put("mode", "cluster");
        spec.put("image", "spark:3.5.5");
        spec.put("imagePullPolicy", "IfNotPresent");
        spec.put("mainClass", entry.getValue().mainClass);
        spec.put("mainApplicationFile", "local:///mnt/jars/" + entry.getKey());
        spec.put("sparkVersion", "3.5.5");

        Map<String, Object> volume = Map.of(
                "name", "job-jars",
                "hostPath", Map.of( // vagy pvc / configMap, stb.
                        "path", "/mnt/driving-telemetry/analytics/algorithms/algorithm-dummy/target/", // available on host
                        "type", "Directory"
                )
        );

        Map<String, Object> volumeMount = Map.of(
                "name", "job-jars",
                "mountPath", "/mnt/jars" // available in container
        );

        // volumes
        spec.put("volumes", List.of(volume));

        Map<String, Object> driver = new HashMap<>();
        driver.put("cores", 1);
        driver.put("memory", "2g");
        driver.put("serviceAccount", "spark-operator-spark");
        driver.put("volumeMounts", List.of(volumeMount));

        Map<String, String> driverLabels = new HashMap<>();
        driverLabels.put("version", "3.5.5");
        driver.put("labels", driverLabels);

        spec.put("driver", driver);

        Map<String, Object> executor = new HashMap<>();
        executor.put("instances", 2);
        executor.put("cores", 1);
        executor.put("memory", "2g");
        executor.put("volumeMounts", List.of(volumeMount));

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
    }
}
