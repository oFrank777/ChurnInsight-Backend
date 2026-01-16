package com.alura.churninsight.Repository;

import com.alura.churninsight.domain.Prediccion.Prediccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrediccionRepository extends JpaRepository<Prediccion, Long> {

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Prediccion p WHERE p.cliente = :cliente")
    void eliminarPorCliente(com.alura.churninsight.domain.Cliente.Cliente cliente);

    @Query("SELECT p FROM Prediccion p WHERE p.cliente = :cliente AND p.id = (SELECT MAX(p2.id) FROM Prediccion p2 WHERE p2.cliente = :cliente)")
    java.util.Optional<Prediccion> buscarPorCliente(com.alura.churninsight.domain.Cliente.Cliente cliente);

    @Query("SELECT COUNT(DISTINCT p.cliente) FROM Prediccion p")
    long countTotalEvaluados();

    @Query("SELECT COUNT(DISTINCT p.cliente) FROM Prediccion p WHERE p.esChurn = true")
    long countChurnProbable();

    @Query("SELECT p FROM Prediccion p LEFT JOIN FETCH p.cliente LEFT JOIN FETCH p.factores WHERE p.esChurn = true AND p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) ORDER BY p.fecha DESC")
    java.util.List<Prediccion> buscarPrediccionesAltoRiesgo();

    @Query("SELECT p FROM Prediccion p LEFT JOIN FETCH p.cliente LEFT JOIN FETCH p.factores WHERE p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) ORDER BY p.fecha DESC")
    java.util.List<Prediccion> buscarTodasLasUltimasPredicciones();

    // Optimizaciones para Dashboard (Cálculos en DB)
    @Query("SELECT COUNT(p) FROM Prediccion p WHERE p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) AND p.cliente.plan = :plan")
    long countByUltimaPrediccionYPlan(com.alura.churninsight.domain.Cliente.PlanStatus plan);

    @Query("SELECT COUNT(p) FROM Prediccion p WHERE p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) AND p.probabilidad < :max")
    long countRiesgoBajo(double max);

    @Query("SELECT COUNT(p) FROM Prediccion p WHERE p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) AND p.probabilidad >= :min AND p.probabilidad < :max")
    long countRiesgoMedio(double min, double max);

    @Query("SELECT COUNT(p) FROM Prediccion p WHERE p.id IN (SELECT MAX(p2.id) FROM Prediccion p2 GROUP BY p2.cliente.id) AND p.probabilidad >= :min")
    long countRiesgoAlto(double min);

}
