package tareasApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import tareasApp.service.TareaService;
import org.springframework.format.annotation.DateTimeFormat;


@Controller
@RequestMapping("/tareas")
public class TareasController {

    private final TareaService tareaService;
    public TareasController(TareaService s){ this.tareaService = s; }

    @GetMapping({"", "/"})
    public String root() { return "redirect:/tareas/hoy"; }

    @GetMapping("/hoy")
    public String hoy(Model m) {
        m.addAttribute("vista", "hoy");
        m.addAttribute("tareas", tareaService.listarPendientesHoy());
        return "tareas/lista";
    }

    @GetMapping("/pendientes")
    public String pendientes(Model m) {
        m.addAttribute("vista", "pendientes");
        m.addAttribute("tareas", tareaService.listarPendientes());
        return "tareas/lista";
    }

    @GetMapping("/completadas")
    public String completadas(Model m) {
        m.addAttribute("vista", "completadas");
        m.addAttribute("tareas", tareaService.listarCompletadas());
        return "tareas/lista";
    }


    @PostMapping
    public String crear(@RequestParam String titulo,
                        @RequestParam(required = false) String descripcion,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento) {
        tareaService.crear(titulo, descripcion, fechaVencimiento);
        return "redirect:/tareas/hoy";
    }


    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        tareaService.completar(id);
        return (referer != null) ? "redirect:" + referer : "redirect:/tareas/hoy";
    }


    @GetMapping("/filtrar")
    public String filtrar(
            @RequestParam(required = false) String estado,      // "pendientes" | "completadas" | "todas"
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model m) {

        String vista = (estado == null || estado.isBlank()) ? "pendientes" : estado;
        m.addAttribute("vista", vista);
        m.addAttribute("q", q);
        m.addAttribute("desde", desde);
        m.addAttribute("hasta", hasta);
        m.addAttribute("tareas", tareaService.filtrar(vista, q, desde, hasta));
        return "tareas/lista";
    }

}
