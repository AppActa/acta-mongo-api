package br.com.acta.common.utils;

import br.com.acta.common.handler.exception.ImmutableFieldException;
import br.com.acta.common.handler.exception.InexistentFieldException;

import java.util.Map;

public final class Validador {
    private Validador() {}

    public static void validarCampos(Map<String, Object> campos, PatchConfig patchConfig) {
        for (String campo : campos.keySet()) {
            if (!patchConfig.allCampos().contains(campo))
                throw new InexistentFieldException(campo);

            if (!patchConfig.patchableCampos().contains(campo))
                throw new ImmutableFieldException(campo);
        }
    }
}
