package br.com.acta.controller;

import br.com.acta.dto.licao_aprendida.LicaoAprendidaRequestDTO;
import br.com.acta.dto.licao_aprendida.LicaoAprendidaResponseDTO;
import br.com.acta.service.LicaoAprendidaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LicaoAprendidaController {
    private final LicaoAprendidaService service;

    @GetMapping("/ciclos/{idCiclo}/licoes-aprendidas")
    public ResponseEntity<List<LicaoAprendidaResponseDTO>> buscar(@PathVariable @Positive Long idCiclo) {
        List<LicaoAprendidaResponseDTO> dtos = service.buscarPorCiclo(idCiclo);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/licoes-aprendidas/{idLicaoAprendida}")
    public ResponseEntity<LicaoAprendidaResponseDTO> buscar(@PathVariable UUID idLicaoAprendida) {
        LicaoAprendidaResponseDTO dto = service.buscar(idLicaoAprendida);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/ciclos/{idCiclo}/licoes-aprendidas")
    public ResponseEntity<LicaoAprendidaResponseDTO> inserir(@PathVariable @Positive Long idCiclo, @Valid @RequestBody LicaoAprendidaRequestDTO dto) {
        LicaoAprendidaResponseDTO licaoAprendida = service.inserir(idCiclo, dto);
        return ResponseEntity.status(201).body(licaoAprendida);
    }

    @PatchMapping("/licoes-aprendidas/{idLicaoAprendida}")
    public ResponseEntity<LicaoAprendidaResponseDTO> patch(@PathVariable UUID idLicaoAprendida, @RequestBody Map<String, Object> campos) {
        LicaoAprendidaResponseDTO dto = service.patch(idLicaoAprendida, campos);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/licoes-aprendidas/{idLicaoAprendida}")
    public ResponseEntity<Void> excluir(@PathVariable UUID idLicaoAprendida) {
        service.excluir(idLicaoAprendida);
        return ResponseEntity.noContent().build();
    }
}
