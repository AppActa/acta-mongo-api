package br.com.acta.dto.licao_aprendida;

import br.com.acta.document.LicaoAprendida;
import br.com.acta.dto.mapper.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LicaoAprendidaMapper
        extends BaseMapper<LicaoAprendidaRequestDTO, LicaoAprendidaResponseDTO, LicaoAprendida> {

    @Mapping(target = "idEmpresa", ignore = true)
    @Mapping(target = "idCiclo", ignore = true)
    @Override
    LicaoAprendida toEntity(LicaoAprendidaRequestDTO dto);
}
