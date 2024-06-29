CREATE TABLE respostas (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           mensagem VARCHAR(1000) NOT NULL,
                           data_criacao DATETIME NOT NULL,
                           autor_id BIGINT,
                           topico_id BIGINT,
                           solucao TINYINT,
                           FOREIGN KEY (autor_id) REFERENCES usuarios(id),
                           FOREIGN KEY (topico_id) REFERENCES topicos(id) ON DELETE CASCADE
);