package br.com.acta.dto.resposta_formulario;

import br.com.acta.document.embedded.RespostaPergunta;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RespostaPerguntaMapper
        extends BaseMapper<RespostaPerguntaRequestDTO, RespostaPerguntaResponseDTO, RespostaPergunta> {
    default List<RespostaPergunta> toEntityList(List<RespostaPerguntaRequestDTO> dtos) {
        return dtos.stream().map(this::toEntity).toList();
    }
}
