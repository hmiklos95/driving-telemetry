package com.example.analyze;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class AnalyzeApplication {

    public static void main(String[] args) {

        new AnalyzeApplication().start();
    }

    private void start() {
        Path jobsFolder = Paths.get("/Users/miklos.herperger/Developer/driving-telemetry/analytics/algorithms/algorithm-speed/target/algorithm-speed-1.0.0.jar");
        try (Stream<Path> paths = Files.walk(jobsFolder)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .forEach(this::loadJobMetadataFromJar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadJobMetadataFromJar(Path jarPath) {
        YAMLMapper yamlMapper = new YAMLMapper();

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry("META-INF/job-info.yaml");
            if (entry != null) {
                InputStream is = jarFile.getInputStream(entry);
                JobMetaData metaData = yamlMapper.readValue(is, JobMetaData.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read metadata from " + jarPath, e);
        }
    }

}
