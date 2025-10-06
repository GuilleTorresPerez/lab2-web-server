// src/main/kotlin/es/unizar/webeng/lab2/TestErrorController.kt
package es.unizar.webeng.lab2

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestErrorController {
    @GetMapping("/boom-404")
    fun boom404(response: HttpServletResponse) {
        response.sendError(HttpStatus.NOT_FOUND.value(), "boom-404")
    }
}
