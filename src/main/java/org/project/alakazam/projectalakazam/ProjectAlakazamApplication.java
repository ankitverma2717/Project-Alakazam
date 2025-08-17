package org.project.alakazam.projectalakazam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProjectAlakazamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectAlakazamApplication.class, args);
    }

}
