# OpenID Connect JVM client

[![codecov](https://codecov.io/gh/shyamz-22/openid-connect-client/branch/master/graph/badge.svg)](https://codecov.io/gh/shyamz-22/openid-connect-client)   [![MavenCentral](https://maven-badges.herokuapp.com/maven-central/io.github.shyamz-22/oidc-jvm-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.shyamz-22/oidc-jvm-client)  [![License: Apache-2.0](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

OpenId Connect Client written in Kotlin.

# OpenID in a nutshell

A Gist of [OpenID Connect](securingApplications.pdf)

This client is implemented with reference to [OpenID Connect Basic Client Implementer's Guide](https://openid.net/specs/openid-connect-basic-1_0.html)

# OpenID connect Basic Flow

![OpenID basic flow](oidcBasic.png)


## Installation

### Gradle

```gradle
compile 'io.github.shyamz-22:oidc-jvm-client:$version'
```

### Maven

```xml
<dependency>
    <groupId>io.github.shyamz-22</groupId>
    <artifactId>oidc-jvm-client</artifactId>
    <version>${version}</version>
</dependency>
```


# How to use the library

## Kotlin

- Step 1:  Load Client Configuration

```kotlin
 ClientConfiguration.
               .with()
               .client("<your-client-id>",
               "<your-redirect-uri>",
               "<your-secret>")
               .issuer("<issuer-url>")

```

- Step 2: Make an Authentication Request

```kotlin
AuthenticationRequestBuilder()
                        .basic()
                        .build()
                        .andRedirect(response);
```

- Step 3: Exchange Code for access token and id token

```kotlin
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

# References
1. [OpenID Connect Specification](https://openid.net/specs/openid-connect-core-1_0.html)
2. [OpenID connect playground](https://openidconnect.net/)
3. [JWT](https://jwt.io/)
