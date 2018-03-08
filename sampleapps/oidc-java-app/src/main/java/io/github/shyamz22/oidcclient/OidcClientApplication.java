package io.github.shyamz22.oidcclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shyamz.openidconnect.authorization.request.AuthenticationRequestBuilder;
import io.github.shyamz.openidconnect.authorization.request.AuthorizationRequest;
import io.github.shyamz.openidconnect.response.OpenIdConnectCallBackInterceptor;
import io.github.shyamz.openidconnect.response.model.AuthenticatedUser;
import io.github.shyamz22.oidcclient.config.OidcUserToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
@Controller
public class OidcClientApplication {

    public static final String STATE_ATTR = "oidc-state";
    private final Set<String> scopes = Collections.singleton("email");
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
                .state(storeAndGet(request.getSession()))
                .build();

        return "redirect:" + build.getAuthorizeUrl();
    }

    @RequestMapping("/callback")
    public String handleCallback(HttpServletRequest request) {

        AuthenticatedUser authenticatedUser = new OpenIdConnectCallBackInterceptor(request)
                .extractCode(String.valueOf(request.getSession().getAttribute(STATE_ATTR)))
                .exchangeCodeForTokens()
                .extractAuthenticatedUserInfo(null);

        SecurityContextHolder.getContext().setAuthentication(new OidcUserToken(authenticatedUser));

        return "redirect:/home";

    }

    @NotNull
    private String storeAndGet(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute(STATE_ATTR, state);
        return state;

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
