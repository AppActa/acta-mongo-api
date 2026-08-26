package br.com.acta.dto.ishikawa;

import br.com.acta.document.embedded.CausaIshikawa;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CausaIshikawaMapper extends BaseMapper<CausaIshikawaRequestDTO, CausaIshikawaResponseDTO, CausaIshikawa> {

    @Mapping(target = "id", ignore = true)
    @Override
    CausaIshikawa toEntity(CausaIshikawaRequestDTO dto);
}
