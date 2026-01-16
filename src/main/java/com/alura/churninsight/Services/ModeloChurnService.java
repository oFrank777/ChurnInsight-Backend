package com.alura.churninsight.Services;

import java.util.List;

@org.springframework.stereotype.Service
public class ModeloChurnService {

    private final OnnxPredictorService onnxPredictorService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ModeloChurnService.class);

    public ModeloChurnService(OnnxPredictorService onnxPredictorService) {
        this.onnxPredictorService = onnxPredictorService;
    }

    public ResultadoModelo calcularProbabilidadCancelacion(com.alura.churninsight.domain.Cliente.Cliente cliente) {
        double probabilidad;
        long claseIA = 0;
        java.util.List<String> factores = new java.util.ArrayList<>();

        // 1. Obtener predicción LOCAL usando el modelo ONNX del equipo de Data Science
        try {
            log.info("Ejecutando predicción local con modelo ONNX para cliente: {}", cliente.getClienteId());
            com.alura.churninsight.domain.Prediccion.DatosResultadoIA resultadoIA = onnxPredictorService
                    .predecir(cliente);

            probabilidad = resultadoIA.probabilidad();
            claseIA = resultadoIA.clase();

            log.info("Predicción ONNX exitosa. Prob: {}, Clase: {}", probabilidad, claseIA);
        } catch (Exception e) {
            log.warn("Error en predicción ONNX: {}. Usando lógica de respaldo.", e.getMessage());
            probabilidad = calcularProbabilidadLocal(cliente, factores);
            claseIA = probabilidad >= 0.5 ? 1 : 0;
        }

        // 2. Generar explicabilidad (Factores de Riesgo) en Java
        if (factores.isEmpty()) {
            generarFactoresExplicativos(cliente, factores);
        }

        // 3. Determinar Acción Recomendada
        String accionRecomendada = determinarAccion(cliente, factores);

        java.util.List<String> mejoresFactores = factores.stream().limit(3).toList();
        return new ResultadoModelo(Math.min(probabilidad, 1.0), claseIA, accionRecomendada, mejoresFactores);
    }

    private String determinarAccion(com.alura.churninsight.domain.Cliente.Cliente cliente,
            java.util.List<String> factores) {
        if (factores.isEmpty())
            return "Mantener monitoreo estándar: El cliente no presenta señales de riesgo actuales en su comportamiento.";

        // Prioridad basada en importancia de la IA con descripciones detalladas
        if (cliente.getUsoMensualHrs() < 100)
            return "Campaña de Re-interés Personalizada: Enviar un correo destacando beneficios no utilizados y novedades del servicio para aumentar su tiempo de conexión mensual.";

        if ("BASICO".equals(cliente.getPlan().name()))
            return "Oferta de Upgrade con Beneficios: Proponer el cambio a un plan Estándar o Premium con un beneficio exclusivo (ej. 20% descuento) para mejorar su experiencia y compromiso.";

        if (cliente.getRetrasosPago() > 0)
            return "Plan de Flexibilidad y Recordatorios: Implementar un sistema de alertas proactivas de pago o facilidades financieras para evitar la interrupción del servicio por mora.";

        if (cliente.getTiempoMeses() != null && cliente.getTiempoMeses() < 15)
            return "Programa de Bienvenida y Recompensa: Otorgar un bono de fidelidad o acceso a funciones premium temporales por ser un cliente nuevo, reforzando su lealtad temprana.";

        if (Boolean.TRUE.equals(cliente.getCambioPlan()))
            return "Encuesta de Satisfacción Pos-Cambio: Realizar un seguimiento para asegurar que el nuevo plan cumple sus expectativas y resolver dudas técnicas de forma inmediata.";

        if (cliente.getSoporteTickets() > 3)
            return "Atención Prioritaria V.I.P.: Asignar un ejecutivo especializado para resolver de forma definitiva sus problemas técnicos recurrentes y restaurar la confianza en la marca.";

        return "Llamada de Relacionamiento Preventiva: Realizar un contacto proactivo para conocer su percepción general y detectar posibles insatisfacciones no registradas.";
    }

    private double calcularProbabilidadLocal(com.alura.churninsight.domain.Cliente.Cliente cliente,
            java.util.List<String> factores) {
        double prob = 0.0;
        if (cliente.getRetrasosPago() > 1)
            prob += 0.30;
        if (cliente.getSoporteTickets() > 4)
            prob += 0.25;
        if (cliente.getUsoMensualHrs() < 5)
            prob += 0.20;
        if (cliente.getTiempoMeses() != null && cliente.getTiempoMeses() < 12)
            prob += 0.15;
        if ("BASICO".equals(cliente.getPlan().name()))
            prob += 0.10;
        if (cliente.getPagoAutomatico() == null || !cliente.getPagoAutomatico())
            prob += 0.10;
        if (Boolean.TRUE.equals(cliente.getCambioPlan()))
            prob += 0.15;
        return prob;
    }

    private void generarFactoresExplicativos(com.alura.churninsight.domain.Cliente.Cliente cliente,
            java.util.List<String> factores) {

        // 1. Uso Mensual (Peso IA: 23%)
        if (cliente.getUsoMensualHrs() < 100) {
            factores.add(
                    "Desconexión del servicio: El cliente utiliza la plataforma muy poco (menos de 100 horas al mes), lo que indica un bajo aprovechamiento del valor del producto y riesgo de abandono pronto.");
        }

        // 2. Tipo de Plan (Peso IA: 21%)
        if ("BASICO".equals(cliente.getPlan().name())) {
            factores.add(
                    "Uso de Plan Básico: Los clientes en planes básicos muestran históricamente menor compromiso con la marca; se recomienda incentivar una mejora de plan para aumentar la retención.");
        }

        // 3. Retrasos de Pago (Peso IA: 18%)
        if (cliente.getRetrasosPago() > 0) {
            factores.add(
                    "Fricción en Pagos: Se han registrado retrasos en la facturación, lo cual es un síntoma crítico de insatisfacción o problemas externos que preceden a la cancelación del servicio.");
        }

        // 4. Tiempo de Contrato (Peso IA: 17%)
        if (cliente.getTiempoMeses() != null && cliente.getTiempoMeses() < 15) {
            factores.add(
                    "Curva de Lealtad Temprana: Al tener menos de 15 meses de antigüedad, el cliente aún no ha consolidado su relación con nosotros y es altamente sensible a ofertas de la competencia.");
        }

        // 5. Cambio de Plan (Peso IA: 7%)
        if (Boolean.TRUE.equals(cliente.getCambioPlan())) {
            factores.add(
                    "Inestabilidad tras Modificación: El cliente realizó un cambio de plan recientemente; esta agitación en su cuenta suele ser una señal de búsqueda activa de mejores opciones antes de retirarse.");
        }

        // 6. Soporte Técnico (Peso IA: 3%)
        if (cliente.getSoporteTickets() > 3) {
            factores.add(
                    "Fatiga por Soporte: El volumen de tickets técnicos sugiere una experiencia de usuario frustrante debido a problemas no resueltos, deteriorando su confianza en el servicio.");
        }
    }

    public record ResultadoModelo(double probabilidad, long claseIA, String accionRecomendada, List<String> factores) {
    }
}
