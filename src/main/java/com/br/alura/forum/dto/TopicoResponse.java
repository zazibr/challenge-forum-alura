package com.br.alura.forum.dto;

import com.br.alura.forum.model.Resposta;
import com.br.alura.forum.model.Topico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TopicoResponse {
    Long id;
    String titulo;
    String mensagem;
    String status;
    String dataCriacao;
    String nomeCurso;
    String nomeAutor;
    Long idAutor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<RespostaResponse> respostas;
    @JsonIgnore
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    public static List<TopicoResponse> converter(List<Topico> topicos) {
        List<TopicoResponse> topicoResponses = new ArrayList<>();
        for (Topico topico : topicos) {
            topicoResponses.add(new TopicoResponse(topico, false));
        }
        return topicoResponses;
    }

    private List<RespostaResponse> addRespostas(List<Resposta> respostas) {
        List<RespostaResponse> respostaResponses = new ArrayList<>();
        for (Resposta resposta : respostas) {
            respostaResponses.add(new RespostaResponse(resposta, false));
        }
        if (respostaResponses.isEmpty()) {
            return null;
        }
        return respostaResponses;
    }

    public TopicoResponse(Topico topico, Boolean respostas) {
        this.id = topico.getId();
        this.titulo = topico.getTitulo();
        this.mensagem = topico.getMensagem();
        this.status = topico.getStatus().toString();
        this.dataCriacao = topico.getDataCriacao().format(formatter);
        this.nomeCurso = topico.getCurso().getNome();
        this.nomeAutor = topico.getAutor().getNome();
        this.idAutor = topico.getAutor().getId();
        if (respostas){
            this.respostas = addRespostas(topico.getRespostas());
        } else {
            this.respostas = null;
        }
    }

}
