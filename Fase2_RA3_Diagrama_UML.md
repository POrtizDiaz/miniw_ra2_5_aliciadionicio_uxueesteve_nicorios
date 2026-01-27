# Fase 2 - RA3: Sockets (UDP y Multicast)
## Diagrama UML - Sesión 13 de enero

### Arquitectura General

```
┌─────────────────────────────────────────────────────┐
│                  RED DE COMUNICACION                 │
│                 (UDP / Multicast)                    │
└──────────────┬──────────────────────────────┬────────┘
               │                              │
        ┌──────▼──────┐              ┌───────▼──────┐
        │   FITA 3    │              │   FITA 4     │
        │  UDP punto  │              │   MULTICAST  │
        │  a punto    │              │ (un servidor,│
        │             │              │  múltiples   │
        │             │              │   clientes)  │
        └──────┬──────┘              └───────┬──────┘
               │                             │
        ┌──────┴──────┐              ┌──────┴─────────┐
        │             │              │                │
   ┌────▼─────┐  ┌───▼──────┐   ┌───▼────────┐  ┌──▼────────┐
   │ClientUDP │  │ServidorUDP   │ServidorMulti   ClientMulti
   │          │  │          │   │cast         │  │cast (N)    │
   │Puerto: X │  │Puerto: Y │   │Puerto: 7000 │  │Puerto: 7000│
   │IP: local │  │IP: local │   │IP: 230.x.x │  │IP: 230.x.x │
   └──────────┘  └──────────┘   │Grupo:privado   └────────────┘
                                 └─────────────┘
```

### FITA 3: UDP (Cliente-Servidor bidireccional)

#### Clases principales:

```
┌─────────────────────────────┐
│    ClientUDP                │
├─────────────────────────────┤
│ - puerto: int               │
│ - ipServidor: String        │
│ - socket: DatagramSocket    │
├─────────────────────────────┤
│ + main(args: String[])      │
│ + enviarMensaje(String)     │
│ + recibirMensaje(): String  │
└─────────────────────────────┘
         │
         │ conecta con
         │
         ▼
┌──────────────────────────────┐
│    ServidorUDP               │
├──────────────────────────────┤
│ - puerto: int                │
│ - socket: DatagramSocket     │
├──────────────────────────────┤
│ + main(args: String[])       │
│ + recibirMensaje()           │
│ + enviarMensaje(respuesta)   │
└──────────────────────────────┘
```

**Flujo UDP:**
```
ClientUDP                          ServidorUDP
   │                                  │
   │──────── DatagramPacket ─────────▶│
   │                                  │
   │                         [Procesa]│
   │                                  │
   │◀────── DatagramPacket ───────────│
   │                                  │
```

---

### FITA 4: MULTICAST (Servidor a múltiples clientes)

#### Clases principales:

```
┌──────────────────────────────┐
│    ServidorMulticast         │
├──────────────────────────────┤
│ - grupo: String = "230.0.0.1"│
│ - puerto: int = 7000         │
│ - socket: MulticastSocket    │
├──────────────────────────────┤
│ + main(args: String[])       │
│ + enviarMensajeGrupo()       │
│ + run() [bucle infinito]     │
└──────────────────────────────┘
         │
         │ envía a (230.0.0.1:7000)
         │
    ┌────┴─────────────────────┐
    │                           │
    ▼                           ▼
┌──────────────────┐    ┌──────────────────┐
│ ClientMulticast1 │    │ ClientMulticast2 │
├──────────────────┤    ├──────────────────┤
│ - grupo: String  │    │ - grupo: String  │
│ - socket: Multi  │    │ - socket: Multi  │
├──────────────────┤    ├──────────────────┤
│ + joinGroup()    │    │ + joinGroup()    │
│ + receive()      │    │ + receive()      │
└──────────────────┘    └──────────────────┘
       ▲                       ▲
       │                       │
       └───── ... N clientes──┘
```

