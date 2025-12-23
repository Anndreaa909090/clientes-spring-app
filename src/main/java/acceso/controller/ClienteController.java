package acceso.controller;

import acceso.DTO.ClienteRequestDTO;
import acceso.DTO.ClienteResponseDTO;
import acceso.mapper.ClienteMapper;
import acceso.model.Cliente;
import acceso.service.ClienteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarClientes(Model model) {

        List<ClienteResponseDTO> clientesDTO = clienteService.findAll()
                .stream()
                .map(ClienteMapper::toDTO)
                .toList();

        model.addAttribute("clientes", clientesDTO);
        model.addAttribute("cliente", new ClienteRequestDTO());
        return "index";
    }

    @PostMapping("/crear")
    public String crearCliente(
            @Valid @ModelAttribute("cliente") ClienteRequestDTO request,
            BindingResult result,
            Model model) {

        if (clienteService.existsByEmail(request.getEmail())) {
            result.rejectValue("email", "error.cliente", "El email ya está registrado");
        }

        if (result.hasErrors()) {
            model.addAttribute("clientes",
                    clienteService.findAll()
                            .stream()
                            .map(ClienteMapper::toDTO)
                            .toList());
            return "index";
        }

        Cliente cliente = ClienteMapper.toEntity(request);
        clienteService.save(cliente);

        return "redirect:/";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {

        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());

        model.addAttribute("cliente", dto);
        model.addAttribute("clienteId", id);
        model.addAttribute("clientes",
                clienteService.findAll()
                        .stream()
                        .map(ClienteMapper::toDTO)
                        .toList());

        return "index";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCliente(
            @PathVariable Long id,
            @Valid @ModelAttribute("cliente") ClienteRequestDTO request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("clientes",
                    clienteService.findAll()
                            .stream()
                            .map(ClienteMapper::toDTO)
                            .toList());
            return "index";
        }

        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());

        clienteService.save(cliente);
        return "redirect:/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return "redirect:/";
    }
}
