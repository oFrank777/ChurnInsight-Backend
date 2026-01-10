CREATE TABLE predicciones (
    id BIGINT AUTO_INCREMENT,
    cliente_id BIGINT,
    probabilidad DOUBLE NOT NULL,
    resultado VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prediccion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE prediccion_factores (
    prediccion_id BIGINT NOT NULL,
    factor VARCHAR(255) NOT NULL,
    CONSTRAINT fk_factores_prediccion FOREIGN KEY (prediccion_id) REFERENCES predicciones(id)
);
