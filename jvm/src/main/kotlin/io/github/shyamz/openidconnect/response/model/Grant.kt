package io.github.shyamz.openidconnect.response.model

import io.github.shyamz.openidconnect.configuration.model.GrantType

abstract class Grant(val grantType: GrantType)