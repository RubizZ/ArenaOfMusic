let gameId = null;            // UUID de la partida, lo asignas cuando cargues la vista.
let playerId = null;           // ID del jugador, lo asignas cuando cargues la vista.
let currentSongId = null;     // ID de la canción que se va a reproducir.
let currentRound = 0;         // Ronda actual.
let totalRounds = 0;           // Total de rondas, lo puedes leer desde el backend en la carga de la vista.
let timePerRound = 0;
let countdownTimer = null;    // Controlador para el temporizador.
let selectedAnswer = "";      // Última respuesta confirmada por el jugador.
let rondaFinalizada = false;
let audioURL;
let audio;
let imageURL;


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
            currentSongId = data.songId;
            actualizarVistaRonda(currentRound);  // Actualiza la UI con la nueva ronda
            obtenerCancion(currentSongId);       // Siguiente paso.
        })
        .catch(error => {
            console.error('Error iniciando ronda:', error);
        });
}

function actualizarVistaRonda(roundData) {
    document.getElementById('numeroRonda').innerText = `${roundData}`;

    const inputRespuesta = document.getElementById('songInput');
    const botonRespuesta = document.getElementById('marcarBtn');

    const overlay = document.getElementById("songTitleOverlay");

    overlay.style.display = "none";

    inputRespuesta.value = '';
    inputRespuesta.disabled = false;
    botonRespuesta.disabled = false;
    selectedAnswer = "";  // Reinicia la respuesta previa

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
        audioURL = URL.createObjectURL(blob);
        actualizarCover();
        reproducirCancion(); // devolverá la URL temporal para reproducir
    }).catch(error => {
        console.error('Error al obtener la canción:', error);
    });
}

function obtenerCover(songId) {
    fetch(`/partida/song/${songId}/cover`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error al obtener la cover: ${response.status}`);
            }
            return response.blob(); // Recibimos la imagen como Blob
        })
        .then(blob => {
            imageURL = URL.createObjectURL(blob); // Convertimos el blob en URL para usar en <img>
            actualizarCover();
        })
        .catch(error => {
            console.error('Error cargando la cover:', error);
        });
}

function actualizarCover() {
    const img = document.querySelector("#songImage");
    if (rondaFinalizada) {
        img.src = imageURL;
    } else {
        img.src = "/img/preview-song-img.jpeg";
    }
}

function reproducirCancion() {
    audio = new Audio(audioURL);

    // Cuando la canción esté lista, inicia la cuenta atrás y la reproducción.
    audio.oncanplaythrough = () => {
        audio.play();
        iniciarCuentaAtras();
    };
}

function iniciarCuentaAtras() {
    let tiempoRestante = timePerRound;
    actualizarContador(tiempoRestante);

    countdownTimer = setInterval(() => {
        tiempoRestante--;
        actualizarContador(tiempoRestante);
        if (tiempoRestante <= 0) {
            clearInterval(countdownTimer);
            if (audio) audio.pause();
            if (!rondaFinalizada)
                finalizarRonda();
            else {
                if (currentRound >= totalRounds)// Finaliza la partida si es la última ronda.
                    finalizarPartida();
                else
                    iniciarRonda(); // Reinicia la ronda si ya se ha finalizado.
            }

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
    selectedAnswer = document.getElementById('songInput').value;
}

function finalizarRonda() {
    rondaFinalizada = true;

    const inputRespuesta = document.getElementById('songInput');
    const botonRespuesta = document.getElementById('marcarBtn');

    inputRespuesta.disabled = true;
    botonRespuesta.disabled = true;

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
            reproducirCancion();
        })
        .catch(error => {
            console.error('Error enviando respuesta:', error);
        });
}

function mostrarResultadoRonda(data) {
    obtenerCover(data.songId); // obtenemos la cover con el ID 
    const overlay = document.getElementById("songTitleOverlay");
    const overlayText = document.getElementById("songTitleText");

    overlayText.textContent = data.songName;
    overlay.style.display = "block";

    // Actualizamos los puntajes de cada jugador en su tarjeta
    for (let pid in data.result) {
        const puntos = data.result[pid];
        const scoreSpan = document.getElementById(`player${pid}score`);
        const cardDiv = document.getElementById(`playerCard${pid}`);

        if (scoreSpan && cardDiv) {
            const actual = parseInt(scoreSpan.innerText);
            scoreSpan.innerText = actual + puntos;

            // Elimina cualquier animación previa
            cardDiv.classList.remove("flash-verde", "flash-rojo");

            if (puntos > 0) {
                cardDiv.classList.add("flash-verde");
            } else {
                cardDiv.classList.add("flash-rojo");
            }

            // Borra la animación tras un segundo
            setTimeout(() => {
                cardDiv.classList.remove("flash-verde", "flash-rojo");
            }, 1000);
        }
    }


}

function finalizarPartida() {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch(`/partida/finalizar/${gameId}`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
    }).then(response => {
        if (!response.ok) throw new Error(`Error finalizando partida: ${response.status}`);
        // Redirigir a la vista de resultados.
        window.location.href = `/partida/resultados`;
    }).catch(error => {
        console.error('Error al finalizar la partida:', error);
    });
}
