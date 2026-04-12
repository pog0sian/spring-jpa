package com.example.springjpa.security

import com.example.springjpa.api.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class RestAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val message = if (authException.message?.contains("токен", ignoreCase = true) == true) {
            "Невалидный или просроченный токен"
        } else {
            "Требуется аутентификация"
        }

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.characterEncoding = Charsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            objectMapper.writeValueAsString(
                ErrorResponse(
                    status = HttpStatus.UNAUTHORIZED.value(),
                    message = message
                )
            )
        )
        response.writer.flush()
    }
}
