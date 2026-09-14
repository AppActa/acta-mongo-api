package br.com.acta.repository;

import br.com.acta.document.Relatorio;
import br.com.acta.document.enums.StatusRelatorio;
import br.com.acta.repository.base.BaseRepository;

import java.util.List;

public interface RelatorioRepository extends BaseRepository<Relatorio> {
    List<Relatorio> findByIdEmpresaAndIdCicloAndStatusOrderByCriadoEmDesc(Long idEmpresa, Long idCiclo, StatusRelatorio status);
}
