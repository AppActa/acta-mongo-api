package br.com.acta.repository;

import br.com.acta.document.LicaoAprendida;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LicaoAprendidaRepository extends BaseRepository<LicaoAprendida> {
    List<LicaoAprendida> findByIdEmpresaAndIdCicloOrderByCriadoEmDesc(Long idEmpresa, Long idCiclo);
    Optional<LicaoAprendida> findByIdAndIdEmpresa(UUID id, Long idEmpresa);
}
