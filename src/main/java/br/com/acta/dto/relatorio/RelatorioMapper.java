package br.com.acta.dto.relatorio;

import br.com.acta.document.Relatorio;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RelatorioMapper
        extends BaseMapper<RelatorioRequestDTO, RelatorioResponseDTO, Relatorio> {

    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "idCiclo", ignore = true)
    @Override
    Relatorio toEntity(RelatorioRequestDTO dto);
}
