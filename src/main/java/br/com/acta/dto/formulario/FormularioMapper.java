package br.com.acta.dto.formulario;

import br.com.acta.document.Formulario;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PerguntaMapper.class)
public interface FormularioMapper
        extends BaseMapper<FormularioRequestDTO, FormularioResponseDTO, Formulario> {

    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "idCiclo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publicadoEm", ignore = true)
    @Override
    Formulario toEntity(FormularioRequestDTO dto);
}
