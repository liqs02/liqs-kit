package com.patryklikus.kit.spring.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(val type: ErrorType, val message: String?)
