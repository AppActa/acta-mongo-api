package br.com.acta.repository;

import br.com.acta.document.RespostaFormulario;
import br.com.acta.repository.base.BaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RespostaFormularioRepository extends BaseRepository<RespostaFormulario> {
    List<RespostaFormulario> findByIdEmpresaAndIdCicloAndIdFormularioOrderByRespondidoEmDesc(Long idEmpresa, Long idCiclo, UUID idFormulario);

    @Query(value = "{ 'idEmpresa': ?0, 'idCiclo': ?1, 'idFormulario': ?2, 'idUsuario': ?3 }", sort = "{ 'respondidoEm': -1 }")
    List<RespostaFormulario> buscarPorFormularioEUsuario(Long idEmpresa, Long idCiclo, UUID idFormulario, Long idUsuario);

    Optional<RespostaFormulario> findByIdAndIdEmpresa(UUID id, Long idEmpresa);
    boolean existsByIdEmpresaAndIdCicloAndIdFormularioAndIdUsuario(Long idEmpresa, Long idCiclo, UUID idFormulario, Long idUsuario);
}