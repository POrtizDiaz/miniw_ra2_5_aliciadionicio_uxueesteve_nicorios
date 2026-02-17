🧹 PAS PREVI: Com assegurar que el port 5000 està lliure
Abans de començar qualsevol prova, si us surt l'error Address already in use, heu de matar el procés antic.

Opció fàcil (des de l'IDE):

Busca a la pestanya de baix ("Run" o "Console") tots els botons vermells (quadrats) de "Stop" i clica'ls tots fins que estiguin grisos (apagats).

Opció "Hacker" (si l'IDE falla):

Obre la terminal (PowerShell o CMD).

Escriu: netstat -ano | findstr :5000

Si surt alguna cosa, fixa't en el número del final (el PID, ex: 12345).

Escriu: taskkill /F /PID 12345 (canvia 12345 pel número que t'hagi sortit).

📸 PART 1: Provar i Documentar la FITA 2 (Sessions i Xat)
Objectiu: Demostrar que el xat funciona, que els usuaris tenen nom i que no es poden repetir noms.

1. Execució:

Ves al paquet ra4.Fita2_Sessions.server.

Executa SecureChatServer.java (Botó dret -> Run).

Ves al paquet ra4.Fita2_Sessions.client.

Executa ClientSimple.java dues vegades (per tenir dos usuaris, diguem-ne Usuari A i Usuari B).

2. Què fer (Guió de prova):

Finestra A: Escriu LOGIN Anna. (Resposta esperada: OK Benvingut Anna).

Finestra B: Escriu LOGIN Anna. (Resposta esperada: ERROR L'usuari Anna ja existeix). <-- Fes captura d'això!

Finestra B: Escriu LOGIN Bernat. (Resposta: OK Benvingut Bernat).

Finestra A: Escriu MSG Hola Bernat, com estàs?.

Finestra B: Mira si ha rebut el missatge. <-- Fes captura d'això!

Finestra A: Escriu LIST. (Haureu de veure Anna i Bernat). <-- Fes captura d'això!

Finestra B: Escriu QUIT.

3. Captures per al PDF (Fita 2):

Captura 1: Intent de Login duplicat (Error) i Login correcte.

Captura 2: Intercanvi de missatges entre dos clients.

Captura 3: La comanda LIST mostrant els usuaris connectats.

🚀 PART 2: Provar i Documentar la FITA 3 (Càrrega i Escalabilitat)
Objectiu: Demostrar que el servidor aguanta 100 usuaris de cop gràcies al Thread Pool.

1. Preparació:

ATURA el servidor de la Fita 2 (botó Stop vermell).

Assegura't que el port 5000 està lliure.

2. Execució:

Ves al paquet ra4.Fita3_Carrega.server.

Executa ServidorEscalable.java. (Fixa't que posa "Servidor ESCALABLE" a la consola).

Ves al paquet ra4.Fita3_Carrega.client.

Executa LoadTest.java.

3. Què observar:

Veureu que la consola del client comença a escopir línies: Bot-1 ha acabat, Bot-45 ha acabat, etc.

Veureu que la consola del servidor rep moltes connexions.

El més important: No ha de donar error vermell.

4. Captures per al PDF (Fita 3):

Captura 4: La consola del ServidorEscalable plena d'activitat entrant.

Captura 5: La consola del LoadTest mostrant com els 100 bots acaben la feina (com el log que m'has passat abans).

📝 PART 3: Redacció del PDF (El que demana el professor)
Al document PDF, a més de les captures, la teva companya ha d'escriure el següent (pots copiar-li aquest esquema):

1. Explicació Tècnica
Per a la Fita 2 (Sessions):

"Hem utilitzat un ConcurrentHashMap a la classe UserStorage per guardar els usuaris connectats. Això és millor que un ArrayList perquè permet buscar usuaris pel seu nom ràpidament i és segur quan hi ha molts fils (thread-safe). El protocol gestiona comandes com LOGIN, MSG i LIST per controlar l'estat."

Per a la Fita 3 (Escalabilitat):

"Per fer el servidor escalable, hem substituït la creació il·limitada de fils (new Thread().start()) per un ExecutorService (Thread Pool) amb una mida fixa de 50 fils.

Això protegeix el servidor: si entren 1000 usuaris de cop, el servidor només en processa 50 a la vegada i la resta fan cua. Això evita que el servidor col·lapsi per falta de memòria RAM (evita atacs DoS)."

2. Decisions Tècniques preses
"Hem separat el codi en paquets (server, client, handler, protocol) per mantenir l'ordre."

"Hem creat una classe LoadTest específica per simular l'estrès del sistema amb 100 bots automàtics."

3. Rols i Tasques
(Aquí poseu qui ha fet què. Exemple:)

[El teu nom]: Programació del nucli del servidor i implementació del Thread Pool.

[Nom companya]: Proves de càrrega, validació de la lògica de sessions i documentació del projecte.