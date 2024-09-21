package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.DuplicateResourceException;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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


}
