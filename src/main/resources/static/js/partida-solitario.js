let gameId = null;            // UUID de la partida, lo asignas cuando cargues la vista.
let playerId = null;           // ID del jugador, lo asignas cuando cargues la vista.
let currentSongId = null;     // ID de la canción que se va a reproducir.
let currentRound = 0;         // Ronda actual.
let totalRounds = 0;           // Total de rondas, lo puedes leer desde el backend en la carga de la vista.
let timePerRound = 0;
let countdownTimer = null;    // Controlador para el temporizador.
let selectedAnswer = "";      // Última respuesta confirmada por el jugador.
let rondaFinalizada = false;

function iniciarJuego(id, players, rondas, fragmentDuration) {
    gameId = id;
    playerId = players[0].id
    totalRounds = rondas;
    timePerRound = fragmentDuration;
    
    iniciarRonda();
}

function iniciarRonda() {
    rondaFinalizada = false;
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch("/partida/inicioRonda/" + gameId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response => {
            if (!response.ok) throw new Error(`Error al iniciar la ronda: ${response.status}`);
            return response.json();
        })
        .then(data => {
            console.log(data)

            currentRound = data.roundNumber;
            currentSongId = data.song;
            actualizarVistaRonda(currentRound);  // Actualiza la UI con el número de ronda.
            obtenerCancion(currentSongId);       // Siguiente paso.
        })
        .catch(error => {
            console.error('Error iniciando ronda:', error);
        });
}

function actualizarVistaRonda(roundData) {
    // Ejemplo: Actualizas el número de ronda en tu vista
    document.getElementById('numeroRonda').innerText = `${roundData}`;

    // Limpias el input de respuesta por si quedó texto
    const inputRespuesta = document.getElementById('songInput');
    if (inputRespuesta) {
        inputRespuesta.value = '';
    }
    selectedAnswer = "";  // Reinicia la respuesta previa

    // Puedes guardar el ID de la canción en una variable global para usar luego
    window.cancionActualId = roundData.song;

    console.log("Vista actualizada para la ronda:", roundData);
}

function obtenerCancion(songId) {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch(`/partida/song/${songId}/audio`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    }).then(response => {
        if (!response.ok) {
            throw new Error("Error al obtener la canción");
        }
        return response.blob(); // asumimos que el backend envía audio como blob
    }).then(blob => {
        const audioURL = URL.createObjectURL(blob);
        reproducirCancion(audioURL); // devolverá la URL temporal para reproducir
    }).catch(error => {
        console.error('Error al obtener la canción:', error);
    });
}

function reproducirCancion(audioUrl) {
    const audio = new Audio(audioUrl);

    // Cuando la canción esté lista, inicia la cuenta atrás y la reproducción.
    audio.oncanplaythrough = () => {
        audio.play();
        iniciarCuentaAtras(audio);
    };

    audio.onended = () => {
        // Por si la canción es corta o termina antes del timer.
        finalizarRonda(audio);
    };
}

function iniciarCuentaAtras(audio) {
    let tiempoRestante = timePerRound;
    actualizarContador(tiempoRestante);

    countdownTimer = setInterval(() => {
        tiempoRestante--;
        actualizarContador(tiempoRestante);
        if (tiempoRestante <= 0) {
            clearInterval(countdownTimer);
            finalizarRonda(audio);
        }
    }, 1000);
}

function actualizarContador(tiempoRestante) {
    const countdown = document.getElementById('countdown');

    if (tiempoRestante <= 0) {
        countdown.innerText = "¡Tiempo!";
    } else {
        countdown.innerText = `${tiempoRestante}s`;
    }

}

function marcarRespuesta() {
    const selectedAnswer = document.getElementById('songInput').value;

    console.log("Respuesta marcada:", selectedAnswer);
}

function finalizarRonda(audio) {
    if (rondaFinalizada) return;
    rondaFinalizada = true;

    if (audio) audio.pause();
    clearInterval(countdownTimer);

    let respuesta = selectedAnswer || document.querySelector("#songInput").value;

    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    // Construir el Map en JSON: { playerId: "respuesta" }
    const body = {};
    body[playerId] = respuesta;

    fetch(`/partida/finRonda/${gameId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(body)
    })
    .then(response => {
        if (!response.ok) throw new Error(`Error enviando respuesta: ${response.status}`);
        return response.json();
    })
    .then(data => {
        mostrarResultadoRonda(data);
        reproducirFragmentoDeNuevo();
    })
    .catch(error => {
        console.error('Error enviando respuesta:', error);
    });
}



function reproducirFragmentoDeNuevo() {
    // Esperar un par de segundos para mostrar resultado y volver a reproducir fragmento.
    // Lógica similar a reproducirCancion(), pero con la imagen de la canción ya mostrada.
    // Al terminar: comprobar si es la última ronda, o iniciar otra ronda.
    setTimeout(() => {
        if (currentRound >= totalRounds) {
            finalizarPartida();
        } else {
            iniciarRonda();
        }
    }, 5000);

    // Ejemplo: 5 segundos para mostrar resultados antes de pasar.
}

function finalizarPartida() {
    fetch(`/partida/finalizar/${gameId}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) throw new Error(`Error finalizando partida: ${response.status}`);
            // Redirigir a la vista de resultados.
            window.location.href = `/partida/resultados/${gameId}`;
        })
        .catch(error => {
            console.error('Error al finalizar la partida:', error);
        });
}
