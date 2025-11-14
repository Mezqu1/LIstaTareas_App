package tareasApp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tareasApp.model.Tarea;
import tareasApp.model.Usuario;
import tareasApp.repository.TareaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository repo;
    private final UsuarioService usuarioService;

    public TareaService(TareaRepository repo, UsuarioService usuarioService) {
        this.repo = repo;
        this.usuarioService = usuarioService;
    }

    // ---------- helper: usuario logueado ----------
    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String nombre = auth.getName();
        if (nombre == null || nombre.isBlank()) return null;
        return usuarioService.buscarPorNombre(nombre);
    }

    // --- Listas rápidas para la UI ---

    @Transactional(readOnly = true)
    public List<Tarea> listarPendientesHoy() {
        return repo.findByArchivadaFalseAndCompletadaFalseAndFechaVencimiento(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarPendientes() {
        return repo.findByArchivadaFalseAndCompletadaFalse();
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarCompletadas() {
        return repo.findByArchivadaFalseAndCompletadaTrue();
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarHoyTodas() {
        return repo.findByArchivadaFalseAndFechaVencimiento(LocalDate.now());
    }

    // --- Crear / Cambiar estado / Archivar ---

    @Transactional
    public Tarea crear(String titulo, String descripcion, LocalDate fechaVencimiento) {
        Tarea t = new Tarea();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setFechaVencimiento(fechaVencimiento != null ? fechaVencimiento : LocalDate.now());
        t.setCompletada(false);
        t.setArchivada(false);

        // NUEVO: asociar al usuario que crea la tarea
        Usuario creador = usuarioActual();
        t.setUsuario(creador); // si es null, luego se muestra "—" en la vista

        return repo.save(t);
    }

    @Transactional
    public void completar(Long id) {
        repo.findById(id).ifPresent(t -> {
            t.setCompletada(true);
            repo.save(t);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void archivar(Long id) {
        repo.findById(id).ifPresent(t -> {
            t.setArchivada(true);
            repo.save(t);
        });
    }

    // --- Filtro unificado para la UI ---

    @Transactional(readOnly = true)
    public List<Tarea> filtrar(String vista, String q, LocalDate desde, LocalDate hasta) {
        // normalizo rango si hay al menos una fecha
        LocalDate d1 = desde, d2 = hasta;
        if (d1 != null && d2 == null) d2 = d1;
        if (d2 != null && d1 == null) d1 = d2;
        if (d1 != null && d2 != null && d1.isAfter(d2)) { var tmp = d1; d1 = d2; d2 = tmp; }

        boolean hayTitulo = q != null && !q.isBlank();
        String qTrim = hayTitulo ? q.trim() : null;

        // Selección por estado
        boolean pendientes = "pendientes".equalsIgnoreCase(vista);
        boolean completadas = "completadas".equalsIgnoreCase(vista);
        boolean todas = "todas".equalsIgnoreCase(vista) || (!pendientes && !completadas);

        // 1) Si no hay rango
        if (d1 == null) {
            if (pendientes) {
                if (hayTitulo) return repo.findByArchivadaFalseAndCompletadaFalseAndTituloContainingIgnoreCase(qTrim);
                return repo.findByArchivadaFalseAndCompletadaFalse();
            }
            if (completadas) {
                if (hayTitulo) return repo.findByArchivadaFalseAndCompletadaTrueAndTituloContainingIgnoreCase(qTrim);
                return repo.findByArchivadaFalseAndCompletadaTrue();
            }
            // todas
            if (hayTitulo) return repo.findByArchivadaFalseAndTituloContainingIgnoreCase(qTrim);
            return repo.findByArchivadaFalseAndFechaVencimientoBetween(LocalDate.MIN, LocalDate.MAX); // todo no-archivado
        }

        // 2) Con rango [d1, d2]
        if (pendientes) {
            if (hayTitulo)
                return repo.findByArchivadaFalseAndCompletadaFalseAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(d1, d2, qTrim);
            return repo.findByArchivadaFalseAndCompletadaFalseAndFechaVencimientoBetween(d1, d2);
        }
        if (completadas) {
            if (hayTitulo)
                return repo.findByArchivadaFalseAndCompletadaTrueAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(d1, d2, qTrim);
            return repo.findByArchivadaFalseAndCompletadaTrueAndFechaVencimientoBetween(d1, d2);
        }
        // todas
        if (hayTitulo)
            return repo.findByArchivadaFalseAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(d1, d2, qTrim);
        return repo.findByArchivadaFalseAndFechaVencimientoBetween(d1, d2);
    }

    // --- Compatibilidad Admin ---
    @Transactional(readOnly = true)
    public List<Tarea> buscarGlobal(String q) {
        return filtrar("todas", q, null, null);
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarGlobalEntre(LocalDate desde, LocalDate hasta) {
        return filtrar("todas", null, desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Tarea> listarGlobalHoy() {
        return repo.findByArchivadaFalseAndFechaVencimiento(LocalDate.now());
    }

}
