package io.github.shyamz22.oidcclient.config;

import io.github.shyamz.openidconnect.configuration.ClientConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static io.github.shyamz.openidconnect.configuration.model.TokenEndPointAuthMethod.Basic;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean {


    @Value(value = "${oidc.issuer}")
    private String issuerUrl;

    @Value(value = "${oidc.client.redirect.uri}")
    private String redirectUri;

    @Value(value = "${oidc.client.id}")
    private String clientId;


    @Value(value = "${oidc.client.secret}")
    private String clientSecret;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/login", "/logout", "/callback").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll();

        http.sessionManagement()
                .invalidSessionUrl("/login")
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    @Override
    public void afterPropertiesSet() {
        ClientConfiguration.INSTANCE
                .with()
                .issuer(issuerUrl)
                .client(clientId,
                        redirectUri,
                        clientSecret)
                .tokenEndPointAuthMethod(Basic);
    }
}
