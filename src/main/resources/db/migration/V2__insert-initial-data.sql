-- V2__insert-initial-data.sql

-- Inserir um curso inicial para podermos cadastrar tópicos
INSERT INTO cursos(nome, categoria) VALUES('Spring Boot', 'Programação');

-- Inserir um usuário inicial para testes de login
-- A senha é "123456", criptografada com BCrypt
INSERT INTO usuarios(nome, email, senha) VALUES('Aluno Teste', 'aluno@email.com', '$2a$10$Y50UaMFOxteibQEYLrwuHeehHYfcoafCopUazP12.rqB41bsolF5.');