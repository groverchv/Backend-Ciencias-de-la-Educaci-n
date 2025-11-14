package com.uagrm.ciencias_de_la_educacion.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private Secret secret = new Secret();
    private Expiration expiration = new Expiration();
    private Refresh refresh = new Refresh();

    @Getter
    @Setter
    public static class Secret {
        private String key;
    }

    @Getter
    @Setter
    public static class Expiration {
        private Long ms;
    }

    @Getter
    @Setter
    public static class Refresh {
        private Expiration expiration = new Expiration();
    }
}
