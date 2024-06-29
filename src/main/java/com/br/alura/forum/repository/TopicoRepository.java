package com.br.alura.forum.repository;

import com.br.alura.forum.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    List<Topico> findAllByAutorId(Long autor_id);

    Topico findByTitulo(String titulo);

}
