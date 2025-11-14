package com.uagrm.ciencias_de_la_educacion.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsConfig {
    private Allowed allowed = new Allowed();
    private Exposed exposed = new Exposed();
    private Allow allow = new Allow();
    private Max max = new Max();

    @Getter
    @Setter
    public static class Allowed {
        private String origins;
        private String methods;
        private String headers;
    }

    @Getter
    @Setter
    public static class Exposed {
        private String headers;
    }

    @Getter
    @Setter
    public static class Allow {
        private Boolean credentials;
    }

    @Getter
    @Setter
    public static class Max {
        private Long age;
    }
}
