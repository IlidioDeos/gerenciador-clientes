package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.config.ModelMapperConfig;
import br.com.ibmec.gerenciador_clientes.dto.EnderecoDTO;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Importando a configuração necessária e desabilitando filtros de segurança
@WebMvcTest(EnderecoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ModelMapperConfig.class)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoService enderecoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarEnderecos() throws Exception {
        Long clienteId = 1L;

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        // Mockando a resposta do serviço
        when(enderecoService.listarPorClienteId(clienteId)).thenReturn(Arrays.asList(endereco));

        // Realizando a requisição e verificando o resultado
        mockMvc.perform(get("/clientes/{clienteId}/enderecos", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rua", is("Rua A")))
                .andExpect(jsonPath("$[0].numero", is("123")))
                .andExpect(jsonPath("$[0].cidade", is("São Paulo")))
                .andExpect(jsonPath("$[0].estado", is("SP")));
    }

    // Outros testes, como adicionar, atualizar e deletar endereços
}
