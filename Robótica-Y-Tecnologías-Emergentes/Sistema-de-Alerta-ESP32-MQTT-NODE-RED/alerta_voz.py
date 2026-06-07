import speech_recognition as sr
import serial
import time

PUERTO = "COM4"
BAUDIOS = 115200

PALABRAS_CLAVE = [
    "auxilio",
    "ayuda",
    "emergencia",
    "socorro",
    "peligro"
]

ser = serial.Serial(PUERTO, BAUDIOS)
time.sleep(2)

r = sr.Recognizer()

with sr.Microphone() as source:

    print("Calibrando micrófono...")
    r.adjust_for_ambient_noise(source, duration=2)

    print("Sistema iniciado")

    while True:

        try:

            audio = r.listen(source)

            texto = r.recognize_google(
                audio,
                language="es-CL"
            ).lower()

            print("Escuchado:", texto)

            for palabra in PALABRAS_CLAVE:

                if palabra in texto:

                    print("ALERTA:", palabra)

                    mensaje = f"ALERTA:{palabra}\n"

                    ser.write(
                        mensaje.encode()
                        )

                    print(
                        "Alerta enviada:",
                        palabra
                        )

                    break
                
        except sr.UnknownValueError:
            pass

        except Exception as e:
            print("Error:", e)
