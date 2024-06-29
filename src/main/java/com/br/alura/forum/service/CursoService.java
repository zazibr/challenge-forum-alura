package com.br.alura.forum.service;

import com.br.alura.forum.model.Curso;
import com.br.alura.forum.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public Curso salvarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Page<Curso> listarCursos(Pageable paginacao) {
        return cursoRepository.findAll(paginacao);
    }

    public Optional<Curso> buscarCursoPorId(Long cursoId) {
        return cursoRepository.findById(cursoId);
    }

    public Optional<Object> buscarCursoPorNome(String nome) {
        return cursoRepository.findByNome(nome);
    }

    public void excluirCurso(Long id) {
        cursoRepository.deleteById(id);
    }

}
