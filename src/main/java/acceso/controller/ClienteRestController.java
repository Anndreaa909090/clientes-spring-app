package acceso.controller;

import acceso.DTO.ClienteRequestDTO;
import acceso.DTO.ClienteResponseDTO;
import acceso.mapper.ClienteMapper;
import acceso.model.Cliente;
import acceso.service.ClienteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    // 🔹 LISTAR CLIENTES
    @GetMapping
    public List<ClienteResponseDTO> listarClientes() {
        return clienteService.findAll()
                .stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }

    // 🔹 CREAR CLIENTE
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO request) {

        if (clienteService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        Cliente cliente = ClienteMapper.toEntity(request);
        Cliente guardado = clienteService.save(cliente);

        return ResponseEntity.ok(
                ClienteMapper.toDTO(guardado)
        );
    }

    // 🔹 OBTENER CLIENTE POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerCliente(@PathVariable Long id) {

        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return ResponseEntity.ok(
                ClienteMapper.toDTO(cliente)
        );
    }

    // 🔹 ACTUALIZAR CLIENTE
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {

        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());

        Cliente actualizado = clienteService.save(cliente);

        return ResponseEntity.ok(
                ClienteMapper.toDTO(actualizado)
        );
    }

    // 🔹 ELIMINAR CLIENTE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
