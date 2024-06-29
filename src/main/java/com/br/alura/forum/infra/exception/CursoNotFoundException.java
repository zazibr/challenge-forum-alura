package com.br.alura.forum.infra.exception;

public class CursoNotFoundException extends RuntimeException {
    public CursoNotFoundException(String mensagem) {
        super(mensagem);
    }
    public CursoNotFoundException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
