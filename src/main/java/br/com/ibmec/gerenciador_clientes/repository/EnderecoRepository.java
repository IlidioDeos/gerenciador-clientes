package br.com.ibmec.gerenciador_clientes.repository;


import br.com.ibmec.gerenciador_clientes.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