**Flujo Multicast:**
```
ServidorMulticast (Envía cada 3 segundos)
   │
   ├──┬──────────────────────────────┬─────┐
   │  │                              │     │
   ▼  ▼                              ▼     ▼
Client1  Client2                  Client3  Client...N
  ✓        ✓                         ✓      ✓
```
---

### FITA 5: MINICHAT TCP (Servidor multihilo con broadcast)

#### Clases principales:

```
┌──────────────────────────────────┐
│    ServidorMinichat              │
├──────────────────────────────────┤
│ - PUERTO: int = 5000             │
│ - serverSocket: ServerSocket     │
│ - clientes: List<ClientHandler>  │
├──────────────────────────────────┤
│ + main(args: String[])           │
│ + broadcast(mensaje, remitente)  │
│ + desconectarCliente(cliente)    │
│ + log(mensaje)                   │
└──────────────────────────────────┘
         │
         │ acepta y crea
         │
         ▼
┌──────────────────────────────────┐
│    ClientHandler                 │
│    implements Runnable           │
├──────────────────────────────────┤
│ - socket: Socket                 │
│ - entrada: BufferedReader        │
│ - salida: PrintWriter            │
│ - idCliente: String              │
├──────────────────────────────────┤
│ + run()                          │
│ + enviarMensaje(mensaje)         │
│ - cerrar()                       │
└──────────────────────────────────┘
         ▲
         │ se conectan múltiples
         │
    ┌────┴─────────────────────┐
    │                           │
    ▼                           ▼
┌──────────────────┐    ┌──────────────────┐
│ ClientMinichat1  │    │ ClientMinichat2  │
├──────────────────┤    ├──────────────────┤
│ - socket: Socket │    │ - socket: Socket │
│ - entrada: BR    │    │ - entrada: BR    │
│ - salida: PW     │    │ - salida: PW     │
│ - nombre: String │    │ - nombre: String │
├──────────────────┤    ├──────────────────┤
│ + iniciar()      │    │ + iniciar()      │
│ - recibirMsg()   │    │ - recibirMsg()   │
│ - enviarMsg()    │    │ - enviarMsg()    │
└──────────────────┘    └──────────────────┘
       ▲                       ▲
       │                       │
       └───── ... N clientes──┘
```

**Flujo MINICHAT TCP:**
```
        ServidorMinichat (puerto 5000)
                │
        ┌───────┴───────┐
        │ Hilo principal│ (acepta conexiones)
        └───────┬───────┘
                │
    ┌───────────┼───────────┐
    │           │           │
┌───▼────┐  ┌──▼─────┐  ┌──▼─────┐
│Hilo    │  │Hilo    │  │Hilo    │
│Cliente1│  │Cliente2│  │Cliente3│
└───┬────┘  └──┬─────┘  └──┬─────┘
    │          │           │
    │  ┌───────┼───────┐   │
    └─▶│   BROADCAST   │◀──┘
       │ (a todos menos│
       │   remitente)  │
       └───────────────┘

Cliente1 envía: "Hola"
    │
    ├──▶ Servidor recibe
    │
    ├──▶ Broadcast → Cliente2 ✓
    └──▶ Broadcast → Cliente3 ✓
```

**Arquitectura de hilos:**
```
┌─────────────────────────────────────┐
│         Servidor Principal          │
│      (Hilo aceptador infinito)      │
└──────────────┬──────────────────────┘
               │
     ┌─────────┼─────────┐
     │         │         │
┌────▼─────┐ ┌▼────────┐ ┌▼──────────┐
│ClientH1  │ │ClientH2 │ │ClientH3   │
│(Runnable)│ │(Runnable)│ │(Runnable) │
│          │ │         │ │           │
│ escucha  │ │ escucha │ │ escucha   │
│ mensajes │ │ mensajes│ │ mensajes  │
└──────────┘ └─────────┘ └───────────┘
     │            │           │
     └────────────┼───────────┘
                  │
         Lista compartida
      (sincronizada con locks)
```

---
## Características de implementación:

### FITA 3 - UDP
-  Protocolo sin conexión
-  Datagramas independientes
-  Comunicación bidireccional
-  Puerto dinámico (cliente) / Fijo (servidor)
-  IP: localhost (127.0.0.1)

