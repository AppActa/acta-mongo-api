package br.com.acta.repository;

import br.com.acta.document.Ishikawa;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;

public interface IshikawaRepository extends BaseRepository<Ishikawa> {
    List<Ishikawa> findByIdEmpresaAndIdCiclo(Long idEmpresa, Long idCiclo);
}
