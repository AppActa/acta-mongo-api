package br.com.acta.repository;

import br.com.acta.document.Formulario;
import br.com.acta.repository.base.BaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormularioRepository extends BaseRepository<Formulario> {
    @Query(value = "{ 'idEmpresa': ?0, 'idCiclo': ?1, 'status': 'ATIVO', '$or': [ { 'idsUsuariosDestinatarios': null }, { 'idsUsuariosDestinatarios': { '$size': 0 } }, { 'idsUsuariosDestinatarios': ?2 } ] }")
    List<Formulario> findDisponiveisParaUsuario(Long idEmpresa, Long idCiclo, Long idUsuario);

    Optional<Formulario> findByIdAndIdEmpresa(UUID id, Long idEmpresa);
}
