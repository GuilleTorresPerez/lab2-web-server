package es.unizar.webeng.lab2

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import jakarta.servlet.RequestDispatcher

@SpringBootTest
@AutoConfigureMockMvc
class ErrorPageTests(@Autowired val mvc: MockMvc) {

    @Test
    @Timeout(5)
    fun `renders custom error html for 404`() {
        val path = "/does-not-exist"

        mvc.perform(
            get("/error")
                .accept(MediaType.TEXT_HTML)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, path)
                .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Not Found")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString(
                "<meta name=\"x-error-template\" content=\"custom-error-v1\">"
            )))
            .andExpect(content().string(containsString("Error 404")))
            .andExpect(content().string(containsString("Not Found")))
            .andExpect(content().string(containsString("Path:")))
            .andExpect(content().string(containsString(path)))
            .andExpect(content().string(containsString("Time:"))) // si lo has cambiado a "Ti:", ajusta aquí
            .andExpect(content().string(containsString(">Back to home</a>")))
    }
}
