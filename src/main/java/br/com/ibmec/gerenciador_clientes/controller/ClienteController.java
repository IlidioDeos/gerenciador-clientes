package br.com.ibmec.gerenciador_clientes.controller;

import br.com.ibmec.gerenciador_clientes.dto.ClienteDTO;
import br.com.ibmec.gerenciador_clientes.model.Cliente;
import br.com.ibmec.gerenciador_clientes.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<ClienteDTO> clientes = clienteService.listarTodos().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteDTO clienteDTO = convertToDTO(cliente);
        return ResponseEntity.ok(clienteDTO);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> adicionar(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = convertToEntity(clienteDTO);
        Cliente clienteSalvo = clienteService.adicionar(cliente);
        ClienteDTO clienteSalvoDTO = convertToDTO(clienteSalvo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteSalvoDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(clienteSalvoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = convertToEntity(clienteDTO);
        Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
        ClienteDTO clienteAtualizadoDTO = convertToDTO(clienteAtualizado);
        return ResponseEntity.ok(clienteAtualizadoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    private Cliente convertToEntity(ClienteDTO clienteDTO) {
        return modelMapper.map(clienteDTO, Cliente.class);
    }
}
