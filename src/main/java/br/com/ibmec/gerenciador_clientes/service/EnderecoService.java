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
    public Endereco atualizar(Long enderecoId, Endereco enderecoAtualizado) {
        Endereco enderecoExistente = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com o ID: " + enderecoId));

        enderecoExistente.setRua(enderecoAtualizado.getRua());
        enderecoExistente.setNumero(enderecoAtualizado.getNumero());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setCidade(enderecoAtualizado.getCidade());
        enderecoExistente.setEstado(enderecoAtualizado.getEstado());
        enderecoExistente.setCep(enderecoAtualizado.getCep());

        return enderecoRepository.save(enderecoExistente);
    }

    @Transactional
    public void deletar(Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com o ID: " + enderecoId));
        enderecoRepository.delete(endereco);
    }
}
