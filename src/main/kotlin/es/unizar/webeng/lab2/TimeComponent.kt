package es.unizar.webeng.lab2

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

// 1) DTO
data class TimeDTO(
    val time: LocalDateTime,
)

// 2) Interfaz
interface TimeProvider {
    fun now(): LocalDateTime
}

// 3) Implementación de servicio
@Service
class TimeService : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.now()
}

// 4) Extension function
fun LocalDateTime.toDTO(): TimeDTO = TimeDTO(time = this)

// 5) Controlador REST
@RestController
class TimeController(
    private val service: TimeProvider,
) {
    @GetMapping("/time")
    fun time(): TimeDTO = service.now().toDTO()
}
