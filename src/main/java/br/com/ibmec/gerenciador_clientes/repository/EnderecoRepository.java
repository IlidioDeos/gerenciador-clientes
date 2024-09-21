package br.com.ibmec.gerenciador_clientes.repository;

import br.com.ibmec.gerenciador_clientes.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Metodo para encontrar endereços por ID do cliente
    List<Endereco> findByClienteId(Long clienteId);

    // Verifica se um endereço pertence a um cliente específico
    boolean existsByIdAndClienteId(Long enderecoId, Long clienteId);
}
