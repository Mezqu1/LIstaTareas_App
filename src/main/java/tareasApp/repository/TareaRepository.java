package tareasApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tareasApp.model.Tarea;

import java.time.LocalDate;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // --- Listas rápidas ---
    List<Tarea> findByArchivadaFalseAndCompletadaFalse();
    List<Tarea> findByArchivadaFalseAndCompletadaTrue();
    List<Tarea> findByArchivadaFalseAndFechaVencimiento(LocalDate fecha);

    // Hoy pendientes
    List<Tarea> findByArchivadaFalseAndCompletadaFalseAndFechaVencimiento(LocalDate fecha);

    // --- Búsqueda por título (sin rango) ---
    List<Tarea> findByArchivadaFalseAndTituloContainingIgnoreCase(String q);
    List<Tarea> findByArchivadaFalseAndCompletadaFalseAndTituloContainingIgnoreCase(String q);
    List<Tarea> findByArchivadaFalseAndCompletadaTrueAndTituloContainingIgnoreCase(String q);

    // --- Rangos ---
    List<Tarea> findByArchivadaFalseAndFechaVencimientoBetween(LocalDate desde, LocalDate hasta);
    List<Tarea> findByArchivadaFalseAndCompletadaFalseAndFechaVencimientoBetween(LocalDate desde, LocalDate hasta);
    List<Tarea> findByArchivadaFalseAndCompletadaTrueAndFechaVencimientoBetween(LocalDate desde, LocalDate hasta);

    // --- Rangos + título ---
    List<Tarea> findByArchivadaFalseAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(LocalDate desde, LocalDate hasta, String q);
    List<Tarea> findByArchivadaFalseAndCompletadaFalseAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(LocalDate desde, LocalDate hasta, String q);
    List<Tarea> findByArchivadaFalseAndCompletadaTrueAndFechaVencimientoBetweenAndTituloContainingIgnoreCase(LocalDate desde, LocalDate hasta, String q);
}
