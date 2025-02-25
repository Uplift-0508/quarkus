package io.quarkus.restclient.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.junit.jupiter.api.Test;

import io.smallrye.config.ConfigLogging;
import io.smallrye.config.PropertiesConfigSource;

public class RestClientConfigTest {

    @Test
    public void testLoadRestClientConfig() throws IOException {
        setupMPConfig();

        Config config = ConfigProvider.getConfig();
        Optional<String> optionalValue = config.getOptionalValue("quarkus.rest-client.test-client.url", String.class);
        assertThat(optionalValue).isPresent();

        RestClientConfig configForKey = RestClientConfig.load("test-client");
        verifyConfig(configForKey);
        RestClientConfig configForClassName = RestClientConfig.load(RestClientConfigTest.class);
        verifyConfig(configForClassName);
    }

    private void verifyConfig(RestClientConfig config) {
        assertThat(config.url).isPresent();
        assertThat(config.url.get()).isEqualTo("http://localhost:8080");
        assertThat(config.uri).isPresent();
        assertThat(config.uri.get()).isEqualTo("http://localhost:8081");
        assertThat(config.scope).isPresent();
        assertThat(config.scope.get()).isEqualTo("Singleton");
        assertThat(config.providers).isPresent();
        assertThat(config.providers.get()).isEqualTo("io.quarkus.restclient.configuration.MyResponseFilter");
        assertThat(config.connectTimeout).isPresent();
        assertThat(config.connectTimeout.get()).isEqualTo(5000);
        assertThat(config.readTimeout).isPresent();
        assertThat(config.readTimeout.get()).isEqualTo(6000);
        assertThat(config.followRedirects).isPresent();
        assertThat(config.followRedirects.get()).isEqualTo(true);
        assertThat(config.proxyAddress).isPresent();
        assertThat(config.proxyAddress.get()).isEqualTo("localhost:8080");
        assertThat(config.queryParamStyle).isPresent();
        assertThat(config.queryParamStyle.get()).isEqualTo(QueryParamStyle.COMMA_SEPARATED);
        assertThat(config.hostnameVerifier).isPresent();
        assertThat(config.hostnameVerifier.get()).isEqualTo("io.quarkus.restclient.configuration.MyHostnameVerifier");
        assertThat(config.connectionTTL).isPresent();
        assertThat(config.connectionTTL.get()).isEqualTo(30000);
        assertThat(config.connectionPoolSize).isPresent();
        assertThat(config.connectionPoolSize.get()).isEqualTo(10);
    }

    private static void setupMPConfig() throws IOException {
        ConfigProviderResolver resolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = resolver.getBuilder();
        URL propertyFile = RestClientConfigTest.class.getClassLoader().getResource("application.properties");
        assertThat(propertyFile).isNotNull();
        configBuilder.withSources(new PropertiesConfigSource(propertyFile));
        resolver.registerConfig(configBuilder.build(), getContextClassLoader());
    }

    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
                ClassLoader tccl = null;
                try {
                    tccl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                    ConfigLogging.log.failedToRetrieveClassloader(ex);
                }
                return tccl;
            });
        }
    }
}
