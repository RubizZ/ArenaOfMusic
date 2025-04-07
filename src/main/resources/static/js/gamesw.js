"use strict"

const gameWs = {

    /**
     * Número de intentos de reconexión si falla la conexión
     */
    retries: 3,

    /**
     * Acción por defecto cuando se recibe un mensaje
     */
    receive: (text) => {
        console.log("Mensaje recibido en gamews:", text);
        // Aquí se puede manejar la lógica específica para los mensajes de las partidas
        let message = JSON.parse(text);
        // Por ejemplo, si es un mensaje de inicio de juego:
        if (message.type === "game_start") {
            console.log("El juego ha comenzado.");
            // Aquí puedes ejecutar la lógica para mostrar la interfaz de juego
        }
    },

    /**
     * Encabezados para la conexión WebSocket (se puede agregar CSRF si es necesario)
     */
    headers: { 'X-CSRF-TOKEN': config.csrf.value },

    /**
     * Variable para controlar si la conexión WebSocket está abierta
     */
    connected: false,

    /**
     * Intenta establecer la comunicación con el WebSocket de la partida
     */
    initialize: (endpoint, gameId, userId, subs = []) => {
        try {
            gameWs.stompClient = Stomp.client(endpoint);
            gameWs.stompClient.reconnect_delay = 2000;
            gameWs.stompClient.reconnect_callback = () => gameWs.retries-- > 0;
            gameWs.stompClient.connect(gameWs.headers, () => {
                gameWs.connected = true;  // Marca como conectado
                console.log('Conectado a ', endpoint, ' - suscribiendo:');
                // Suscribirse a los canales específicos de la partida solo después de la conexión
                gameWs.subscribeToChannels(subs);  // Llamar a la nueva función para suscribir
                // Enviar un mensaje de entrada de jugador o iniciar alguna acción
                gameWs.sendIfConnected(`/game/wait/${gameId}`, { "playerId": userId });
            });
            console.log("Conectado al WS '" + endpoint + "' para la partida");
        } catch (e) {
            console.log("Error al conectar al WS para la partida '" + endpoint + "' : ", e);
        }
    },

    /**
     * Suscribirse a los canales específicos de la partida
     */
    subscribeToChannels: (subs) => {
        if (gameWs.connected) {
            while (subs.length !== 0) {
                let sub = subs.pop();
                console.log(`Suscribiendo a ${sub}...`);
                try {
                    gameWs.stompClient.subscribe(sub, (m) => gameWs.receive(m.body));
                    console.log("Suscripción exitosa a " + sub);
                } catch (e) {
                    console.log("Error, no se pudo suscribir a " + sub, e);
                }
            }
        } else {
            console.log("No se puede suscribir, WebSocket aún no está conectado.");
        }
    },

    /**
     * Enviar un mensaje a través del WebSocket si está conectado
     */
    sendIfConnected: (destination, body) => {
        if (gameWs.connected) {
            try {
                
                gameWs.stompClient.send(destination, {}, JSON.stringify(body));
                console.log(`Mensaje enviado a ${destination}`);
            } catch (e) {
                console.log("Error al enviar mensaje:", e);
            }
        } else {
            console.log("No se puede enviar mensaje, WebSocket aún no está conectado.");
        }
    },

    /**
     * Enviar un mensaje a través del WebSocket
     */
    send: (destination, body) => {
        // Enviar el mensaje solo si la conexión está establecida
        gameWs.sendIfConnected(destination, body);
        
    }
};

/**
 * Funciones específicas del juego que pueden necesitarse
 */
function startGame(gameId) {
    // Lógica de inicio del juego (por ejemplo, notificar a todos los jugadores que el juego ha comenzado)
    gameWs.send(`/game/start/${gameId}`, { action: "start" });
}

function sendAnswer(gameId, answer) {
    // Enviar una respuesta del jugador durante una ronda
    gameWs.send(`/game/answer/${gameId}`, { answer: answer });
}
