package br.com.acta.repository;

import br.com.acta.document.CincoPorques;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface CincoPorquesRepository extends BaseRepository<CincoPorques> {
    List<CincoPorques> findByIdIshikawa(UUID idIshikawa);
}