package com.br.alura.forum.dto;

import com.br.alura.forum.model.Resposta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespostaResponse {

    @NotBlank
    private Long idResposta;
    @NotBlank
    private String nomeTopico;
    @NotBlank
    private String mensagem;
    @NotBlank
    private String dataCriacao;
    @NotBlank
    private String nomeAutor;
    @NotNull
    private Long idAutor;
    @NotBlank
    private String solucao;

    @JsonIgnore
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    public RespostaResponse(Resposta resposta, Boolean respostas) {
        this.idResposta = resposta.getId();
        this.mensagem = resposta.getMensagem();
        if (respostas){
            this.nomeTopico = resposta.getTopico().getTitulo();
        }
        this.dataCriacao = resposta.getDataCriacao().format(formatter);
        this.nomeAutor = resposta.getAutor().getNome();
        this.idAutor = resposta.getAutor().getId();
        if (resposta.getSolucao()){
            this.solucao = "Sim";
        } else {
            this.solucao = "Não";
        }
    }

    public static List<RespostaResponse> converter(List<Resposta> respostas) {
        List<RespostaResponse> respostaResponses = new ArrayList<>();
        for (Resposta resposta : respostas) {
            respostaResponses.add(new RespostaResponse(resposta, true));
        }
        if (respostaResponses.isEmpty()) {
            return null;
        }
        return respostaResponses;
    }
}
