package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.dto.ClienteDTO;
import br.com.ibmec.gerenciador_clientes.exception.DuplicateResourceException;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.config.ModelMapperConfig;
import br.com.ibmec.gerenciador_clientes.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ModelMapperConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodosClientes() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@example.com");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");

        // Mockando a resposta do serviço
        when(clienteService.listarTodos()).thenReturn(Arrays.asList(cliente));

        // Realizando a requisição e verificando o resultado
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("João Silva")))
                .andExpect(jsonPath("$[0].email", is("joao.silva@example.com")));
    }

    @Test
    void adicionarCliente_Success() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(2L);
        clienteDTO.setNome("Carlos Souza");
        clienteDTO.setEmail("carlos.souza@example.com");
        clienteDTO.setCpf("321.654.987-00");
        clienteDTO.setDataNascimento(LocalDate.of(1985, 7, 20));
        clienteDTO.setTelefone("(31) 98765-4321");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(2L);
        clienteSalvo.setNome("Carlos Souza");
        clienteSalvo.setEmail("carlos.souza@example.com");
        clienteSalvo.setCpf("321.654.987-00");
        clienteSalvo.setDataNascimento(LocalDate.of(1985, 7, 20));
        clienteSalvo.setTelefone("(31) 98765-4321");

        when(clienteService.adicionar(any(Cliente.class))).thenReturn(clienteSalvo);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("http://localhost/clientes/2")))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.nome", is("Carlos Souza")))
                .andExpect(jsonPath("$.email", is("carlos.souza@example.com")));
    }

    @Test
    void adicionarCliente_EmailDuplicado_ThrowsException() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Ana Paula");
        clienteDTO.setEmail("ana.paula@example.com");
        clienteDTO.setCpf("555.666.777-88");
        clienteDTO.setDataNascimento(LocalDate.of(1993, 8, 25));
        clienteDTO.setTelefone("(41) 91234-5678");

        when(clienteService.adicionar(any(Cliente.class)))
                .thenThrow(new DuplicateResourceException("Email já cadastrado: " + clienteDTO.getEmail()));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Duplicate Resource")))
                .andExpect(jsonPath("$.message", is("Email já cadastrado: " + clienteDTO.getEmail())))
                .andExpect(jsonPath("$.path", is("/clientes")));
    }

    @Test
    void adicionarCliente_CpfDuplicado_ThrowsException() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Ana Paula");
        clienteDTO.setEmail("ana.paula@example.com");
        clienteDTO.setCpf("555.666.777-88");
        clienteDTO.setDataNascimento(LocalDate.of(1993, 8, 25));
        clienteDTO.setTelefone("(41) 91234-5678");

        when(clienteService.adicionar(any(Cliente.class)))
                .thenThrow(new DuplicateResourceException("CPF já cadastrado: " + clienteDTO.getCpf()));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Duplicate Resource")))
                .andExpect(jsonPath("$.message", is("CPF já cadastrado: " + clienteDTO.getCpf())))
                .andExpect(jsonPath("$.path", is("/clientes")));
    }

    @Test
    void buscarClientePorId_Success() throws Exception {
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Maria Oliveira");
        cliente.setEmail("maria.oliveira@example.com");
        cliente.setCpf("987.654.321-00");
        cliente.setDataNascimento(LocalDate.of(1992, 5, 15));
        cliente.setTelefone("(21) 99876-5432");

        when(clienteService.buscarPorId(clienteId)).thenReturn(cliente);

        mockMvc.perform(get("/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clienteId.intValue())))
                .andExpect(jsonPath("$.nome", is("Maria Oliveira")))
                .andExpect(jsonPath("$.email", is("maria.oliveira@example.com")));
    }

    @Test
    void buscarClientePorId_NotFound() throws Exception {
        Long clienteId = 1L;

        when(clienteService.buscarPorId(clienteId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        mockMvc.perform(get("/clientes/{id}", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado com o ID: " + clienteId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId)));
    }

    @Test
    void atualizarCliente_Success() throws Exception {
        Long clienteId = 1L;
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(clienteId);
        clienteDTO.setNome("João Pedro Silva");
        clienteDTO.setEmail("joao.pedro@example.com");
        clienteDTO.setCpf("123.456.789-00"); // Mesmo CPF, não duplicado
        clienteDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteDTO.setTelefone("(11) 91234-5678");

        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(clienteId);
        clienteAtualizado.setNome("João Pedro Silva");
        clienteAtualizado.setEmail("joao.pedro@example.com");
        clienteAtualizado.setCpf("123.456.789-00");
        clienteAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteAtualizado.setTelefone("(11) 91234-5678");

        when(clienteService.atualizar(eq(clienteId), any(Cliente.class))).thenReturn(clienteAtualizado);

        mockMvc.perform(put("/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clienteId.intValue())))
                .andExpect(jsonPath("$.nome", is("João Pedro Silva")))
                .andExpect(jsonPath("$.email", is("joao.pedro@example.com")));
    }

    @Test
    void atualizarCliente_NotFound_ThrowsException() throws Exception {
        Long clienteId = 1L;
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Pedro Silva");
        clienteDTO.setEmail("joao.pedro@example.com");
        clienteDTO.setCpf("123.456.789-00");
        clienteDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteDTO.setTelefone("(11) 91234-5678");

        when(clienteService.atualizar(eq(clienteId), any(Cliente.class)))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        mockMvc.perform(put("/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado com o ID: " + clienteId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId)));
    }

    @Test
    void atualizarCliente_EmailDuplicado_ThrowsException() throws Exception {
        Long clienteId = 1L;
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Pedro Silva");
        clienteDTO.setEmail("joao.pedro@example.com"); // Email duplicado
        clienteDTO.setCpf("123.456.789-00");
        clienteDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteDTO.setTelefone("(11) 91234-5678");

        when(clienteService.atualizar(eq(clienteId), any(Cliente.class)))
                .thenThrow(new DuplicateResourceException("Email já cadastrado: " + clienteDTO.getEmail()));

        mockMvc.perform(put("/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Duplicate Resource")))
                .andExpect(jsonPath("$.message", is("Email já cadastrado: " + clienteDTO.getEmail())))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId)));
    }

    @Test
    void atualizarCliente_CpfDuplicado_ThrowsException() throws Exception {
        Long clienteId = 1L;
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Pedro Silva");
        clienteDTO.setEmail("joao.pedro@example.com");
        clienteDTO.setCpf("123.456.789-00"); // CPF duplicado
        clienteDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteDTO.setTelefone("(11) 91234-5678");

        when(clienteService.atualizar(eq(clienteId), any(Cliente.class)))
                .thenThrow(new DuplicateResourceException("CPF já cadastrado: " + clienteDTO.getCpf()));

        mockMvc.perform(put("/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Duplicate Resource")))
                .andExpect(jsonPath("$.message", is("CPF já cadastrado: " + clienteDTO.getCpf())))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId)));
    }

    @Test
    void removerCliente_Success() throws Exception {
        Long clienteId = 1L;

        doNothing().when(clienteService).deletar(clienteId);

        mockMvc.perform(delete("/clientes/{id}", clienteId))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).deletar(clienteId);
    }

    @Test
    void removerCliente_NotFound_ThrowsException() throws Exception {
        Long clienteId = 1L;

        doThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId))
                .when(clienteService).deletar(clienteId);

        mockMvc.perform(delete("/clientes/{id}", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado com o ID: " + clienteId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId)));
    }
}
