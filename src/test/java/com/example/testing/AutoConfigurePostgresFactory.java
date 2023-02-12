package com.example.testing;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"NullableProblems", "resource"})
public final class AutoConfigurePostgresFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
                                                     List<ContextConfigurationAttributes> configAttributes) {
        if (!AnnotatedElementUtils.hasAnnotation(testClass, AutoConfigurePostgres.class)) {
            return null;
        }

        return (configurableApplicationContext, mergedContextConfiguration) -> {
            final var container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"));
            container.start();

            final var propertySource = new MapPropertySource("datasource", Map.ofEntries(
                    Map.entry("spring.datasource.url", container.getJdbcUrl()),
                    Map.entry("spring.datasource.username", container.getUsername()),
                    Map.entry("spring.datasource.password", container.getPassword()),
                    Map.entry("spring.test.database.replace", AutoConfigureTestDatabase.Replace.NONE)
            ));

            configurableApplicationContext.getEnvironment()
                    .getPropertySources()
                    .addFirst(propertySource);
        };
    }
}
