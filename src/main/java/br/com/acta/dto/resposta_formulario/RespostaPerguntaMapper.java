package br.com.acta.dto.resposta_formulario;

import br.com.acta.document.embedded.RespostaPergunta;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RespostaPerguntaMapper
        extends BaseMapper<RespostaPerguntaRequestDTO, RespostaPerguntaResponseDTO, RespostaPergunta> {
}
