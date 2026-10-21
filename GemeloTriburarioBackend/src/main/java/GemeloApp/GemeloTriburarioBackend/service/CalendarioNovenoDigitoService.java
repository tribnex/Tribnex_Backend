package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.CalendarioNovenoDigito;
import GemeloApp.GemeloTriburarioBackend.repository.CalendarioNovenoDigitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarioNovenoDigitoService {

    private final CalendarioNovenoDigitoRepository repository;

    @Transactional(readOnly = true)
    public List<CalendarioNovenoDigito> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public CalendarioNovenoDigito buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public CalendarioNovenoDigito crear(CalendarioNovenoDigito datos) {
        datos.setIdCalendario(null);
        return repository.save(datos);
    }

    @Transactional
    public CalendarioNovenoDigito editar(Long id, CalendarioNovenoDigito datos) {
        CalendarioNovenoDigito existente = obtenerOFallar(id);
        datos.setIdCalendario(existente.getIdCalendario());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("CalendarioNovenoDigito no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private CalendarioNovenoDigito obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CalendarioNovenoDigito no encontrado: " + id));
    }
}
