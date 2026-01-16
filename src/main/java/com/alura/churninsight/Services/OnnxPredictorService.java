package com.alura.churninsight.Services;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import com.alura.churninsight.domain.Cliente.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OnnxPredictorService {

    private static final Logger log = LoggerFactory.getLogger(OnnxPredictorService.class);
    private OrtEnvironment env;
    private OrtSession session;

    @PostConstruct
    public void init() {
        try {
            log.info("Cargando modelo ONNX para predicción local...");
            this.env = OrtEnvironment.getEnvironment();
            // El archivo fue convertido y guardado en src/main/resources/churn_model.onnx
            byte[] modelBytes = getClass().getResourceAsStream("/churn_model.onnx").readAllBytes();
            this.session = env.createSession(modelBytes);
            log.info("Modelo ONNX cargado exitosamente. Ready para inferencia local.");
        } catch (Exception e) {
            log.error("Error crítico: No se pudo cargar el modelo ONNX. {}", e.getMessage());
        }
    }

    public com.alura.churninsight.domain.Prediccion.DatosResultadoIA predecir(Cliente cliente) {
        if (session == null) {
            log.warn("Sesión ONNX no disponible.");
            return new com.alura.churninsight.domain.Prediccion.DatosResultadoIA(0.0, 0, new java.util.ArrayList<>());
        }

        try {
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("tiempo_meses",
                    OnnxTensor.createTensor(env, new float[][] { { (float) cliente.getTiempoMeses() } }));
            inputs.put("retrasos_pago",
                    OnnxTensor.createTensor(env, new float[][] { { (float) cliente.getRetrasosPago() } }));
            inputs.put("uso_mensual_horas",
                    OnnxTensor.createTensor(env, new float[][] { { cliente.getUsoMensualHrs().floatValue() } }));
            inputs.put("plan", OnnxTensor.createTensor(env, new String[][] { { cliente.getPlan().name() } }));
            inputs.put("soporte_tickets",
                    OnnxTensor.createTensor(env, new float[][] { { (float) cliente.getSoporteTickets() } }));
            inputs.put("cambio_plan",
                    OnnxTensor.createTensor(env, new float[][] { { cliente.getCambioPlan() ? 1.0f : 0.0f } }));
            inputs.put("pago_automatico",
                    OnnxTensor.createTensor(env, new float[][] { { cliente.getPagoAutomatico() ? 1.0f : 0.0f } }));
            inputs.put("Genero", OnnxTensor.createTensor(env, new String[][] { { cliente.getGenero().name() } }));

            try (OrtSession.Result results = session.run(inputs)) {
                // El resultado 0 es la clase predicha (long[])
                // El resultado 1 es la probabilidad: [{0: p, 1: p}]
                long clasePredicha = ((long[]) results.get(0).getValue())[0];
                Object probabilitiesObj = results.get(1).getValue();

                double probabilidad = 0.0;

                if (probabilitiesObj instanceof List) {
                    List<?> list = (List<?>) probabilitiesObj;
                    if (!list.isEmpty()) {
                        Object firstItem = list.get(0);
                        Map<?, ?> probMap = null;
                        if (firstItem instanceof Map)
                            probMap = (Map<?, ?>) firstItem;
                        else if (firstItem instanceof ai.onnxruntime.OnnxMap)
                            probMap = ((ai.onnxruntime.OnnxMap) firstItem).getValue();

                        if (probMap != null) {
                            Object probChurn = probMap.get(1L);
                            if (probChurn == null)
                                probChurn = probMap.get(1);
                            if (probChurn != null)
                                probabilidad = ((Number) probChurn).doubleValue();
                        }
                    }
                }

                return new com.alura.churninsight.domain.Prediccion.DatosResultadoIA(probabilidad, clasePredicha,
                        new java.util.ArrayList<>());
            } finally {
                inputs.values().forEach(OnnxTensor::close);
            }
        } catch (Exception e) {
            log.error("Error en inferencia ONNX: {}", e.getMessage());
            return new com.alura.churninsight.domain.Prediccion.DatosResultadoIA(0.0, 0, new java.util.ArrayList<>());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (session != null)
                session.close();
            if (env != null)
                env.close();
        } catch (Exception e) {
            log.error("Error cerrando sesión ONNX: {}", e.getMessage());
        }
    }
}
