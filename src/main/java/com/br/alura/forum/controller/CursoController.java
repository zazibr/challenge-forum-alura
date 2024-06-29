package com.br.alura.forum.controller;

import com.br.alura.forum.dto.CursoRecordDto;
import com.br.alura.forum.model.Curso;
import com.br.alura.forum.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping
    public ResponseEntity<Object> salvarCurso(@RequestBody @Valid CursoRecordDto cursoRecordDto, UriComponentsBuilder uriComponentsBuilder) {
        Curso curso = new Curso();
        BeanUtils.copyProperties(cursoRecordDto, curso);
        if (cursoService.buscarCursoPorNome(curso.getNome()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um curso com o nome informado");
        }
        cursoService.salvarCurso(curso);
        var uri = uriComponentsBuilder.path("/cursos/{id}").buildAndExpand(curso.getId()).toUri();
        return ResponseEntity.created(uri).body(curso);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarCurso(@PathVariable Long id) {
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);
        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe curso com o id informado");
        }
        var curso = cursoOptional.get();
        BeanUtils.copyProperties(cursoOptional, curso);
        return ResponseEntity.ok(curso);
    }

    @GetMapping
    public ResponseEntity<Page<Curso>> listarCursos(@PageableDefault(page = 0, size = 10, sort = "id") Pageable paginacao) {
        Optional<Page<Curso>> cursosOptional = Optional.ofNullable(cursoService.listarCursos(paginacao));
        if (cursosOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Page<Curso> cursos = cursosOptional.get();
        return ResponseEntity.ok(cursos);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarCurso(@PathVariable Long id, @RequestBody @Valid CursoRecordDto cursoRecordDto) {
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);
        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe curso com o id informado");
        }
        Curso curso = cursoOptional.get();
        BeanUtils.copyProperties(cursoRecordDto, curso);
        return ResponseEntity.ok(cursoService.salvarCurso(curso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirCurso(@PathVariable Long id) {
        if (cursoService.buscarCursoPorId(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe curso com o id informado");
        }
        cursoService.excluirCurso(id);
        return ResponseEntity.ok().body("Curso excluído com sucesso");
    }

}

