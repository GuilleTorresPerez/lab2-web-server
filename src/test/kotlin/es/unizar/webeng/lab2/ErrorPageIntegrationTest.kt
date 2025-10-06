package es.unizar.webeng.lab2

import jakarta.servlet.DispatcherType
import jakarta.servlet.RequestDispatcher
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import org.springframework.context.ApplicationContext
import org.thymeleaf.spring6.view.ThymeleafViewResolver
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertNotNull


@SpringBootTest
@AutoConfigureMockMvc
class ErrorPageIntegrationTest(@Autowired private val mvc: MockMvc) {

    @Test
    fun `renders custom error html for 404`() {
        mvc.perform(
            get("/error")
                .with { req ->
                    req.dispatcherType = DispatcherType.ERROR
                    req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404)
                    req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/boom-404")
                    req.setAttribute(RequestDispatcher.ERROR_MESSAGE, "boom-404")
                    req
                }
                .accept(MediaType.TEXT_HTML)
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("<!doctype html>")))
            .andExpect(content().string(containsString("x-error-template")))
            .andExpect(content().string(containsString("boom-404")))
    }
}
