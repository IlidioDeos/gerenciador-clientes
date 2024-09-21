package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.dto.EnderecoDTO;
import br.com.ibmec.gerenciador_clientes.exception.ResourceNotFoundException;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EnderecoController.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void listarEnderecosPorClienteId() throws Exception {
        Long clienteId = 1L;

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        EnderecoDTO enderecoDTO = modelMapper.map(endereco, EnderecoDTO.class);

        when(enderecoService.listarPorClienteId(clienteId)).thenReturn(Arrays.asList(endereco));

        mockMvc.perform(get("/clientes/{clienteId}/enderecos", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rua", is("Rua A")))
                .andExpect(jsonPath("$[0].numero", is("123")));
    }

    @Test
    void adicionarEnderecoParaCliente() throws Exception {
        Long clienteId = 1L;

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua B");
        enderecoDTO.setNumero("456");
        enderecoDTO.setBairro("Bairro Novo");
        enderecoDTO.setCidade("Rio de Janeiro");
        enderecoDTO.setEstado("RJ");
        enderecoDTO.setCep("87654-321");

        Endereco endereco = modelMapper.map(enderecoDTO, Endereco.class);

        Endereco enderecoSalvo = new Endereco();
        enderecoSalvo.setId(2L);
        enderecoSalvo.setRua("Rua B");
        enderecoSalvo.setNumero("456");
        enderecoSalvo.setBairro("Bairro Novo");
        enderecoSalvo.setCidade("Rio de Janeiro");
        enderecoSalvo.setEstado("RJ");
        enderecoSalvo.setCep("87654-321");

        when(enderecoService.adicionar(eq(clienteId), any(Endereco.class))).thenReturn(enderecoSalvo);

        mockMvc.perform(post("/clientes/{clienteId}/enderecos", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.rua", is("Rua B")))
                .andExpect(jsonPath("$.numero", is("456")));
    }

    @Test
    void atualizarEndereco() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 2L;

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setRua("Rua C");
        enderecoDTO.setNumero("789");
        enderecoDTO.setBairro("Bairro Central");
        enderecoDTO.setCidade("Curitiba");
        enderecoDTO.setEstado("PR");
        enderecoDTO.setCep("54321-098");

        Endereco endereco = modelMapper.map(enderecoDTO, Endereco.class);

        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setId(enderecoId);
        enderecoAtualizado.setRua("Rua C");
        enderecoAtualizado.setNumero("789");
        enderecoAtualizado.setBairro("Bairro Central");
        enderecoAtualizado.setCidade("Curitiba");
        enderecoAtualizado.setEstado("PR");
        enderecoAtualizado.setCep("54321-098");

        when(enderecoService.atualizar(eq(enderecoId), any(Endereco.class))).thenReturn(enderecoAtualizado);

        mockMvc.perform(put("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(enderecoId.intValue())))
                .andExpect(jsonPath("$.rua", is("Rua C")))
                .andExpect(jsonPath("$.numero", is("789")));
    }

    @Test
    void deletarEndereco() throws Exception {
        Long clienteId = 1L;
        Long enderecoId = 2L;

        doNothing().when(enderecoService).deletar(enderecoId);

        mockMvc.perform(delete("/clientes/{clienteId}/enderecos/{enderecoId}", clienteId, enderecoId))
                .andExpect(status().isOk());

        verify(enderecoService, times(1)).deletar(enderecoId);
    }

    @Test
    void listarEnderecosClienteNaoEncontrado() throws Exception {
        Long clienteId = 99L;

        when(enderecoService.listarPorClienteId(clienteId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId));

        mockMvc.perform(get("/clientes/{clienteId}/enderecos", clienteId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente não encontrado com o ID: " + clienteId));
    }
}
