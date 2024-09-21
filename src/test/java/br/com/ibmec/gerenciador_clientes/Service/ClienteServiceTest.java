package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.DuplicateResourceException;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");
    }

    @Test
    void adicionarClienteComSucesso() {
        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(clienteRepository.existsByCpf(cliente.getCpf())).thenReturn(false);
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente clienteSalvo = clienteService.adicionar(cliente);

        assertNotNull(clienteSalvo);
        assertEquals(cliente.getId(), clienteSalvo.getId());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void adicionarClienteComEmailDuplicado() {
        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            clienteService.adicionar(cliente);
        });

        assertEquals("Email já cadastrado: " + cliente.getEmail(), exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void adicionarClienteComCpfDuplicado() {
        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(clienteRepository.existsByCpf(cliente.getCpf())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            clienteService.adicionar(cliente);
        });

        assertEquals("CPF já cadastrado: " + cliente.getCpf(), exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void buscarClientePorIdExistente() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        Cliente clienteEncontrado = clienteService.buscarPorId(cliente.getId());

        assertNotNull(clienteEncontrado);
        assertEquals(cliente.getId(), clienteEncontrado.getId());
    }

    @Test
    void buscarClientePorIdInexistente() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.buscarPorId(cliente.getId());
        });

        assertEquals("Cliente não encontrado com o ID: " + cliente.getId(), exception.getMessage());
    }

    @Test
    void listarTodosClientes() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        var clientes = clienteService.listarTodos();

        assertNotNull(clientes);
        assertFalse(clientes.isEmpty());
        assertEquals(1, clientes.size());
    }

    @Test
    void atualizarClienteComSucesso() {
        Long clienteId = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome("João Pedro Silva");
        clienteAtualizado.setEmail("joao.pedro@example.com");
        clienteAtualizado.setCpf("123.456.789-00"); // Mesmo CPF, não duplicado
        clienteAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteAtualizado.setTelefone("(11) 91234-5678");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(clienteAtualizado.getEmail())).thenReturn(false);
        // Removido: when(clienteRepository.existsByCpf(clienteAtualizado.getCpf())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        Cliente resultado = clienteService.atualizar(clienteId, clienteAtualizado);

        assertNotNull(resultado);
        assertEquals("João Pedro Silva", resultado.getNome());
        assertEquals("joao.pedro@example.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_NotFound_ThrowsException() {
        Long clienteId = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João Pedro Silva");
        clienteAtualizado.setEmail("joao.pedro@example.com");
        clienteAtualizado.setCpf("123.456.789-00");
        clienteAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteAtualizado.setTelefone("(11) 91234-5678");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.atualizar(clienteId, clienteAtualizado);
        });

        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_EmailDuplicado_ThrowsException() {
        Long clienteId = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome("João Pedro Silva");
        clienteAtualizado.setEmail("joao.pedro@example.com"); // Email duplicado
        clienteAtualizado.setCpf("123.456.789-00");
        clienteAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteAtualizado.setTelefone("(11) 91234-5678");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(clienteAtualizado.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            clienteService.atualizar(clienteId, clienteAtualizado);
        });

        assertEquals("Email já cadastrado: " + clienteAtualizado.getEmail(), exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void atualizarCliente_CpfDuplicado_ThrowsException() {
        Long clienteId = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome("João Pedro Silva");
        clienteAtualizado.setEmail("joao.pedro@example.com");
        clienteAtualizado.setCpf("999.999.999-99"); // Novo CPF, duplicado
        clienteAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteAtualizado.setTelefone("(11) 91234-5678");

        // Mockando a resposta do repositório
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByEmail(clienteAtualizado.getEmail())).thenReturn(false);
        when(clienteRepository.existsByCpf(clienteAtualizado.getCpf())).thenReturn(true); // CPF duplicado

        // Executando o teste
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            clienteService.atualizar(clienteId, clienteAtualizado);
        });

        // Verificando a mensagem da exceção
        assertEquals("CPF já cadastrado: " + clienteAtualizado.getCpf(), exception.getMessage());

        // Verificando que o método save nunca foi chamado
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deletarClienteComSucesso() {
        Long clienteId = 1L;

        when(clienteRepository.existsById(clienteId)).thenReturn(true); // Cliente existe
        doNothing().when(clienteRepository).deleteById(clienteId);

        clienteService.deletar(clienteId);

        verify(clienteRepository, times(1)).existsById(clienteId);
        verify(clienteRepository, times(1)).deleteById(clienteId);
    }

    @Test
    void deletarCliente_NotFound_ThrowsException() {
        Long clienteId = 1L;

        when(clienteRepository.existsById(clienteId)).thenReturn(false); // Cliente não existe

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            clienteService.deletar(clienteId);
        });

        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(clienteRepository, never()).deleteById(anyLong());
    }

}
