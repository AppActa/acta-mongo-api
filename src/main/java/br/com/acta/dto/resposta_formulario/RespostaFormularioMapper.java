package br.com.acta.dto.resposta_formulario;

import br.com.acta.document.RespostaFormulario;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RespostaPerguntaMapper.class)
public interface RespostaFormularioMapper
        extends BaseMapper<RespostaFormularioRequestDTO, RespostaFormularioResponseDTO, RespostaFormulario> {

    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "idCiclo", ignore = true)
    @Mapping(target = "idFormulario", ignore = true)
    @Mapping(target = "respondidoEm", ignore = true)
    @Override
    RespostaFormulario toEntity(RespostaFormularioRequestDTO dto);
}
