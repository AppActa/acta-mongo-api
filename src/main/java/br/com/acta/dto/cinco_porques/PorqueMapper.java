package br.com.acta.dto.cinco_porques;

import br.com.acta.document.embedded.Porque;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PorqueMapper
        extends BaseMapper<PorqueRequestDTO, PorqueResponseDTO, Porque> {
}
