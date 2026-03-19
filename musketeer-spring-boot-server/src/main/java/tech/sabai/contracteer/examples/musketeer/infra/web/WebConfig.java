package tech.sabai.contracteer.examples.musketeer.infra.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Serves the hand-written OpenAPI spec as a static resource so that
// Swagger UI (via springdoc.swagger-ui.url in application.yml) can load it.
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/musketeer-api.yaml")
            .addResourceLocations("classpath:/");
  }
}
