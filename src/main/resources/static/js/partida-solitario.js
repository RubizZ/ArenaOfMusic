let gameId = null;            // UUID de la partida, lo asignas cuando cargues la vista.
let playerId = null;           // ID del jugador, lo asignas cuando cargues la vista.
let isHost = false;            // host, lo asignas cuando cargues la vista.
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
let availableSongs = [];


//LÓGICA PARTIDA
function iniciarJuego(id, player, hostId, rondas, fragmentDuration) {
    gameId = id;
    playerId = player
    isHost = hostId === player;
    totalRounds = rondas;
    timePerRound = fragmentDuration;

    obtenerListaCanciones()
        .then(() => {
            iniciarRonda();
        })
        .catch(error => {
            console.error('Error obteniendo la lista de canciones:', error);
        });
}

function iniciarRonda() {
    rondaFinalizada = false;
    obtenerInfoRonda().then(() => {
        actualizarVistaRonda(currentRound);  // Actualiza la UI con la nueva ronda
        reproducirCancion().then(() => {
            finalizarRonda(); // Finaliza la ronda si es la última.
        }).catch(error => {
            console.error('Error reproduciendo la canción:', error);
        });
    }).catch(error => {
        console.error('Error obteniendo info ronda:', error);
    });
}


function finalizarRonda() {
    rondaFinalizada = true;
    clearInterval(countdownTimer);
    enviarRespuesta().then(() => {
        obtenerRespuestas().then(() => {
            actualizarVistaRonda(currentRound);  // Actualiza la UI con la nueva ronda
        }).catch(error => {
            console.error('Error obteniendo las respuestas:', error);
        });
    }).catch(error => {
        console.error('Error enviando las respuestas:', error);
    });

    reproducirCancion().then(() => {
        if (currentRound >= totalRounds) { // Finaliza la partida si es la última ronda.
            finalizarPartida();
        } else {
            avanzarRonda();
        }
    });
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
        window.location.href = `/partida/resultados/${gameId}`;
    }).catch(error => {
        console.error('Error al finalizar la partida:', error);
    });
}

function avanzarRonda() {
    if (isHost) {
        const csrfToken = document.querySelector('input[name="_csrf"]').value;

        fetch(`/partida/ronda/avanzar/${gameId}`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
        }).then(response => {
            if (!response.ok) throw new Error(`Error al avanzar ronda: ${response.status}`);
            return response.json();
        }).then(data => {
            iniciarRonda();
        }).catch(error => {
            console.error('Error al avanzar ronda:', error);
        });
    }
}


function obtenerInfoRonda() {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch(`/partida/ronda/info/${gameId}`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
    }).then(response => {
        if (!response.ok) throw new Error(`Error al obtener info ronda: ${response.status}`);
        return response.json();
    }).then(data => {
        currentRound = data.roundNumber;
        currentSongId = data.songId;
        return obtenerCancion(currentSongId);       // Siguiente paso.
    }).catch(error => {
        console.error('Error obteniendo info ronda:', error);
    });
}


function enviarRespuesta() {
    let respuesta = selectedAnswer || document.querySelector("#songInput").value;

    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    // Construir el Map en JSON: { playerId: "respuesta" }
    const body = {};
    body[playerId] = respuesta;

    fetch(`/partida/ronda/respuestas/${gameId}`, {
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
        .catch(error => {
            console.error('Error enviando respuesta:', error);
        });
}


function obtenerRespuestas() {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    fetch(`/partida/ronda/resultados/${gameId}`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
    }).then(response => {
        if (!response.ok) throw new Error(`Error al obtener respuestas: ${response.status}`);
        return response.json();
    }).then(data => {
        mostrarResultadoRonda(data);
    }).catch(error => {
        console.error('Error obteniendo respuestas:', error);
    });
}

//FIN LOGICA PARTIDA
//-----------------------------------------------------
//LOGICA CANCIONES
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
        return response.blob();
    }).then(blob => {
        audioURL = URL.createObjectURL(blob);
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
//FIN LOGICA CANCIONES
//-----------------------------------------------------
//LÓGICA SUGERENCIAS
function obtenerListaCanciones() {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    return fetch(`/partida/obtenerTitulos`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response => {
            if (!response.ok) throw new Error(`Error al obtener la lista: ${response.status}`);
            return response.json();
        })
        .then(data => {
            availableSongs = data; // Guardamos la lista recibida
        });
}

document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('songInput');
    input.addEventListener('input', actualizarSugerencias);
});

