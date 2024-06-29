package com.br.alura.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record CursoRecordDto(
        @NotBlank
        String nome,
        @NotBlank
        String categoria) {
}
