package com.br.alura.forum.repository;

import com.br.alura.forum.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    Curso findById(long id);

    Optional<Object> findByNome(String nome);
}
