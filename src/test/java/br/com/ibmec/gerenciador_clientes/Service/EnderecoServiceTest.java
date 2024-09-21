package br.com.ibmec.gerenciador_clientes.service;

import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");
        cliente.setEnderecos(new ArrayList<>()); // Inicializa a lista de endereços

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
    void atualizarEnderecoPertenceAoCliente_Success() {
        Long clienteId = 1L;
        Long enderecoId = 1L;
        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setRua("Rua B");
        enderecoAtualizado.setNumero("456");
        enderecoAtualizado.setBairro("Bairro B");
        enderecoAtualizado.setCidade("Rio de Janeiro");
        enderecoAtualizado.setEstado("RJ");
        enderecoAtualizado.setCep("87654-321");

        when(enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId)).thenReturn(true);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoAtualizado);

        Endereco resultado = enderecoService.atualizar(clienteId, enderecoId, enderecoAtualizado);

        assertNotNull(resultado);
        assertEquals("Rua B", resultado.getRua());
        assertEquals("456", resultado.getNumero());
        assertEquals("Bairro B", resultado.getBairro());
        assertEquals("Rio de Janeiro", resultado.getCidade());
        assertEquals("RJ", resultado.getEstado());
        assertEquals("87654-321", resultado.getCep());

        verify(enderecoRepository, times(1)).existsByIdAndClienteId(enderecoId, clienteId);
        verify(enderecoRepository, times(1)).findById(enderecoId);
        verify(enderecoRepository, times(1)).save(any(Endereco.class));
    }

    @Test
    void atualizarEnderecoNaoPertenceAoCliente_ThrowsException() {
        Long clienteId = 1L;
        Long enderecoId = 2L; // Endereço que não pertence ao cliente
        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setRua("Rua C");
        enderecoAtualizado.setNumero("789");
        enderecoAtualizado.setBairro("Bairro C");
        enderecoAtualizado.setCidade("Belo Horizonte");
        enderecoAtualizado.setEstado("MG");
        enderecoAtualizado.setCep("11223-445");

        when(enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.deletar(clienteId, enderecoId);
        });

        assertEquals("Endereço com ID: 2 não pertence ao cliente com ID: 1", exception.getMessage());

        verify(enderecoRepository, times(1)).existsByIdAndClienteId(enderecoId, clienteId);
        verify(enderecoRepository, never()).findById(anyLong());
        verify(enderecoRepository, never()).delete(any(Endereco.class));
    }

    @Test
    void listarEnderecosDoCliente_Success() {
        Long clienteId = 1L;

        Endereco endereco1 = new Endereco();
        endereco1.setId(1L);
        endereco1.setRua("Rua A");
        endereco1.setNumero("123");
        endereco1.setBairro("Centro");
        endereco1.setCidade("São Paulo");
        endereco1.setEstado("SP");
        endereco1.setCep("12345-678");
        endereco1.setCliente(cliente);

        Endereco endereco2 = new Endereco();
        endereco2.setId(2L);
        endereco2.setRua("Rua B");
        endereco2.setNumero("456");
        endereco2.setBairro("Bairro B");
        endereco2.setCidade("Rio de Janeiro");
        endereco2.setEstado("RJ");
        endereco2.setCep("87654-321");
        endereco2.setCliente(cliente);

        cliente.getEnderecos().add(endereco1);
        cliente.getEnderecos().add(endereco2);

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);

        List<Endereco> resultado = enderecoService.listarPorClienteId(clienteId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Rua A", resultado.get(0).getRua());
        assertEquals("Rua B", resultado.get(1).getRua());

        // Verifica que enderecoRepository.findByClienteId nunca foi chamado
        verify(enderecoRepository, never()).findByClienteId(anyLong());
    }

    @Test
    void adicionarEndereco_Success() {
        Long clienteId = 1L;
        Endereco endereco = new Endereco();
        endereco.setRua("Rua B");
        endereco.setNumero("456");
        endereco.setBairro("Bairro B");
        endereco.setCidade("Rio de Janeiro");
        endereco.setEstado("RJ");
        endereco.setCep("87654-321");

        Endereco enderecoSalvo = new Endereco();
        enderecoSalvo.setId(2L);
        enderecoSalvo.setRua("Rua B");
        enderecoSalvo.setNumero("456");
        enderecoSalvo.setBairro("Bairro B");
        enderecoSalvo.setCidade("Rio de Janeiro");
        enderecoSalvo.setEstado("RJ");
        enderecoSalvo.setCep("87654-321");
        enderecoSalvo.setCliente(cliente);

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(enderecoSalvo);

        Endereco resultado = enderecoService.adicionar(clienteId, endereco);

        assertNotNull(resultado);
        assertEquals("Rua B", resultado.getRua());
        assertEquals("456", resultado.getNumero());
        verify(enderecoRepository, times(1)).save(any(Endereco.class));
    }

    @Test
    void adicionarEndereco_ClienteNaoEncontrado_ThrowsException() {
        Long clienteId = 1L;
        Endereco endereco = new Endereco();
        endereco.setRua("Rua B");
        endereco.setNumero("456");
        endereco.setBairro("Bairro B");
        endereco.setCidade("Rio de Janeiro");
        endereco.setEstado("RJ");
        endereco.setCep("87654-321");

        when(clienteService.buscarPorId(clienteId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.adicionar(clienteId, endereco);
        });

        assertEquals("Cliente não encontrado com o ID: " + clienteId, exception.getMessage());
        verify(enderecoRepository, never()).save(any(Endereco.class));
    }

    @Test
    void deletarEndereco_Success() {
        Long clienteId = 1L;
        Long enderecoId = 1L;

        when(enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId)).thenReturn(true);
        when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
        doNothing().when(enderecoRepository).delete(endereco); // Serviço chama delete(endereco)

        enderecoService.deletar(clienteId, enderecoId);

        verify(enderecoRepository, times(1)).existsByIdAndClienteId(enderecoId, clienteId);
        verify(enderecoRepository, times(1)).findById(enderecoId);
        verify(enderecoRepository, times(1)).delete(endereco); // Verifica que delete(endereco) foi chamado
    }

    @Test
    void deletarEndereco_EnderecoNaoPertenceAoCliente_ThrowsException() {
        Long clienteId = 1L;
        Long enderecoId = 2L;

        when(enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.deletar(clienteId, enderecoId);
        });

        assertEquals("Endereço com ID: 2 não pertence ao cliente com ID: 1", exception.getMessage());

        verify(enderecoRepository, times(1)).existsByIdAndClienteId(enderecoId, clienteId);
        verify(enderecoRepository, never()).findById(anyLong());
        verify(enderecoRepository, never()).delete(any(Endereco.class));
    }

    @Test
    void deletarEndereco_EnderecoNaoEncontrado_ThrowsException() {
        Long clienteId = 1L;
        Long enderecoId = 3L;

        when(enderecoRepository.existsByIdAndClienteId(enderecoId, clienteId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            enderecoService.deletar(clienteId, enderecoId);
        });

        assertEquals("Endereço com ID: 3 não pertence ao cliente com ID: 1", exception.getMessage());

        verify(enderecoRepository, times(1)).existsByIdAndClienteId(enderecoId, clienteId);
        verify(enderecoRepository, never()).deleteById(anyLong());
        verify(enderecoRepository, never()).delete(any(Endereco.class));
    }

}
