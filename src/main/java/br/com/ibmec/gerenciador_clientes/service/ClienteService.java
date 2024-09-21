package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.DuplicateResourceException;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        logger.info("Listando todos os clientes");
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        logger.debug("Buscando cliente com ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cliente não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
                });
    }

    @Transactional
    public Cliente adicionar(Cliente cliente) {
        logger.debug("Adicionando novo cliente: {}", cliente.getEmail());
        // Verificar unicidade de email e CPF
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            logger.warn("Email duplicado: {}", cliente.getEmail());
            throw new DuplicateResourceException("Email já cadastrado: " + cliente.getEmail());
        }
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            logger.warn("CPF duplicado: {}", cliente.getCpf());
            throw new DuplicateResourceException("CPF já cadastrado: " + cliente.getCpf());
        }
        Cliente salvo = clienteRepository.save(cliente);
        logger.info("Cliente adicionado com ID: {}", salvo.getId());
        return salvo;
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente salvo = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));

        if (!salvo.getEmail().equals(clienteAtualizado.getEmail()) &&
                clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new DuplicateResourceException("Email já cadastrado: " + clienteAtualizado.getEmail());
        }

        if (!salvo.getCpf().equals(clienteAtualizado.getCpf()) &&
                clienteRepository.existsByCpf(clienteAtualizado.getCpf())) {
            throw new DuplicateResourceException("CPF já cadastrado: " + clienteAtualizado.getCpf());
        }

        // Atualizar campos necessários
        salvo.setNome(clienteAtualizado.getNome());
        salvo.setEmail(clienteAtualizado.getEmail());
        salvo.setCpf(clienteAtualizado.getCpf());
        salvo.setDataNascimento(clienteAtualizado.getDataNascimento());
        salvo.setTelefone(clienteAtualizado.getTelefone());

        return clienteRepository.save(salvo);
    }



    @Transactional
    public void deletar(Long id) {
        boolean exists = clienteRepository.existsById(id);
        if (!exists) {
            logger.error("Cliente não encontrado com o ID: {}", id);
            throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deleteById(id);
        logger.info("Cliente deletado com sucesso, ID: {}", id);
    }
}
