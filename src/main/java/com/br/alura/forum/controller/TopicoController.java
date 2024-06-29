package com.br.alura.forum.controller;

import com.br.alura.forum.dto.*;
import com.br.alura.forum.model.*;
import com.br.alura.forum.service.CursoService;
import com.br.alura.forum.service.RespostaService;
import com.br.alura.forum.service.TopicoService;
import com.br.alura.forum.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;
    @Autowired
    private RespostaService respostaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CursoService cursoService;

    @PostMapping
    public ResponseEntity<Object> criarTopico(@RequestBody @Valid TopicoRecordDto topicoRecordDto, UriComponentsBuilder uriBuilder) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(topicoRecordDto.autorId());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Autor não encontrado!");
        }
        Usuario autor = usuarioOptional.get();
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(topicoRecordDto.cursoId());
        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Curso não encontrado!");
        }
        Curso curso = cursoOptional.get();
        Topico topico = new Topico();
        BeanUtils.copyProperties(topicoRecordDto, topico);
        topico.setAutor(autor);
        topico.setCurso(curso);
        topicoService.salvarTopico(topico);
        TopicoResponse topicoResponse = new TopicoResponse(topico, true);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(topicoResponse);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> responderTopico(@PathVariable Long id, @RequestBody @Valid RespostaRecordDto respostaRecordDto){
        Optional<Topico> topicoOptional = Optional.ofNullable(topicoService.buscarTopicoPorId(id));
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Tópico não encontrado!");
        }
        Topico topico = topicoOptional.get();
        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(respostaRecordDto.autor_id());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Autor não encontrado!");
        }
        Usuario usuario = usuarioOptional.get();
        if (!usuario.isAtivo()){
            return ResponseEntity.badRequest().body("Usuário não está ativo!");
        }
        if (topico.getStatus().equals(StatusTopico.FECHADO)){
            return ResponseEntity.badRequest().body("Não é possível responder um tópico Fechado!");
        } else if (topico.getStatus().equals(StatusTopico.NAO_RESPONDIDO)){
            topicoService.mudarStatusTopico(topico);
        }
        Resposta resposta = new Resposta();
        resposta.setMensagem(respostaRecordDto.mensagem());
        resposta.setAutor(usuario);
        resposta.setTopico(topico);
        respostaService.salvarResposta(resposta);
        return ResponseEntity.ok().body(new TopicoResponse (topico, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponse> buscarTopicoPorId(@PathVariable Long id) {
        Optional<Topico> topicoOptional = Optional.ofNullable(topicoService.buscarTopicoPorId(id));
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Topico topico = topicoOptional.get();
        TopicoResponse topicoResponse = new TopicoResponse(topico, true);
        return ResponseEntity.ok(topicoResponse);
    }

    @GetMapping
    public ResponseEntity<Page<TopicoResponse>> listarTopicos(@PageableDefault(page = 0, size = 10, sort = "dataCriacao") Pageable paginacao) {
        Page<Topico> topicos = topicoService.listarTopicos(paginacao);
        List<TopicoResponse> topicosResponse = TopicoResponse.converter(topicos.getContent());
        Page<TopicoResponse> page = new PageImpl<>(topicosResponse, paginacao, topicos.getTotalElements());
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}/topicos")
    public ResponseEntity<List<TopicoResponse>> listarTopicosPorUsuario(@PathVariable @Valid Long id) {
        List<Topico> topicos = topicoService.listarTopicosPorUsuario(id);
        List<TopicoResponse> topicosResponse = TopicoResponse.converter(topicos);
        return ResponseEntity.ok(topicosResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarTopico(@PathVariable Long id, @RequestBody @Valid TopicoRecordDto topicoRecordDto) {
        Optional<Topico> topicoOptional = Optional.ofNullable(topicoService.buscarTopicoPorId(id));
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Tópico não encontrado!");
        }
        Topico topico = topicoOptional.get();
        if (topico.getStatus().equals(StatusTopico.FECHADO)){
            return ResponseEntity.badRequest().body("Não é possível atualizar um tópico Fechado!");
        }
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(topicoRecordDto.cursoId());
        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Curso não encontrado!");
        }
        Curso curso = cursoOptional.get();
        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(topicoRecordDto.autorId());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Autor não encontrado!");
        }
        Usuario autor = usuarioOptional.get();
        topico.setTitulo(topicoRecordDto.titulo());
        topico.setMensagem(topicoRecordDto.mensagem());
        topico.setCurso(curso);
        topico.setAutor(autor);
        topicoService.salvarTopico(topico);
        TopicoResponse topicoResponse = new TopicoResponse(topico, true);
        return ResponseEntity.ok(topicoResponse);
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<TopicoResponse> fecharTopico(@PathVariable Long id) {
        Topico topico = topicoService.fecharTopico(id);
        TopicoResponse topicoResponse = new TopicoResponse(topico, true);
        return ResponseEntity.ok(topicoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTopico(@PathVariable Long id) {
        topicoService.excluirTopico(id);
        return ResponseEntity.noContent().build();
    }

}