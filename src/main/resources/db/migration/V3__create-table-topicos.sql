CREATE TABLE topicos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL UNIQUE,
                         mensagem VARCHAR(1000) NOT NULL,
                         data_criacao DATETIME NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         autor_id BIGINT,
                         curso_id BIGINT,
                         FOREIGN KEY (autor_id) REFERENCES usuarios(id),
                         FOREIGN KEY (curso_id) REFERENCES cursos(id)
);