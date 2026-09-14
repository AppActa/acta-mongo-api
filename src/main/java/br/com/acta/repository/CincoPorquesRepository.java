package br.com.acta.repository;

import br.com.acta.document.CincoPorques;
import br.com.acta.repository.base.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface CincoPorquesRepository extends BaseRepository<CincoPorques> {
    Optional<CincoPorques> findByIdIshikawaAndIdCausaIshikawa(UUID idIshikawa, UUID idCausaIshikawa);
}
