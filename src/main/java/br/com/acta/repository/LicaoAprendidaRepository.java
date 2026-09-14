package br.com.acta.repository;

import br.com.acta.document.LicaoAprendida;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;

public interface LicaoAprendidaRepository extends BaseRepository<LicaoAprendida> {
    List<LicaoAprendida> findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(Long idEmpresa, Long idCiclo);
}
