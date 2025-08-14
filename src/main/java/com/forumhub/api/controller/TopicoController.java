package com.forumhub.api.controller;

import com.forumhub.api.dto.DadosAtualizacaoTopico;
import com.forumhub.api.dto.DadosCadastroTopico;
import com.forumhub.api.dto.DadosDetalhamentoTopico;
import com.forumhub.api.dto.DadosListagemTopico;
import com.forumhub.api.model.Topico;
import com.forumhub.api.model.Usuario;
import com.forumhub.api.repository.CursoRepository;
import com.forumhub.api.repository.TopicoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoTopico> cadastrar(@RequestBody @Valid DadosCadastroTopico dados, UriComponentsBuilder uriBuilder) {
        // Regra de Negócio: Não permitir tópicos duplicados
        if (topicoRepository.existsByTitulo(dados.titulo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um tópico com este título.");
        }

        // Busca o curso pelo ID fornecido. Lança exceção se não encontrar.
        var curso = cursoRepository.findById(dados.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com o ID fornecido."));

        // Obtém o usuário autenticado a partir do contexto de segurança
        var autor = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Cria o novo tópico com os dados recebidos
        var topico = new Topico();
        topico.setTitulo(dados.titulo());
        topico.setMensagem(dados.mensagem());
        topico.setAutor(autor);
        topico.setCurso(curso);
        // Status e Data de Criação são definidos por padrão no modelo

        topicoRepository.save(topico);

        // Cria a URI para o novo recurso
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

        // Retorna 201 Created com a URI e os detalhes do tópico
        return ResponseEntity.created(uri).body(new DadosDetalhamentoTopico(topico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemTopico>> listar(@PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        var page = topicoRepository.findAll(paginacao).map(DadosListagemTopico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar(@PathVariable Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado."));
        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoTopico> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoTopico dados) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado."));

        // Regra de Segurança: Verifica se o usuário autenticado é o autor do tópico
        var usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!topico.getAutor().equals(usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Atualiza os dados do tópico
        topico.setTitulo(dados.titulo());
        topico.setMensagem(dados.mensagem());
        topico.setStatus(dados.status());

        return ResponseEntity.ok(new DadosDetalhamentoTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado."));

        // Regra de Segurança: Verifica se o usuário autenticado é o autor do tópico
        var usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!topico.getAutor().equals(usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        topicoRepository.delete(topico);

        // Retorna 204 No Content, indicando sucesso sem conteúdo no corpo
        return ResponseEntity.noContent().build();
    }
}