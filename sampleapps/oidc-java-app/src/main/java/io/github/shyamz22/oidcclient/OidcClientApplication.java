package io.github.shyamz22.oidcclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder;
import io.github.shyamz.openidconnect.authorization.request.AuthorizationRequest;
import io.github.shyamz.openidconnect.response.OpenIdConnectCallBackInterceptor;
import io.github.shyamz.openidconnect.response.model.AuthenticatedUser;
import io.github.shyamz22.oidcclient.config.OidcUserToken;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@SpringBootApplication
@Controller
public class OidcClientApplication {

    private final Set<String> scopes = Collections.singleton("email");
    private Map<String, String> stateStore = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Autowired
    public OidcClientApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(OidcClientApplication.class, args);
    }

    @RequestMapping(value = {"/", "/home"})
    public String home(HttpServletRequest request, final Principal principal) {

        if (Objects.isNull(principal)) {
            return "redirect:/logout";
        }
        request.setAttribute("user", marshal((OidcUserToken) principal));
        return "homepage";
    }

    @RequestMapping(value = {"/logout", "/login"})
    public String logout(HttpServletRequest request) {
        if (Objects.nonNull(request.getSession())) {
            request.getSession().invalidate();
        }

        AuthorizationRequest build = new AuthenticationRequestBuilder()
                .basic()
                .scope(scopes)
                .state(stateGeneratorFunc(request))
                .build();

        return "redirect:" + build.getAuthorizeUrl();
    }

    @RequestMapping("/callback")
    public String handleCallback(HttpServletRequest request) {

        AuthenticatedUser authenticatedUser = new OpenIdConnectCallBackInterceptor(request)
                .extractCode(stateStore.get(request.getSession().getId()))
                .exchangeCodeForTokens()
                .extractAuthenticatedUserInfo(null);

        SecurityContextHolder.getContext().setAuthentication(new OidcUserToken(authenticatedUser));

        return "redirect:/home";

    }

    @NotNull
    private Function0<String> stateGeneratorFunc(HttpServletRequest request) {
        return () -> {
            String state = UUID.randomUUID().toString();
            stateStore.put(request.getSession().getId(), state);
            return state;
        };
    }

    @Nullable
    private String marshal(OidcUserToken principal) {
        try {
            return objectMapper.writeValueAsString(principal);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
