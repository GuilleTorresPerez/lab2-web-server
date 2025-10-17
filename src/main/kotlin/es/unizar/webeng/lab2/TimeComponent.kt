package es.unizar.webeng.lab2

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

//El código reponde a una petición HTTP GET en el endpoint "/time" devolviendo la hora actual en formato JSON.


// 1) DTO (Define el formato de datos que se intercambia con el exterior)
data class TimeDTO(
    val time: LocalDateTime,
)

// 2) Interfaz (Capa de dominio, qué hace el sistema sin entrar en detalles de implementación)
interface TimeProvider {
    fun now(): LocalDateTime
}

// 3) Implementación de servicio (Capa de aplicación, implementa la lógica concreta)
@Service
class TimeService : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.now() // Usa LocalDateTime.now() (infraestructura, algo del sistema operativo o librería estándar).
}

// 4) Es una extension function que convierte el modelo del dominio (LocalDateTime) en un DTO (TimeDTO).
fun LocalDateTime.toDTO(): TimeDTO = TimeDTO(time = this)

// 5) Controlador REST (Capa de infraestructura, expone la funcionalidad a través de HTTP)
@RestController
class TimeController(
    private val service: TimeProvider,
) {
    @GetMapping("/time")
    fun time(): TimeDTO = service.now().toDTO()
}
