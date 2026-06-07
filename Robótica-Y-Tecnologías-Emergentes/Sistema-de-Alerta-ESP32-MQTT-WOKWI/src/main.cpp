#include <Arduino.h>
#include <WiFi.h>
#include <PubSubClient.h>

bool ledManual = false;

// ----- Credenciales WiFi ------------------------------
const char *SSID = "Wokwi-GUEST"; // red simulada en WOKWI
const char *PASSWORD = "";        // sin password en WOKWI

// ----- Broker MQTT ------------------------------------
const char *BROKER = "broker.hivemq.com";
const int PORT = 1883;
const char *CLIENT_ID = "esp32-ubo-g1410";

// ---------------- TOPICS ------------------------------
const char *TOPIC_CONTROL = "ubo/g1410/control";
const char *TOPIC_POT = "ubo/g1410/sensor/pot";
const char *TOPIC_MOV = "ubo/g1410/sensor/mov";
const char *TOPIC_ALERTA = "ubo/g1410/sensor/alerta";
const char *TOPIC_BOTON = "ubo/g1410/sensor/boton";

// ----- Pines ------------------------------------------
#define PIN_LED 2
#define PIN_POT 34
#define PIN_PIR 13
#define PIN_BOTON 15

// ----- Objetos de comunicacion ------------------------
WiFiClient wifiClient;               // Capa TCP /IP
PubSubClient mqttClient(wifiClient); // Capa MQTT encima de TCP

// -----CALLBACK -------------------------------------------------------------
void mqttCallback(char *topic, byte *payload, unsigned int length)
{

  String msg = "";
  for (unsigned int i = 0; i < length; i++)
  {
    msg += (char)payload[i];
  }

  Serial.printf("[MQTT RECV] Topic: %s | Msg: %s\n", topic, msg.c_str());

  if (String(topic) == TOPIC_CONTROL)
  {

    if (msg == "ON")
    {
      ledManual = true;
      Serial.println(">> LED encendido manualmente");
    }
    else if (msg == "OFF")
    {
      ledManual = false;
      Serial.println(">> LED apagado manualmente");
    }
  }
}

// ----- RECONEXION ------------------------------------------
void reconnectMQTT()
{
  while (!mqttClient.connected())
  {
    Serial.printf("Conectando a %s:%d...\n", BROKER, PORT);

    if (mqttClient.connect(CLIENT_ID))
    {
      Serial.println("Conectado!");
      mqttClient.subscribe(TOPIC_CONTROL);
      Serial.printf("Suscrito a: %s\n", TOPIC_CONTROL);
    }
    else
    {
      Serial.printf("Error: %d\n", mqttClient.state());
      delay(5000);
    }
  }
}

void setup()
{
  Serial.begin(115200);

  pinMode(PIN_LED, OUTPUT);
  pinMode(PIN_BOTON, INPUT_PULLUP);
  pinMode(PIN_PIR, INPUT);

  digitalWrite(PIN_LED, LOW); // Inicialmente el LED apagado

  // Conectar a WiFi
  WiFi.begin(SSID, PASSWORD);
  Serial.print("Conectando WiFi");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi CONECTADO! IP: " + WiFi.localIP().toString());

  // Configurar cliente MQTT
  mqttClient.setServer(BROKER, PORT);
  mqttClient.setCallback(mqttCallback);
}

void loop()
{
  // Verificar y mantener conexion MQTT en cada iteracion
  if (!mqttClient.connected())
  {
    reconnectMQTT(); // Se llama solo si perdemos conexion
  }
  mqttClient.loop(); // OBLIGATORIO : procesa mensajes entrantes del broker

  // Publicar datos cada 2 segundos
  static unsigned long ultimaPublicacion = 0;

  if (millis() - ultimaPublicacion > 2000)
  {
    ultimaPublicacion = millis();

    int pot = analogRead(PIN_POT);
    int pir = digitalRead(PIN_PIR);
    int boton = digitalRead(PIN_BOTON);

    // -------- PUBLICAR POT --------
    char payloadPot[10];
    sprintf(payloadPot, "%d", pot);
    mqttClient.publish(TOPIC_POT, payloadPot);

    // -------- PUBLICAR MOVIMIENTO --------
    const char *estadoMov = pir ? "Movimiento detectado" : "Sin movimiento";
    mqttClient.publish(TOPIC_MOV, estadoMov);

    // -------- PUBLICAR BOTON -------------
    const char *estadoBoton = (boton == LOW) ? "Boton presionado" : "Boton no presionado";
    mqttClient.publish(TOPIC_BOTON, estadoBoton);

    // -------- LOGICA ALERTA --------
    bool alerta = (pot > 2000 || pir == 1 || boton == LOW);

    String msgAlerta;

    if (alerta)
    {
      msgAlerta = "ALERTA ACTIVADA - ";

      if (pot > 2000)
        msgAlerta += "Potenciometro alto ";
      if (pir == 1)
        msgAlerta += "Movimiento ";
      if (boton == LOW)
        msgAlerta += "Boton presionado ";
    }
    else
    {
      msgAlerta = "Alerta no activada";
    }
    mqttClient.publish(TOPIC_ALERTA, msgAlerta.c_str());

    // LED local
    digitalWrite(PIN_LED, (alerta || ledManual) ? HIGH : LOW);

    // Debug
    Serial.println("------ ESTADO ------");
    Serial.println(payloadPot);
    Serial.println(estadoMov);
    Serial.println(estadoBoton);
    Serial.println(msgAlerta);
    Serial.println("--------------------");
  }
}