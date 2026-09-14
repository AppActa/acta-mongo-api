package br.com.acta.repository;

import br.com.acta.document.Formulario;
import br.com.acta.document.enums.StatusFormulario;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;

public interface FormularioRepository extends BaseRepository<Formulario> {
    List<Formulario> findByIdEmpresaAndIdCicloAndStatus(Long idEmpresa, Long idCiclo, StatusFormulario status);
}
