package br.com.acta.repository;

import br.com.acta.document.Formulario;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormularioRepository extends BaseRepository<Formulario> {
    List<Formulario> findByIdEmpresaAndIdCiclo(Long idEmpresa, Long idCiclo);
    Optional<Formulario> findByIdAndIdEmpresa(UUID id, Long idEmpresa);
}
