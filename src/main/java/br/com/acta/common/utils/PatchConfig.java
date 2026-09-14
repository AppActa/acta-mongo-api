package br.com.acta.common.utils;

import java.util.HashSet;
import java.util.Set;

public record PatchConfig(
        Set<String> allCampos,
        Set<String> patchableCampos
) {
    public PatchConfig {
        allCampos = new HashSet<>(allCampos);
        allCampos.add("id");
    }
}
