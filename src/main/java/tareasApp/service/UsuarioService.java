package tareasApp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tareasApp.model.Usuario;
import tareasApp.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearUsuario(String nombre, String rawPassword, List<String> roles) {
        String n = nombre == null ? "" : nombre.trim();
        if (n.isEmpty()) throw new IllegalArgumentException("Nombre requerido");
        if (usuarioRepository.existsByNombreIgnoreCase(n))
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");

        Usuario u = new Usuario();
        u.setNombre(n);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRoles(roles);
        return usuarioRepository.save(u);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public Usuario actualizar(Long id, Usuario datos, List<String> roles) {
        Usuario existente = buscar(id);
        String n = datos.getNombre() == null ? "" : datos.getNombre().trim();
        if (n.isEmpty()) throw new IllegalArgumentException("Nombre requerido");
        if (usuarioRepository.existsByNombreIgnoreCaseAndIdNot(n, id))
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre");

        existente.setNombre(n);
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datos.getPassword()));
        }
        existente.setRoles(roles);
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        String actual = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = buscar(id);
        if (u.getNombre().equals(actual)) {
            throw new IllegalStateException("No puedes eliminarte a ti mismo");
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre).orElse(null);
    }
}