package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.dto.EnderecoDTO;
import br.com.ibmec.gerenciador_clientes.model.Endereco;
import br.com.ibmec.gerenciador_clientes.service.EnderecoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<List<EnderecoDTO>> listarEnderecos(@PathVariable Long clienteId) {
        List<EnderecoDTO> enderecos = enderecoService.listarPorClienteId(clienteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enderecos);
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO> adicionarEndereco(@PathVariable Long clienteId, @Valid @RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = convertToEntity(enderecoDTO);
        Endereco enderecoSalvo = enderecoService.adicionar(clienteId, endereco);
        EnderecoDTO enderecoSalvoDTO = convertToDTO(enderecoSalvo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(enderecoSalvoDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(enderecoSalvoDTO);
    }

    @PutMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId,
                                                         @Valid @RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = convertToEntity(enderecoDTO);
        Endereco enderecoAtualizado = enderecoService.atualizar(clienteId, enderecoId, endereco);
        EnderecoDTO enderecoAtualizadoDTO = convertToDTO(enderecoAtualizado);
        return ResponseEntity.ok(enderecoAtualizadoDTO);
    }

    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId) {
        enderecoService.deletar(clienteId, enderecoId);
        return ResponseEntity.noContent().build();
    }

    private EnderecoDTO convertToDTO(Endereco endereco) {
        return modelMapper.map(endereco, EnderecoDTO.class);
    }

    private Endereco convertToEntity(EnderecoDTO enderecoDTO) {
        return modelMapper.map(enderecoDTO, Endereco.class);
    }
}
