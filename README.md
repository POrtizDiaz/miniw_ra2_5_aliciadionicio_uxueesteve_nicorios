📖 README - Guía de Pruebas y Capturas Fase 4 (RA5)
⚠️ IMPORTANTE PARA TODAS LAS CAPTURAS: Según el PDF de la práctica, todas las capturas de pantalla deben mostrar l'hora del sistema visible (la barra de tareas de tu Windows/Mac con el reloj). No recortes solo la ventana de la terminal, captura la pantalla completa o la ventana junto con el reloj.

🟢 FITA 1 — Cifrado Simétrico (AES/GCM)
Objetivo del PDF: Demostrar que los mensajes MSG viajan cifrados usando una clave precompartida (AES), mientras que LOGIN, LIST y QUIT viajan en texto plano.

Pasos de ejecución:
Asegúrate de que el puerto 5000 está libre.

Ejecuta el ServidorEscalable de la carpeta Fita 1.

Ejecuta dos instancias de ClientSimple de la carpeta Fita 1 (las llamaremos Cliente A y Cliente B).

Qué probar y capturar:
Paso 1: Conexión y Texto Plano

En el Cliente A escribe: LOGIN Anna

En el Cliente B escribe: LOGIN Bernat

Lo que demuestra: Que el comando LOGIN no está cifrado y funciona normal.

Paso 2: Mensajes Cifrados en Tránsito y Desencriptados

En el Cliente A escribe: MSG Hola Bernat, esto es un secreto.

Ve a la consola del Servidor. Deberías ver un texto tipo: MSG Desxifrat al servidor -> Anna: Hola Bernat... (o el log que hayamos puesto para demostrar que ha llegado cifrado y el servidor ha podido abrirlo).

Ve a la consola del Cliente B. Debería aparecer el mensaje en claro.

📸 CAPTURA 1: Haz una captura donde se vean las tres consolas (Cliente A enviando, Servidor recibiendo/desencriptando, Cliente B leyendo). Esta captura justifica el punto del PDF: "Missatges xifrats en trànsit i Missatges desxifrats al servidor".

🔵 FITA 2 — RSA, Intercambio de Claves y Validación Robusta
Objetivo del PDF: Demostrar el Handshake (RSA), que la clave AES ahora es dinámica, que hay validación de errores y (como extra) los roles y el HASH SHA-256.

Pasos de ejecución:
Detén los procesos de la Fita 1.

Ejecuta el ServidorEscalable de la carpeta Fita 2.

Ejecuta dos instancias de ClientSimple de la carpeta Fita 2 (Cliente A y Cliente B).

Qué probar y capturar:
Paso 1: Intercambio de claves (Handshake RSA/AES)

Nada más arrancar el Servidor, fíjate que pone: "Generant parell de claus RSA...".

Nada más arrancar un Cliente, fíjate que pone: "Connectant al servidor i negociant claus..." y luego "Connexió SEGURA establerta!".

📸 CAPTURA 2: Captura la consola del Servidor y del Cliente justo al arrancar, demostrando el intercambio exitoso de claves (Handshake).

Paso 2: Validación de entradas y control de errores

En el Cliente A, intenta hacer un login inválido (por ejemplo, con caracteres raros o muy corto): LOGIN @#!! o LOGIN ab.

El servidor debe rechazarlo con un mensaje de ERROR Nom d'usuari invàlid....

📸 CAPTURA 3: Captura este momento. Esto justifica el punto del PDF: "Errors gestionats correctament / Validacions implementades".

Paso 3: Excelencia - Roles de Usuario (Admin vs User)

En el Cliente A escribe un nombre correcto: LOGIN AdminUser. El servidor le asignará el rol [Rol: ADMIN].

En el Cliente B escribe: LOGIN NormalUser. El servidor le asignará el rol [Rol: USER].

En el Cliente B (el NormalUser) escribe: LIST. Le debe dar un ERROR Permís denegat....

En el Cliente A (el Admin) escribe: LIST. Le debe mostrar la lista de usuarios.

📸 CAPTURA 4: Captura las consolas demostrando que el sistema de permisos funciona.

Paso 4: Excelencia - Integridad (SHA-256)

En el Cliente A escribe: MSG Validando la integridad del mensaje con SHA-256.

Ve a la consola del Servidor y observa que procesa el "MSG Segur". (Opcionalmente, si intentaras enviar un mensaje manipulado o sin el hash, saltaría el error de integridad).

📸 CAPTURA 5: Captura el envío de un mensaje normal en la Fita 2 para demostrar que toda la comunicación fluye correctamente tras el Handshake.

📝 Resumen para copiar y pegar en el documento PDF de entrega:
Cuando redactéis el PDF, usad estas descripciones para acompañar las capturas:

Fita 1: "A la Captura 1 demostrem com s'ha integrat AES. El client encripta el missatge abans d'enviar-lo (el protocol base no canvia, només s'intercepta MSG). El servidor rep una cadena en Base64, la desxifra amb la clau precompartida per veure'n el contingut, i la torna a xifrar per fer-ne el broadcast."

Fita 2 (RSA): "A la Captura 2 es veu l'intercanvi de claus. El servidor genera les claus RSA en arrencar. El client, en connectar-se, rep la clau pública, genera una clau AES de sessió, i l'envia xifrada amb RSA al servidor."

Fita 2 (Validacions i Excel·lència): "Hem anat més enllà dels requisits bàsics. Com es veu a les Captures 3 i 4, el sistema valida amb expressions regulars els noms d'usuari i gestiona excepcions. A més, hem implementat un sistema de rols (ADMIN/USER) on només l'administrador pot llistar usuaris, i una verificació d'integritat amb SHA-256 per cada missatge xifrat."