package tareasApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tareasApp.model.Usuario;
import tareasApp.service.UsuarioService;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "admin/usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("esEdicion", false);
        return "admin/usuario_form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute Usuario usuario,
                        @RequestParam(name = "esAdmin", defaultValue = "false") boolean esAdmin) {
        usuarioService.crearUsuario(usuario.getNombre(), usuario.getPassword(),
                esAdmin ? List.of("ROLE_ADMIN") : List.of("ROLE_USER"));
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscar(id));
        model.addAttribute("esEdicion", true);
        return "admin/usuario_form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id, @ModelAttribute Usuario usuario,
                         @RequestParam(name = "esAdmin", defaultValue = "false") boolean esAdmin) {
        usuarioService.actualizar(id, usuario, esAdmin ? List.of("ROLE_ADMIN") : List.of("ROLE_USER"));
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/admin/usuarios";
    }
}