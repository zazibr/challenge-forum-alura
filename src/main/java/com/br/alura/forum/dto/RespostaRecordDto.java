package com.br.alura.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RespostaRecordDto(

        @NotBlank
        String mensagem,
        @NotNull
        Long autor_id,
        Long topico_id
) {
}
