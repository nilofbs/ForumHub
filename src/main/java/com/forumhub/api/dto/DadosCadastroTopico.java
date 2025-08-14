package com.forumhub.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroTopico(
        @NotBlank(message = "Título é obrigatório")
        String titulo,
        @NotBlank(message = "Mensagem é obrigatória")
        String mensagem,
        @NotNull(message = "ID do curso é obrigatório")
        Long idCurso
) {}