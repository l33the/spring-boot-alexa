package org.springframework.boot.alexa;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.servlet.SkillServlet;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Predefined Configuration for using ASK SDK 2.0 in combination with Spring Boot.
 * Enables HTTPS and the SkillServlet so you just have to include your Skill.
 * Extend from this class to run the default configuration.
 * <p>
 * In your application.properties you have to define two values:
 * <br>
 * <ul>
 * <li><b>alexa.skill.id</b> - Enter here your Skills id</li>
 * <li><b>alexa.skill.handler.classpath</b> - Enter here the classpath where this Configuration should fetch its
 * <a href="https://github.com/alexa/alexa-skills-kit-sdk-for-java/wiki/Developing-Your-First-Skill">RequestHandler</a>
 * from.</li>
 * </ul>
 *
 * @author Sebastian Göhring - Basvik GmbH & Co. KG
 * @version 0.0.1
 * @since 0.0.1
 */
@Configuration
public class AlexaConfiguration {

    @Value("${server.port}")
    private int sslPort;

    @Value("${alexa.skill.id}")
    private String skillId;

    @Value("${alexa.skill.endpoint.url}")
    private String endpoint;

    private final List<RequestHandler> requestHandlers;

    @Autowired
    public AlexaConfiguration(List <RequestHandler> requestHandlers) {
        this.requestHandlers = requestHandlers;
    }

    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(sslPort);
        return connector;
    }

    @Bean
    public ServletRegistrationBean<SkillServlet> registerServlet(Skill skillInstance) {
        SkillServlet skillServlet = new SkillServlet(skillInstance);
        return new ServletRegistrationBean<>(skillServlet, endpoint);
    }

    @Bean
    public Skill skillInstance() {
        return Skills.standard()
                .addRequestHandlers(requestHandlers)
                .withSkillId(skillId)
                .build();
    }
}
