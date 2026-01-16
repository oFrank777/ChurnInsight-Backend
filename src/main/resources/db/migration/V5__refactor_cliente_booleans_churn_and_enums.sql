-- 1. Normalización de Datos: Convertir Strings ("SI"/"NO", "HOMBRE"/"MUJER") a nuevos formatos
-- Primero aseguramos que las columnas sean VARCHAR para poder manipularlas si cambiaron de tipo
-- (Esto es útil si la migración se corre sobre una base parcialmente migrada)

UPDATE clientes SET genero = 'MASCULINO' WHERE genero IN ('HOMBRE', 'MASCULINO');
UPDATE clientes SET genero = 'FEMENINO' WHERE genero IN ('MUJER', 'FEMENINO');

UPDATE clientes SET cambio_plan = '1' WHERE cambio_plan IN ('SI', 'true', '1', 'SÍ');
UPDATE clientes SET cambio_plan = '0' WHERE cambio_plan NOT IN ('1', 'SI', 'true', 'SÍ') OR cambio_plan IS NULL;

UPDATE clientes SET pago_automatico = '1' WHERE pago_automatico IN ('SI', 'true', '1', 'SÍ');
UPDATE clientes SET pago_automatico = '0' WHERE pago_automatico NOT IN ('1', 'SI', 'true', 'SÍ') OR pago_automatico IS NULL;

-- 2. Refactorización de Tipos de Columna
ALTER TABLE clientes 
    MODIFY cambio_plan BOOLEAN NOT NULL DEFAULT 0,
    MODIFY pago_automatico BOOLEAN NOT NULL DEFAULT 0,
    MODIFY genero VARCHAR(20) NOT NULL;

-- 3. Gestión de la columna 'churn'
-- La creamos y la sincronizamos con el historial
ALTER TABLE clientes ADD COLUMN churn BOOLEAN DEFAULT 0;

-- Sincronizar 'churn' con historial de predicciones (Probabilidad >= 0.6 -> Churn = 1)
UPDATE clientes SET churn = 1 WHERE id IN (SELECT cliente_id FROM predicciones WHERE probabilidad >= 0.6);
UPDATE clientes SET churn = 0 WHERE churn IS NULL;
ALTER TABLE clientes MODIFY churn BOOLEAN NOT NULL DEFAULT 0;
