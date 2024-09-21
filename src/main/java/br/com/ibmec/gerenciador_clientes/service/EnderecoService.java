package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteService clienteService;

    @Transactional(readOnly = true)
    public Endereco buscarPorId(Long enderecoId) {
        return enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com o ID: " + enderecoId));
    }

    @Transactional(readOnly = true)
    public List<Endereco> listarPorClienteId(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        return cliente.getEnderecos();
    }

    @Transactional
    public Endereco adicionar(Long clienteId, Endereco endereco) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        endereco.setCliente(cliente);
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Endereco atualizar(Long clienteId, Long enderecoId, Endereco enderecoAtualizado) {
        // Verificar se o endereço pertence ao cliente
        boolean pertence = enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId);
        if (!pertence) {
            throw new ResourceNotFoundException("Endereço com ID: " + enderecoId + " não pertence ao cliente com ID: " + clienteId);
        }

        // Buscar o endereço existente
        Endereco enderecoExistente = buscarPorId(enderecoId);

        // Atualizar os campos
        enderecoExistente.setRua(enderecoAtualizado.getRua());
        enderecoExistente.setNumero(enderecoAtualizado.getNumero());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setCidade(enderecoAtualizado.getCidade());
        enderecoExistente.setEstado(enderecoAtualizado.getEstado());
        enderecoExistente.setCep(enderecoAtualizado.getCep());

        return enderecoRepository.save(enderecoExistente);
    }

    @Transactional
    public void deletar(Long clienteId, Long enderecoId) {
        boolean exists = enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId);
        if (!exists) {
            throw new ResourceNotFoundException("Endereço com ID: " + enderecoId + " não pertence ao cliente com ID: " + clienteId);
        }

        Endereco endereco = buscarPorId(enderecoId); // Método que busca o endereço ou lança exceção
        enderecoRepository.delete(endereco);
    }
}
