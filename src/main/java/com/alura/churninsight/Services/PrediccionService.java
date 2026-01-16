package com.alura.churninsight.Services;

import com.alura.churninsight.Repository.ClienteRepository;
import com.alura.churninsight.Repository.PrediccionRepository;
import com.alura.churninsight.domain.Cliente.Cliente;
import com.alura.churninsight.domain.Prediccion.Prediccion;
import com.alura.churninsight.domain.Prediccion.DatosSolicitudPrediccion;
import com.alura.churninsight.domain.Prediccion.ResultadoPrediccion;
import com.alura.churninsight.domain.Prediccion.DatosEstadisticas;
import com.alura.churninsight.domain.Prediccion.HistorialDTO;
import com.alura.churninsight.domain.Prediccion.DatosGraficosDTO;
import com.alura.churninsight.Infra.ValidacionDeNegocioException;
import com.alura.churninsight.domain.Cliente.PlanStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class PrediccionService {
        private final ClienteRepository clienteRepository;
        private final ModeloChurnService modeloChurnService;
        private final PrediccionRepository prediccionRepository;
        private final ClienteService clienteService;

        public PrediccionService(ClienteRepository clienteRepository,
                        ModeloChurnService modeloChurnService,
                        PrediccionRepository prediccionRepository,
                        ClienteService clienteService) {
                this.clienteRepository = clienteRepository;
                this.modeloChurnService = modeloChurnService;
                this.prediccionRepository = prediccionRepository;
                this.clienteService = clienteService;
        }

        @Transactional
        public ResultadoPrediccion predecirIndividual(DatosSolicitudPrediccion datos) {
                if (clienteRepository.existsByClienteId(datos.idCliente())) {
                        throw new ValidacionDeNegocioException("Ya existe un cliente con el ID " + datos.idCliente()
                                        + ". Use la sección de consulta o asigne un nuevo ID.");
                }

                Cliente cliente = clienteService.registrarDesdeDTO(datos);
                return procesarPrediccion(cliente);
        }

        @Transactional
        public ResultadoPrediccion predecirPorClienteId(Integer id) {
                Cliente cliente = clienteRepository.findByClienteId(id)
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
                return procesarPrediccion(cliente);
        }

        @Transactional
        public ResultadoPrediccion predecirPorId(Long id) {
                if (id == null)
                        throw new IllegalArgumentException("ID no puede ser nulo");
                Cliente cliente = clienteRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
                return procesarPrediccion(cliente);
        }

        private ResultadoPrediccion procesarPrediccion(Cliente cliente) {
                ModeloChurnService.ResultadoModelo resultadoModelo = modeloChurnService
                                .calcularProbabilidadCancelacion(cliente);

                double probabilidad = resultadoModelo.probabilidad();
                // CONFIAMOS 100% EN LA IA: Usamos la clase predicha por el modelo ONNX (1 =
                // Churn, 0 = Retención)
                boolean isChurnIA = resultadoModelo.claseIA() == 1;
                String prevision = isChurnIA ? "Va a cancelar" : "No va a cancelar";

                cliente.setChurn(isChurnIA);
                clienteRepository.save(cliente);

                // Limpieza absoluta: Cada cliente DEBE tener solo una predicción.
                prediccionRepository.eliminarPorCliente(cliente);
                prediccionRepository.flush();

                Prediccion prediccion = new Prediccion();
                prediccion.setCliente(cliente);
                prediccion.setProbabilidad(probabilidad);
                prediccion.setEsChurn(isChurnIA);
                prediccion.setResultado(prevision);
                prediccion.setAccionRecomendada(resultadoModelo.accionRecomendada());
                prediccion.setFecha(LocalDateTime.now());

                if (resultadoModelo.factores() != null) {
                        prediccion.setFactores(new java.util.ArrayList<>(resultadoModelo.factores()));
                }

                prediccionRepository.save(prediccion);

                return new ResultadoPrediccion(
                                isChurnIA,
                                prevision,
                                probabilidad,
                                resultadoModelo.accionRecomendada(),
                                resultadoModelo.factores(),
                                cliente.getClienteId(),
                                cliente.getGenero(),
                                cliente.getPlan(),
                                cliente.getTiempoMeses(),
                                cliente.getUsoMensualHrs(),
                                cliente.getSoporteTickets(),
                                cliente.getRetrasosPago(),
                                cliente.getPagoAutomatico(),
                                cliente.getCambioPlan());
        }

        public DatosEstadisticas obtenerEstadisticas() {
                long total = prediccionRepository.countTotalEvaluados();
                long cancelaciones = prediccionRepository.countChurnProbable();
                double tasaCancelacion = total == 0 ? 0 : (double) cancelaciones / total;

                return new DatosEstadisticas(total, tasaCancelacion);
        }

        @Transactional
        public List<ResultadoPrediccion> predecirEnLote(MultipartFile archivo) {
                try {
                        String contenido = new String(archivo.getBytes());
                        String[] lineas = contenido.split("\n");
                        log.info("Iniciando procesamiento por lote de {} líneas...", lineas.length - 1);

                        // Procesamiento secuencial para garantizar estabilidad transaccional
                        return java.util.Arrays.stream(lineas)
                                        .skip(1) // Saltar cabecera
                                        .map(linea -> {
                                                String fila = linea.trim();
                                                if (fila.isEmpty())
                                                        return null;
                                                try {
                                                        String[] celdas = fila.split(",");
                                                        // RESPETAMOS EL ID ORIGINAL DEL NEGOCIO (ESCENARIO REAL)
                                                        Integer idCliente = Integer.parseInt(celdas[0].trim());

                                                        // registrarOActualizar buscará si el ID existe:
                                                        // - Si existe: Actualiza los datos del cliente
                                                        // - No existe: Crea un cliente nuevo
                                                        Cliente cliente = clienteService.registrarOActualizar(
                                                                        idCliente,
                                                                        Integer.parseInt(celdas[1].trim()), // meses
                                                                        Integer.parseInt(celdas[2].trim()), // retrasos
                                                                        Double.parseDouble(celdas[3].trim()), // uso
                                                                        PlanStatus.valueOf(
                                                                                        celdas[4].trim().toUpperCase()), // plan
                                                                        Integer.parseInt(celdas[5].trim()), // tickets
                                                                        com.alura.churninsight.domain.Cliente.GeneroStatus
                                                                                        .valueOf(celdas[8].trim()
                                                                                                        .toUpperCase()), // genero
                                                                        celdas[6].trim().equals("1"), // cambio_plan
                                                                        celdas[7].trim().equals("1"), // pago_automatico
                                                                        null // churn
                                                        );

                                                        // Siempre genera una nueva predicción histórica para este
                                                        // cliente
                                                        return procesarPrediccion(cliente);
                                                } catch (Exception e) {
                                                        log.warn("Error procesando línea: {}. Detalle: {}", fila,
                                                                        e.getMessage());
                                                        return null;
                                                }
                                        })
                                        .filter(java.util.Objects::nonNull)
                                        .toList();
                } catch (Exception e) {
                        log.error("Error crítico en procesamiento por lote: {}", e.getMessage());
                        throw new RuntimeException("Error al procesar el archivo CSV", e);
                }
        }

        public List<HistorialDTO> obtenerClientesAltoRiesgo() {
                return prediccionRepository.buscarPrediccionesAltoRiesgo().stream()
                                .map(p -> new HistorialDTO(
                                                p.getId(),
                                                p.getCliente().getClienteId(),
                                                p.getProbabilidad(),
                                                p.getResultado(),
                                                p.getCliente().getChurn() != null ? p.getCliente().getChurn() : false,
                                                p.getAccionRecomendada(),
                                                p.getFecha(),
                                                p.getFactores(),
                                                p.getCliente().getPlan() != null ? p.getCliente().getPlan().toString()
                                                                : "N/A"))
                                .toList();
        }

        public DatosGraficosDTO obtenerDatosGraficos() {
                long total = prediccionRepository.countTotalEvaluados();
                long churn = prediccionRepository.countChurnProbable();
                long retencion = total - churn;

                long basico = prediccionRepository.countByUltimaPrediccionYPlan(PlanStatus.BASICO);
                long estandar = prediccionRepository.countByUltimaPrediccionYPlan(PlanStatus.ESTANDAR);
                long premium = prediccionRepository.countByUltimaPrediccionYPlan(PlanStatus.PREMIUM);

                long riesgoBajo = prediccionRepository.countRiesgoBajo(0.4);
                long riesgoMedio = prediccionRepository.countRiesgoMedio(0.4, 0.7);
                long riesgoAlto = prediccionRepository.countRiesgoAlto(0.7);

                return new DatosGraficosDTO(total, churn, retencion, basico, estandar, premium, riesgoBajo, riesgoMedio,
                                riesgoAlto);
        }

        public java.util.List<Integer> obtenerTodosLosClienteIds() {
                return clienteRepository.findAllClienteIds();
        }
}
