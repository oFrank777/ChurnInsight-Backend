package com.alura.churninsight.domain.Prediccion;

import com.alura.churninsight.domain.Cliente.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "predicciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;

    private double probabilidad;
    private String resultado;
    private boolean esChurn;
    private String accionRecomendada;
    private LocalDateTime fecha;

    @ElementCollection
    @CollectionTable(name = "prediccion_factores", joinColumns = @JoinColumn(name = "prediccion_id"))
    @Column(name = "factor")
    private List<String> factores;
}
