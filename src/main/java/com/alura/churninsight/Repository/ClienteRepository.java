package com.alura.churninsight.Repository;

import com.alura.churninsight.domain.Cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByClienteId(Integer clienteId);

    java.util.Optional<Cliente> findByClienteId(Integer clienteId);

    @org.springframework.data.jpa.repository.Query("SELECT c.clienteId FROM Cliente c ORDER BY c.clienteId")
    java.util.List<Integer> findAllClienteIds();
}
