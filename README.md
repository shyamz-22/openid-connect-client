# OpenID connect JVM client

OpenId Connect Client written in Kotlin.

# OpenID in a nutshell

I have tried here my best to simplify [OpenID Connect](securingApplications.pdf)

This client is implemented with reference to [OpenID Connect Basic Client Implementer's Guide](https://openid.net/specs/openid-connect-basic-1_0.html)

# OpenID connect Basic Flow

![OpenID basic flow](oidcBasic.png)


# How to use the library

## Kotlin

- Step 1:  Load Client Configuration

```java
 ClientConfiguration.
               .with()
               .client("<your-client-id>",
               "<your-redirect-uri>",
               "<your-secret>")
               .issuer("<issuer-url>")

```

- Step 2: Make an Authentication Request

```java
AuthenticationRequestBuilder()
                        .basic()
                        .build()
                        .andRedirect(response);
```

- Step 3: Exchange Code for access token and id token

```java
val user = OpenIdConnectCallBackInterceptor(httpServletRequest)
                .extractCode()
                .exchangeCodeForTokens()
                .extractAuthenticatedUserInfo();
```

## Java

- Step 1:  Load Client Configuration

```java
 ClientConfiguration.INSTANCE
               .with()
               .client("<your-client-id>",
               "<your-redirect-uri>",
               "<your-secret>")
               .issuer("<issuer-url>")

```

- Step 2: Make an Authentication Request

```java
new AuthenticationRequestBuilder()
                        .basic()
                        .build()
                        .andRedirect(response);
```

- Step 3: Exchange Code for access token and id token

```java
AuthenticatedUser user = new OpenIdConnectCallBackInterceptor(httpServletRequest)
                .extractCode(null)
                .exchangeCodeForTokens()
                .extractAuthenticatedUserInfo(null);
```
