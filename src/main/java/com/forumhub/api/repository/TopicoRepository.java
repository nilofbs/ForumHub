package com.forumhub.api.repository;

import com.forumhub.api.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    // Verifica se já existe um tópico com o mesmo título
    boolean existsByTitulo(String titulo);
}