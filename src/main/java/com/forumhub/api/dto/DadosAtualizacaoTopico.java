package com.forumhub.api.dto;

import jakarta.validation.constraints.NotBlank;
import com.forumhub.api.model.StatusTopico;

public record DadosAtualizacaoTopico(
        @NotBlank
        String titulo,
        @NotBlank
        String mensagem,
        StatusTopico status
) {}