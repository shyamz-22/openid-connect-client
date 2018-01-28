package io.github.shyamz.openidconnect.response.model

data class AuthenticatedUser(val basicFlowResponse: BasicFlowResponse,
                             val claims: Map<String, Any>)