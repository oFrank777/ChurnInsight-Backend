package com.alura.churninsight.Controller;

import com.alura.churninsight.Services.PrediccionService;
import com.alura.churninsight.domain.Prediccion.DatosEstadisticas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class PrediccionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PrediccionService prediccionService;

        @Test
        @WithMockUser
        void testObtenerEstadisticas() throws Exception {
                DatosEstadisticas estadisticas = new DatosEstadisticas(100L, 0.25);

                when(prediccionService.obtenerEstadisticas()).thenReturn(estadisticas);

                mockMvc.perform(get("/predict/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalEvaluados").value(100))
                                .andExpect(jsonPath("$.tasaChurn").value(0.25));
        }

        @Test
        @WithMockUser
        void testPredecirIndividualExitosa() throws Exception {
                String json = """
                                {
                                  "id_cliente": 1,
                                  "tiempo_contrato_meses": 12,
                                  "retrasos_pago": 0,
                                  "uso_mensual": 10.5,
                                  "plan": "PREMIUM",
                                  "tickets_soporte": 1
                                }
                                """;

                mockMvc.perform(post("/predict")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void testPredecirIndividualErrorValidacion() throws Exception {
                String json = """
                                {
                                  "id_cliente": 1
                                }
                                """;

                mockMvc.perform(post("/predict")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest());
        }
}
