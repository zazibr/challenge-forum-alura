CREATE TABLE usuarios (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          senha VARCHAR(255) NOT NULL,
                          ativo TINYINT DEFAULT 1,
                          PRIMARY KEY (id)
);