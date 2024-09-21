package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.config.ModelMapperConfig;
import br.com.ibmec.gerenciador_clientes.dto.EnderecoDTO;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers; // Importação específica
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnderecoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ModelMapperConfig.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void atualizarEnderecoPertenceAoCliente_Success() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 1L;

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua B");
        enderecoDTO.setNumero("456");
        enderecoDTO.setBairro("Bairro B");
        enderecoDTO.setCidade("Rio de Janeiro");
        enderecoDTO.setEstado("RJ");
        enderecoDTO.setCep("87654-321");

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setId(enderecoId);
        enderecoAtualizado.setRua("Rua B");
        enderecoAtualizado.setNumero("456");
        enderecoAtualizado.setBairro("Bairro B");
        enderecoAtualizado.setCidade("Rio de Janeiro");
        enderecoAtualizado.setEstado("RJ");
        enderecoAtualizado.setCep("87654-321");

        // Usando ArgumentMatchers.any() do Mockito para evitar ambiguidade
        when(enderecoService.atualizar(eq(clienteId), eq(enderecoId), ArgumentMatchers.any(Endereco.class))).thenReturn(enderecoAtualizado);

        mockMvc.perform(put("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(enderecoId.intValue())))
                .andExpect(jsonPath("$.rua", is("Rua B")))
                .andExpect(jsonPath("$.numero", is("456")))
                .andExpect(jsonPath("$.cidade", is("Rio de Janeiro")))
                .andExpect(jsonPath("$.estado", is("RJ")))
                .andExpect(jsonPath("$.cep", is("87654-321")));
    }

    @Test
    void atualizarEnderecoNaoPertenceAoCliente_ThrowsException() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 2L;

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua C");
        enderecoDTO.setNumero("789");
        enderecoDTO.setBairro("Bairro C");
        enderecoDTO.setCidade("Belo Horizonte");
        enderecoDTO.setEstado("MG");
        enderecoDTO.setCep("11223-445");

        when(enderecoService.atualizar(eq(clienteId), eq(enderecoId), ArgumentMatchers.any(Endereco.class)))
                .thenThrow(new ResourceNotFoundException("Endereço com ID: 2 não pertence ao cliente com ID: 1"));

        mockMvc.perform(put("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Endereço com ID: 2 não pertence ao cliente com ID: 1")))
                .andExpect(jsonPath("$.path", is("/clientes/1/enderecos/2")));
    }

    @Test
    void listarEnderecosDoCliente_Success() throws Exception {
        Long clienteId = 1L;

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        when(enderecoService.listarPorClienteId(clienteId)).thenReturn(Arrays.asList(endereco));

        mockMvc.perform(get("/clientes/{clienteId}/enderecos", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rua", is("Rua A")))
                .andExpect(jsonPath("$[0].numero", is("123")))
                .andExpect(jsonPath("$[0].bairro", is("Centro")))
                .andExpect(jsonPath("$[0].cidade", is("São Paulo")))
                .andExpect(jsonPath("$[0].estado", is("SP")))
                .andExpect(jsonPath("$[0].cep", is("12345-678")));
    }

    @Test
    void listarEnderecosDoCliente_NotFound() throws Exception {
        Long clienteId = 1L;

        when(enderecoService.listarPorClienteId(clienteId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        mockMvc.perform(get("/clientes/{clienteId}/enderecos", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado com o ID: " + clienteId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId + "/enderecos")));
    }

    @Test
    void adicionarEndereco_Success() throws Exception {
        Long clienteId = 1L;
        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua B");
        enderecoDTO.setNumero("456");
        enderecoDTO.setBairro("Bairro B");
        enderecoDTO.setCidade("Rio de Janeiro");
        enderecoDTO.setEstado("RJ");
        enderecoDTO.setCep("87654-321");

        Endereco enderecoSalvo = new Endereco();
        enderecoSalvo.setId(1L);
        enderecoSalvo.setRua("Rua B");
        enderecoSalvo.setNumero("456");
        enderecoSalvo.setBairro("Bairro B");
        enderecoSalvo.setCidade("Rio de Janeiro");
        enderecoSalvo.setEstado("RJ");
        enderecoSalvo.setCep("87654-321");
        enderecoSalvo.setCliente(null); // Evite referências circulares

        when(enderecoService.adicionar(eq(clienteId), ArgumentMatchers.any(Endereco.class))).thenReturn(enderecoSalvo);

        mockMvc.perform(post("/clientes/{clienteId}/enderecos", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is("http://localhost/clientes/1/enderecos/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rua", is("Rua B")))
                .andExpect(jsonPath("$.numero", is("456")))
                .andExpect(jsonPath("$.bairro", is("Bairro B")))
                .andExpect(jsonPath("$.cidade", is("Rio de Janeiro")))
                .andExpect(jsonPath("$.estado", is("RJ")))
                .andExpect(jsonPath("$.cep", is("87654-321")));
    }

    @Test
    void adicionarEndereco_ClienteNaoEncontrado_ThrowsException() throws Exception {
        Long clienteId = 1L;
        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua B");
        enderecoDTO.setNumero("456");
        enderecoDTO.setBairro("Bairro B");
        enderecoDTO.setCidade("Rio de Janeiro");
        enderecoDTO.setEstado("RJ");
        enderecoDTO.setCep("87654-321");

        when(enderecoService.adicionar(eq(clienteId), ArgumentMatchers.any(Endereco.class)))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        mockMvc.perform(post("/clientes/{clienteId}/enderecos", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente não encontrado com o ID: " + clienteId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId + "/enderecos")));
    }

    @Test
    void deletarEndereco_Success() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 1L;

        doNothing().when(enderecoService).deletar(clienteId, enderecoId);

        mockMvc.perform(delete("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId))
                .andExpect(status().isNoContent());

        verify(enderecoService, times(1)).deletar(clienteId, enderecoId);
    }

    @Test
    void deletarEndereco_EnderecoNaoPertenceAoCliente_ThrowsException() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 2L;

        doThrow(new ResourceNotFoundException("Endereço com ID: 2 não pertence ao cliente com ID: 1"))
                .when(enderecoService).deletar(clienteId, enderecoId);

        mockMvc.perform(delete("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Endereço com ID: 2 não pertence ao cliente com ID: 1")))
                .andExpect(jsonPath("$.path", is("/clientes/1/enderecos/2")));
    }

    @Test
    void deletarEndereco_EnderecoNaoEncontrado_ThrowsException() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 3L;

        doThrow(new ResourceNotFoundException("Endereço não encontrado com o ID: " + enderecoId))
                .when(enderecoService).deletar(clienteId, enderecoId);

        mockMvc.perform(delete("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is("Endereço não encontrado com o ID: " + enderecoId)))
                .andExpect(jsonPath("$.path", is("/clientes/" + clienteId + "/enderecos/" + enderecoId)));
    }
}
