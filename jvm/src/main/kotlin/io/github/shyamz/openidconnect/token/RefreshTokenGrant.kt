package io.github.shyamz.openidconnect.token

import io.github.shyamz.openidconnect.configuration.model.GrantType
import io.github.shyamz.openidconnect.response.model.Grant

class RefreshTokenGrant(val refreshToken: String): Grant(GrantType.RefreshToken)