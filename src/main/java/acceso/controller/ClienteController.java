package acceso.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import acceso.model.Cliente;
import acceso.service.ClienteService;

@Controller
@RequestMapping("/")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("cliente", new Cliente());
        return "index";
    }
    
    @PostMapping("/crear")
    public String crearCliente(@Valid @ModelAttribute Cliente cliente, 
                              BindingResult result, Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.findAll());
            return "index";
        }
        
        
        if (clienteService.existsByEmail(cliente.getEmail())) {
            result.rejectValue("email", "error.cliente", "El email ya está registrado");
            model.addAttribute("clientes", clienteService.findAll());
            return "index";
        }
        
        clienteService.save(cliente);
        return "redirect:/";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de cliente inválido: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("clientes", clienteService.findAll());
        return "index";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizarCliente(@PathVariable Long id, @Valid @ModelAttribute Cliente cliente, 
                                   BindingResult result, Model model) {
        
        if (result.hasErrors()) {
            cliente.setId(id);
            model.addAttribute("clientes", clienteService.findAll());
            return "index";
        }
        
        cliente.setId(id);
        clienteService.save(cliente);
        return "redirect:/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return "redirect:/";
    }
}