### FITA 4 - MULTICAST
-  Rango multicast: 224.0.0.0 - 239.255.255.255
-  Grupo: 230.0.0.1 (privado)
-  Puerto: 7000
-  Servidor envía continuamente
-  Múltiples clientes pueden recibir simultáneamente
-  Los clientes se unen al grupo (joinGroup)
-  Comunicación unidireccional (servidor → clientes)

### FITA 5 - MINICHAT TCP
-  Protocolo TCP con conexión persistente
-  Puerto: 5000
-  Arquitectura multihilo (1 hilo por cliente)
-  Lista sincronizada de clientes conectados
-  Broadcast de mensajes (todos menos remitente)
-  Lectura y escritura paralela en cliente
-  Logs con timestamp de conexión/desconexión
-  Comando `/salir` para desconexión limpia
-  BufferedReader/PrintWriter para mensajería
-  Comunicación bidireccional full-duplex

---

## Pruebas recomendadas (Tester):

### FITA 3 y 4:
```bash
# Terminal 1: Iniciar servidor multicast
java -cp target/classes ra3.ProvaFase2.Fita4.ServidorMulticast

# Terminal 2, 3, 4, etc.: Iniciar múltiples clientes
java -cp target/classes ra3.ProvaFase2.Fita4.ClientMulticast
java -cp target/classes ra3.ProvaFase2.Fita4.ClientMulticast
java -cp target/classes ra3.ProvaFase2.Fita4.ClientMulticast
```

**Resultado esperado:** Todos los clientes reciben los mismos mensajes del servidor.

### FITA 5:
```bash
# Terminal 1: Iniciar servidor minichat
java -cp target/classes ra3.ProvaFase2.Fita5.ServidorMinichat

# Terminales 2, 3, 4, etc.: Iniciar clientes con diferentes nombres
java -cp target/classes ra3.ProvaFase2.Fita5.ClientMinichat Alice
java -cp target/classes ra3.ProvaFase2.Fita5.ClientMinichat Bob
java -cp target/classes ra3.ProvaFase2.Fita5.ClientMinichat Charlie

# Enviar mensajes desde cualquier cliente
> Hola a todos
> ¿Cómo estáis?
> /salir  (para desconectar)
```

**Resultado esperado:** 
- Cada mensaje aparece en todos los demás clientes
- Logs del servidor muestran conexiones/desconexiones
- Los clientes pueden enviar y recibir simultáneamente

---

## Consideraciones de Seguridad:

### General:
1. Validación de direcciones multicast
2. Manejo de excepciones (IOException, SocketException)
3. Cierre de sockets con try-with-resources
4. Sincronización en caso de acceso concurrente
5. Considerar encriptación si es necesario (DTLS para UDP)
6. Control de firewall en puertos 7000 y rango UDP

### FITA 5 (Minichat):
1. **Sincronización de lista compartida** - Uso de Collections.synchronizedList()
2. **Cierre de recursos** - Manejo en bloque finally de cada ClientHandler
3. **Control de excepciones** - Try-catch en bucles de lectura
4. **Prevención de race conditions** - Método synchronized en enviarMensaje()
5. **Logs de auditoría** - Registro de todas las conexiones y desconexiones
6. **Validación de entrada** - Verificar mensajes nulos o vacíos
7. **Límite de clientes** - Considerar un máximo de conexiones simultáneas
8. **Timeout de inactividad** - Socket timeout para clientes inactivos
9. **Sanitización de mensajes** - Prevenir inyección de comandos especiales
10. **Autenticación** - En fase posterior añadir login/registro

---

**Sesión del 13 de enero de 2026**
- Backend & Security:  Implementación completada (Fita 3, 4)
- Tester: Pruebas con múltiples clientes
- Documentador: Diagrama UML completado

**Sesión del 20 de enero de 2026**
- Backend & Security: Implementación Fita 5 (Minichat multihilo)
- Documentador: Diagrama UML Fita 5 completado
