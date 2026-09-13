package br.com.acta.dto.formulario;

import br.com.acta.document.embedded.Pergunta;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PerguntaMapper
        extends BaseMapper<PerguntaRequestDTO, PerguntaResponseDTO, Pergunta> {

    @Mapping(target = "id", ignore = true)
    @Override
    Pergunta toEntity(PerguntaRequestDTO dto);
}
