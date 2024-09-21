package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @InjectMocks
    private EnderecoService enderecoService;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private ClienteService clienteService;

    private Cliente cliente;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");
        endereco.setCliente(cliente);
    }

    @Test
    void adicionarEnderecoComSucesso() {
        when(clienteService.buscarPorId(cliente.getId())).thenReturn(cliente);
        when(enderecoRepository.save(endereco)).thenReturn(endereco);

        Endereco enderecoSalvo = enderecoService.adicionar(cliente.getId(), endereco);

        assertNotNull(enderecoSalvo);
        assertEquals(endereco.getId(), enderecoSalvo.getId());
        verify(enderecoRepository, times(1)).save(endereco);
    }

    @Test
    void adicionarEnderecoClienteNaoEncontrado() {
        when(clienteService.buscarPorId(cliente.getId())).thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + cliente.getId()));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.adicionar(cliente.getId(), endereco);
        });

        assertEquals("Cliente não encontrado com o ID: " + cliente.getId(), exception.getMessage());
        verify(enderecoRepository, never()).save(any(Endereco.class));
    }

    @Test
    void buscarEnderecoPorIdExistente() {
        when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.of(endereco));

        Endereco enderecoEncontrado = enderecoService.buscarPorId(endereco.getId());

        assertNotNull(enderecoEncontrado);
        assertEquals(endereco.getId(), enderecoEncontrado.getId());
    }

    @Test
    void buscarEnderecoPorIdInexistente() {
        when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.buscarPorId(endereco.getId());
        });

        assertEquals("Endereço não encontrado com o ID: " + endereco.getId(), exception.getMessage());
    }
}
