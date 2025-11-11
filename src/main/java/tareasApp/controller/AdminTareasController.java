package tareasApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import tareasApp.service.TareaService;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/admin/tareas")
public class AdminTareasController {

    private final TareaService tareaService;
    public AdminTareasController(TareaService s){ this.tareaService = s; }

    @GetMapping
    public String listar(Model m,
                         @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,
                         @RequestParam(required=false) String q) {
        if (q != null && !q.isBlank()) m.addAttribute("tareas", tareaService.buscarGlobal(q));
        else if (desde != null && hasta != null) m.addAttribute("tareas", tareaService.listarGlobalEntre(desde, hasta));
        else m.addAttribute("tareas", tareaService.listarGlobalHoy());
        return "admin/tareas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id){
        tareaService.eliminar(id);
        return "redirect:/admin/tareas";
    }

    @PostMapping("/{id}/archivar")
    public String archivar(@PathVariable Long id){
        tareaService.archivar(id);
        return "redirect:/admin/tareas";
    }
}
