# Aplicación de Despacho de Distribuidora de Alimentos

## 📱 Descripción
Esta aplicación móvil permite a los usuarios realizar compras en una distribuidora de alimentos y calcular automáticamente el costo de despacho según el monto de compra y la distancia entre el cliente y la bodega (ubicada en la Plaza de Armas de la ciudad). También contempla el monitoreo de temperatura del congelador del camión para productos que requieren cadena de frío.

## 🚀 Funcionalidades
- Registro de usuarios mediante cuentas Gmail.
- Cálculo automático del costo de despacho:
  - Gratis para compras sobre $50.000 dentro de 20 km.
  - $150/km para compras entre $25.000 y $49.999.
  - $300/km para compras menores a $25.000.
- Cálculo de distancia usando la fórmula Haversine.
- Alarma por sobretemperatura en productos congelados (diseñada como funcionalidad futura).

## 🛠️ Tecnologías
- Android Studio
- Java/Kotlin
- Firebase Authentication (para login con Gmail)
- Sensor de temperatura (simulado o real, según disponibilidad)


