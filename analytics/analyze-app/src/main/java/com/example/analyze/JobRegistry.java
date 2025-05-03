package com.example.analyze;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JobRegistry {

    private List<JobMetaData> jobMetaDataList;

    @Autowired
    public JobRegistry(
            @Value("jobs.folder") String jobFolder
    ) {
        Path jobsFolder = Paths.get(jobFolder);
        try (Stream<Path> paths = Files.walk(jobsFolder)) {
            jobMetaDataList = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .map(this::loadJobMetadataFromJar)
                    .collect(Collectors.toList());

            int a = 10;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JobMetaData loadJobMetadataFromJar(Path jarPath) {
        YAMLMapper yamlMapper = new YAMLMapper();

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry("META-INF/job-info.yaml");
            if (entry != null) {
                InputStream is = jarFile.getInputStream(entry);
                JobMetaData metaData = yamlMapper.readValue(is, JobMetaData.class);

                return metaData;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read metadata from " + jarPath, e);
        }
        return null;
    }
}