document.addEventListener('click', (e) => {
    if (e.target.classList.contains('sugerencia-item')) {
        const input = document.getElementById('songInput');
        input.value = e.target.innerText;
        document.getElementById('sugerencias').innerHTML = '';
    }
});

function actualizarSugerencias() {
    const input = document.getElementById('songInput');
    const valor = input.value.trim().toLowerCase();
    const sugerenciasDiv = document.getElementById('sugerencias');

    if (valor.length < 2) {
        sugerenciasDiv.innerHTML = '';
        return;
    }

    // Filtra los títulos que contengan el texto en cualquier parte
    const sugerencias = availableSongs
        .filter(titulo => titulo.toLowerCase().includes(valor))
        .slice(0, 5); // Limitar a 5 sugerencias

    sugerenciasDiv.innerHTML = sugerencias
        .map(titulo => `<div class="sugerencia-item">${titulo}</div>`)
        .join('');
}

function marcarRespuesta() {
    selectedAnswer = document.getElementById('songInput').value;
}
//FIN LÓGICA SUGERENCIAS
//-----------------------------------------------------
//LOGICA ACTUALIZACIÓN VISTAS
function actualizarCover() {
    const img = document.querySelector("#songImage");
    if (rondaFinalizada) {
        img.src = imageURL;
    } else {
        img.src = "/img/preview-song-img.jpeg";
    }
}

function actualizarVistaRonda(roundData) {
    document.getElementById('numeroRonda').innerText = `${roundData}`;

    const inputRespuesta = document.getElementById('songInput');
    const botonRespuesta = document.getElementById('marcarBtn');

    const overlay = document.getElementById("songTitleOverlay");

    if (!rondaFinalizada) {
        overlay.style.display = "none";

        inputRespuesta.value = '';
        inputRespuesta.disabled = false;
        botonRespuesta.disabled = false;
        selectedAnswer = "";  // Reinicia la respuesta previa
    } else {
        inputRespuesta.disabled = true;
        botonRespuesta.disabled = true;
    }
    actualizarCover();
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
//FIN LOGICA ACTUALIZACIÓN VISTAS
//-----------------------------------------------------
//LOGICA REPRODCCION DE CANCIONES
function iniciarCuentaAtrasInicial(callback) {
    const countdown = document.getElementById('countdown');
    const mensajes = ["Preparados...", "Listos...", "¡YA!"];
    let indice = 0;

    const preparacionTimer = setInterval(() => {
        countdown.innerText = mensajes[indice];
        indice++;

        if (indice > mensajes.length) {
            clearInterval(preparacionTimer);
            if (callback) callback(); // Cuando termina, llama a la función para empezar la ronda normal
        }
    }, 1000);
}

function reproducirCancion() {
    audio = new Audio(audioURL);

    // Cuando la canción esté lista, inicia la cuenta atrás y la reproducción.
    audio.oncanplaythrough = () => {
        if (!rondaFinalizada) {
            iniciarCuentaAtrasInicial(() => {
                audio.play();
                iniciarCuentaAtras(); // <-- Tu cuenta regresiva normal de tiempo por ronda
            });
        } else {
            setTimeout(() => {  // Una pausa de 1'5 segundos entre fin primera reproducción y comienzo de la segunda.
                audio.play();
                iniciarCuentaAtras();
            }, 1500);
        }
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
//FIN LOGICA REPRODUCCION DE CANCIONES
