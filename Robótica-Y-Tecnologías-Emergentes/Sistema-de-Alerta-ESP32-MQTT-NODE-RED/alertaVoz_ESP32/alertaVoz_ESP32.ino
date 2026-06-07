#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// WiFi
const char* ssid = "Merino";
const char* password = "1234567m";

// MQTT
const char* mqtt_server = "broker.hivemq.com";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

// Hardware
#define LED_PIN 23
#define BUZZER_PIN 22

bool alertaActiva = false;
String ultimaPalabra = "";
int contadorAlertas = 0;

unsigned long inicioAlerta = 0;
const unsigned long TIEMPO_ALERTA = 3000;

// Timers
unsigned long lastWiFiTry = 0;
unsigned long lastMQTTTry = 0;

void conectarWiFi() {
  Serial.println("[WiFi] Conectando...");
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
}

void verificarWiFi() {
  if (WiFi.status() == WL_CONNECTED) return;

  if (millis() - lastWiFiTry > 10000) {
    lastWiFiTry = millis();
    Serial.println("[WiFi] reconectando...");
    WiFi.disconnect();
    WiFi.begin(ssid, password);
  }
}

bool conectarMQTT() {
  if (WiFi.status() != WL_CONNECTED) return false;

  String clientId = "ESP32_" + String(random(10000));

  Serial.println("[MQTT] intentando conectar...");

  bool ok = client.connect(clientId.c_str());

  if (ok) {
    Serial.println("[MQTT] conectado OK");
    client.publish("ubo/g2/sistema", "ESP32 conectado");
  } else {
    Serial.print("[MQTT] fallo, estado=");
    Serial.println(client.state());
  }

  return ok;
}

void verificarMQTT() {
  if (WiFi.status() != WL_CONNECTED) return;

  if (client.connected()) return;

  if (millis() - lastMQTTTry > 5000) {
    lastMQTTTry = millis();
    conectarMQTT();
  }
}

void publicarEstado(String estado) {

  if (!client.connected()) {
    Serial.println("[ERROR] MQTT no conectado");
    return;
  }

  StaticJsonDocument<300> doc;

  doc["estado"] = estado;
  doc["palabra"] = ultimaPalabra;
  doc["contador"] = contadorAlertas;
  doc["ip"] = WiFi.localIP().toString();
  doc["rssi"] = WiFi.RSSI();
  doc["duracion"] = TIEMPO_ALERTA;
  doc["timestamp"] = millis();
  doc["destinatario"] = "Juan";
  doc["ubicacion"] = "Talagante";
  doc["mensaje"] = "ALERTA";

  String payload;
  serializeJson(doc, payload);

  bool ok = client.publish("ubo/g2/alerta", payload.c_str());

  if (ok) {
    Serial.println("[MQTT] publish OK");
  } else {
    Serial.println("[MQTT] publish FALLÓ");
  }
}

void activarAlerta(String palabra) {

  if (alertaActiva) return;

  ultimaPalabra = palabra;
  contadorAlertas++;

  digitalWrite(LED_PIN, HIGH);
  tone(BUZZER_PIN, 2000);

  alertaActiva = true;
  inicioAlerta = millis();

  publicarEstado("ACTIVA");
}

void verificarAlerta() {
  if (!alertaActiva) return;

  if (millis() - inicioAlerta > TIEMPO_ALERTA) {
    digitalWrite(LED_PIN, LOW);
    noTone(BUZZER_PIN);

    alertaActiva = false;

    publicarEstado("INACTIVA");
  }
}

void procesarSerial() {

  if (!Serial.available()) return;

  String msg = Serial.readStringUntil('\n');
  msg.trim();

  Serial.println("[RX] " + msg);

  if (msg.startsWith("ALERTA:")) {
    String palabra = msg.substring(7);
    palabra.trim();

    if (palabra.length() > 0) {
      activarAlerta(palabra);
    }
  }
}

void setup() {

  Serial.begin(115200);

  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  digitalWrite(LED_PIN, LOW);
  noTone(BUZZER_PIN);

  randomSeed(micros());

  conectarWiFi();

  client.setServer(mqtt_server, mqtt_port);

  Serial.println("[SYSTEM] iniciado");
}

void loop() {

  verificarWiFi();
  verificarMQTT();

  if (client.connected()) {
    client.loop();
  }

  procesarSerial();
  verificarAlerta();
}