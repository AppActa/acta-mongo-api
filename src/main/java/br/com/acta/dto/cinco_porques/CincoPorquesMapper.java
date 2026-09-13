package br.com.acta.dto.cinco_porques;

import br.com.acta.document.CincoPorques;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PorqueMapper.class)
public interface CincoPorquesMapper
        extends BaseMapper<CincoPorquesRequestDTO, CincoPorquesResponseDTO, CincoPorques> {

    @Mapping(target = "idIshikawa", ignore = true)
    @Mapping(target = "idCausaRaiz", ignore = true)
    @Override
    CincoPorques toEntity(CincoPorquesRequestDTO dto);
}
