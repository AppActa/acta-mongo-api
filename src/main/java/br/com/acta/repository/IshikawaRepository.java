package br.com.acta.repository;

import br.com.acta.document.Ishikawa;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IshikawaRepository extends BaseRepository<Ishikawa> {
    List<Ishikawa> findByIdEmpresaAndIdCiclo(Long idEmpresa, Long idCiclo);

    Optional<Ishikawa> findByIdAndIdEmpresa(UUID id, Long idEmpresa);
}
