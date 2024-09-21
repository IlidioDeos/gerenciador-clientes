package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.dto.EnderecoDTO;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.service.EnderecoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes/{clienteId}/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<EnderecoDTO> listarEnderecos(@PathVariable Long clienteId) {
        return enderecoService.listarPorClienteId(clienteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public EnderecoDTO adicionarEndereco(@PathVariable Long clienteId, @Valid @RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = convertToEntity(enderecoDTO);
        Endereco enderecoSalvo = enderecoService.adicionar(clienteId, endereco);
        return convertToDTO(enderecoSalvo);
    }

    @PutMapping("/{enderecoId}")
    public EnderecoDTO atualizarEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId,
                                         @Valid @RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = convertToEntity(enderecoDTO);
        Endereco enderecoAtualizado = enderecoService.atualizar(enderecoId, endereco);
        return convertToDTO(enderecoAtualizado);
    }

    @DeleteMapping("/{enderecoId}")
    public void deletarEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId) {
        enderecoService.deletar(enderecoId);
    }

    private EnderecoDTO convertToDTO(Endereco endereco) {
        return modelMapper.map(endereco, EnderecoDTO.class);
    }

    private Endereco convertToEntity(EnderecoDTO enderecoDTO) {
        return modelMapper.map(enderecoDTO, Endereco.class);
    }
}
