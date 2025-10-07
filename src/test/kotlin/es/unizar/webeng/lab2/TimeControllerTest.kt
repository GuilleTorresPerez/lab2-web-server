package es.unizar.webeng.lab2

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@WebMvcTest(TimeController::class)
class TimeControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var timeProvider: TimeProvider

    @Test
    fun `GET - time returns JSON with 200`() {
        val fixed = LocalDateTime.parse("2024-10-01T16:33:58.91803")
        given(timeProvider.now()).willReturn(fixed)

        mockMvc
            .get("/time")
            .andDo { print() }
            .andExpect {
                status { isOk() } // 200
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.time") { value("2024-10-01T16:33:58.91803") }
            }
    }
}
