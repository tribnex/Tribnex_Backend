package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.ObligacionUsuario;
import GemeloApp.GemeloTriburarioBackend.repository.ObligacionUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObligacionUsuarioService {

    private final ObligacionUsuarioRepository repository;

    @Transactional(readOnly = true)
    public List<ObligacionUsuario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ObligacionUsuario buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional(readOnly = true)
    public List<ObligacionUsuario> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByFechaVencimientoAsc(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<ObligacionUsuario> listarPorUsuarioYEstado(Long idUsuario, String estado) {
        return repository.findByUsuario_IdUsuarioAndEstado(idUsuario, estado);
    }

    @Transactional
    public ObligacionUsuario crear(ObligacionUsuario datos) {
        datos.setIdObligacionUsuario(null);
        return repository.save(datos);
    }

    @Transactional
    public ObligacionUsuario editar(Long id, ObligacionUsuario datos) {
        ObligacionUsuario existente = obtenerOFallar(id);
        existente.setPeriodoFiscal(datos.getPeriodoFiscal());
        existente.setFechaVencimiento(datos.getFechaVencimiento());
        existente.setEstado(datos.getEstado());
        existente.setEnMiCalendario(datos.getEnMiCalendario());
        existente.setMontoCalculado(datos.getMontoCalculado());
        existente.setNotas(datos.getNotas());
        return repository.save(existente);
    }

    @Transactional
    public ObligacionUsuario marcarCumplida(Long id) {
        ObligacionUsuario o = obtenerOFallar(id);
        o.setEstado("CUMPLIDA");
        o.setFechaCumplimiento(LocalDate.now());
        return repository.save(o);
    }

    @Transactional
    public ObligacionUsuario alternarEnCalendario(Long id, boolean enCalendario) {
        ObligacionUsuario o = obtenerOFallar(id);
        o.setEnMiCalendario(enCalendario);
        return repository.save(o);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Obligacion de usuario no encontrada: " + id);
        }
        repository.deleteById(id);
    }

    private ObligacionUsuario obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Obligacion de usuario no encontrada: " + id));
    }
}
