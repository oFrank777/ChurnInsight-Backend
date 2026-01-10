package com.alura.churninsight.domain.Prediccion;

import com.alura.churninsight.domain.Cliente.PlanStatus;
import com.alura.churninsight.domain.Cliente.GeneroStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DatosSolicitudPrediccion(
                @NotNull(message = "El ID del cliente es obligatorio para el registro") @JsonProperty("id_cliente") Integer idCliente,

                @NotNull(message = "El tiempo de contrato es obligatorio") @Min(0) @Max(120) @JsonProperty("tiempo_contrato_meses") Integer tiempoContratoMeses,

                @NotNull(message = "El número de retrasos es obligatorio") @Min(0) @JsonProperty("retrasos_pago") Integer retrasosPago,

                @NotNull(message = "El uso mensual es obligatorio") @PositiveOrZero @JsonProperty("uso_mensual") Double usoMensual,

                @NotNull(message = "El plan es obligatorio") @JsonProperty("plan") PlanStatus plan,

                @NotNull(message = "El número de tickets de soporte es obligatorio") @Min(0) @JsonProperty("tickets_soporte") Integer ticketsSoporte,

                @JsonProperty("genero") GeneroStatus genero,

                @JsonProperty("pago_automatico") Boolean pagoAutomatico,

                @JsonProperty("cambio_plan") Boolean cambioPlan,

                @JsonProperty("churn") Boolean churn) {
}
