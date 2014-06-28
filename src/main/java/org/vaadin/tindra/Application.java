package org.vaadin.tindra;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.vaadin.spring.boot.EnableTouchKitServlet;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableTouchKitServlet
public class Application {

    public static void main(String[] args) {
        System.setProperty("org.geotools.referencing.forceXY", "true");
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configure embedded tomcat so that is use gzip for various resources.
     * 
     * @return 
     */
    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(
                    ConfigurableEmbeddedServletContainer servletContainer) {
                ((TomcatEmbeddedServletContainerFactory) servletContainer).
                        addConnectorCustomizers(
                                new TomcatConnectorCustomizer() {
                                    @Override
                                    public void customize(Connector connector) {
                                        AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector.
                                        getProtocolHandler();
                                        httpProtocol.setCompression("on");
                                        httpProtocol.setCompressionMinSize(256);
                                        String mimeTypes = httpProtocol.
                                        getCompressableMimeTypes();
                                        String mimeTypesWithJson = mimeTypes + "," + MediaType.APPLICATION_JSON_VALUE + ",application/javascript";
                                        httpProtocol.setCompressableMimeTypes(
                                                mimeTypesWithJson);
                                    }
                                }
                        );
            }
        };
    }

}
