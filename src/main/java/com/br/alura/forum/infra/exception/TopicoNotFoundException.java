package com.br.alura.forum.infra.exception;

public class TopicoNotFoundException extends RuntimeException {
    public TopicoNotFoundException(String mensagem) {
        super(mensagem);
    }
    public TopicoNotFoundException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
