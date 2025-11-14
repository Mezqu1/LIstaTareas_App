package tareasApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String listar(Model model,
                         @RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "msg", required = false) String msg) {

        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        if (error != null) model.addAttribute("error", error);
        if (msg != null) model.addAttribute("msg", msg);
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
                        @RequestParam(name = "esAdmin", defaultValue = "false") boolean esAdmin,
                        RedirectAttributes ra) {
        try {
            usuarioService.crearUsuario(
                    usuario.getNombre(),
                    usuario.getPassword(),
                    esAdmin ? List.of("ROLE_ADMIN") : List.of("ROLE_USER")
            );
            ra.addFlashAttribute("msg", "Usuario creado correctamente.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace(); // DEBUG
            ra.addFlashAttribute("error",
                    "Error inesperado al crear usuario: "
                            + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id,
                             @RequestParam(value = "error", required = false) String error,
                             Model model) {
        model.addAttribute("usuario", usuarioService.buscar(id));
        model.addAttribute("esEdicion", true);
        if (error != null) model.addAttribute("error", error);
        return "admin/usuario_form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @ModelAttribute Usuario usuario,
                         @RequestParam(name = "esAdmin", defaultValue = "false") boolean esAdmin,
                         RedirectAttributes ra) {
        try {
            usuarioService.actualizar(
                    id,
                    usuario,
                    esAdmin ? List.of("ROLE_ADMIN") : List.of("ROLE_USER")
            );
            ra.addFlashAttribute("msg", "Usuario actualizado correctamente.");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        } catch (Exception ex) {
            ex.printStackTrace(); // imprime el stacktrace en la consola
            ra.addFlashAttribute("error",
                    "Error inesperado al actualizar usuario: "
                            + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("msg", "Usuario eliminado correctamente.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace(); // DEBUG
            ra.addFlashAttribute("error",
                    "No se pudo eliminar el usuario: "
                            + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
