SISTEMA IoT DE DETECCIÓN DE ALERTAS POR VOZ

---

## BROKER MQTT

Se utiliza el broker público MQTT:

* broker.hivemq.com
* Puerto: 1883

Este broker permite la comunicación entre el ESP32 y Node-RED para el envío y visualización en tiempo real de alertas del sistema.
---

## TOPICS UTILIZADOS

* ubo/g2/alerta

Publica el estado de la alerta en formato JSON (ACTIVA / INACTIVA), junto con información como palabra detectada, contador de alertas, IP, RSSI, timestamp y mensaje descriptivo.

* ubo/g2/sistema

Publica eventos del sistema como la conexión del ESP32 al broker MQTT y su estado general.

---

## PASOS BÁSICOS PARA REVISAR EL SISTEMA

1. Configurar el SSID y contraseña WiFi en el código del ESP32.

2. Cargar el programa en el ESP32 desde Arduino IDE.

3. Verificar el puerto COM usado por Python y configurarlo correctamente en el script de envío.

4. Ejecutar el script Python de reconocimiento de voz o envío de comandos.

5. Abrir Node-RED y desplegar el flujo del dashboard.

6. Enviar una palabra de alerta o comando desde Python (ej: “ALERTA: ayuda”).

7. Verificar activación del LED, buzzer y actualización en Node-RED en tiempo real.
