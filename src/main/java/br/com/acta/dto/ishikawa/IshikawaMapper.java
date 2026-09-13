package br.com.acta.dto.ishikawa;

import br.com.acta.document.Ishikawa;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CausaIshikawaMapper.class)
public interface IshikawaMapper extends BaseMapper<IshikawaRequestDTO, IshikawaResponseDTO, Ishikawa> {

    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "idCiclo", ignore = true)
    @Override
    Ishikawa toEntity(IshikawaRequestDTO dto);
}
