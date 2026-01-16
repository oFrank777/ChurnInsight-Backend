-- Migración para sincronizar la base de datos con la nueva lógica de IA
-- Añade el campo es_churn para guardar la decisión binaria del modelo ONNX

ALTER TABLE predicciones ADD COLUMN es_churn BOOLEAN DEFAULT FALSE;
