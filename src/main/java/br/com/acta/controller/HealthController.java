package br.com.acta.controller;

import br.com.acta.dto.health.HealthResponseDTO;
import br.com.acta.dto.health.HealthStatus;
import br.com.acta.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/health", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HealthController {
    private final HealthService service;

    @GetMapping
    public ResponseEntity<HealthResponseDTO> verificar() {
        HealthResponseDTO dto = service.verificar();

        if (dto.status() == HealthStatus.DOWN)
            return ResponseEntity.status(503).body(dto);

        return ResponseEntity.ok(dto);
    }
}