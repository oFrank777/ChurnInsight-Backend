import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
import joblib

# 1. Carga de datos (Ejemplo de estructura para el Hackathon)
data = {
    'tiempo_meses': [12, 24, 5, 48, 2, 36],
    'retrasos_pago': [2, 0, 1, 0, 3, 1],
    'uso_mensual_horas': [15.5, 45.0, 2.3, 50.0, 1.0, 30.0],
    'plan': [1, 3, 1, 3, 1, 2], # 1:Basico, 2:Estandar, 3:Premium
    'soporte_tickets': [4, 1, 5, 0, 6, 2],
    'churn': [1, 0, 1, 0, 1, 0] # 1:Cancela, 0:Continua
}

df = pd.DataFrame(data)

# 2. Entrenamiento del Modelo
X = df.drop('churn', axis=1)
y = df['churn']

model = RandomForestClassifier(n_estimators=100)
model.fit(X, y)

# 3. Exportación
# En un entorno real, el Backend cargaría este archivo .joblib
# joblib.dump(model, 'churn_model.joblib')

print("Modelo DS de referencia generado exitosamente.")
print("Este código simula la lógica que fue traducida al servicio Java para el MVP.")
