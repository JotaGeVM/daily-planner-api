TRUNCATE TABLE ocorrencias,
tarefas,
categorias RESTART IDENTITY CASCADE;

ALTER TABLE
    categorias
ADD
    COLUMN usuario_id BIGINT NOT NULL REFERENCES usuarios(id);

ALTER TABLE
    tarefas
ADD
    COLUMN usuario_id BIGINT NOT NULL REFERENCES usuarios(id);