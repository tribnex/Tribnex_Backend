package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.PeriodoFiscalDatos;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.PeriodoFiscalDatosRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodoFiscalDatosService {

    private final PeriodoFiscalDatosRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PeriodoFiscalDatos> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PeriodoFiscalDatos buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional(readOnly = true)
    public List<PeriodoFiscalDatos> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional
    public PeriodoFiscalDatos guardarPeriodo(Long idUsuario, PeriodoFiscalDatos datos) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));

        PeriodoFiscalDatos periodo = repository
                .findByUsuario_IdUsuarioAndPeriodoFiscal(idUsuario, datos.getPeriodoFiscal())
                .orElseGet(() -> PeriodoFiscalDatos.builder().usuario(usuario)
                        .periodoFiscal(datos.getPeriodoFiscal()).build());

        periodo.setTotalVentas(datos.getTotalVentas());
        periodo.setTotalCompras(datos.getTotalCompras());
        periodo.setIvaGenerado(datos.getIvaGenerado());
        periodo.setCreditoTributario(datos.getCreditoTributario());
        return repository.save(periodo);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Periodo fiscal no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private PeriodoFiscalDatos obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Periodo fiscal no encontrado: " + id));
    }
}
