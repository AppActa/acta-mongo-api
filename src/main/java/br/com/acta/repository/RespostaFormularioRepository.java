package br.com.acta.repository;

import br.com.acta.document.RespostaFormulario;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface RespostaFormularioRepository extends BaseRepository<RespostaFormulario> {
    List<RespostaFormulario> findByIdEmpresaAndIdCicloAndIdFormularioOrderByRespondidoEmDesc(Long idEmpresa, Long idCiclo, UUID idFormulario);
}
