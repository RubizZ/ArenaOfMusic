// Partida Solitario
// Este archivo contiene la lógica para manejar la partida de un jugador en solitario.

// VARIABLES 
// VARIABLES DE CONFIGURACIÓN
let gameId = null;
let playerId = null;
let isHost = false;
let totalRounds = 0;
let timePerRound = 0;
let answerMode = null;
let answerType = null;

// VARIABLES DE CANCIONES
let currentSongId = null;
let currentRound = 0;
let audioURL;
let audio;
let imageURL;

//LISTA DE TITULOS/ARTISTAS DE CANCIONES PARA SUGERENCIAS
let availableAnswers = [];

// VARIABLES DE JUEGO
let countdownTimer = null;
let selectedAnswer = "";
let rondaFinalizada = false;

//VARIABLES DE OPCIONES
let roundSongOptions = [];


//METODOS
//LÓGICA PARTIDA
function iniciarJuego(id, player, hostId, rondas, fragmentDuration, gameAnswerMode, gameAnswerType) {
    gameId = id;
    playerId = player
    isHost = hostId === player;
    totalRounds = rondas;
    timePerRound = fragmentDuration;
    answerMode = gameAnswerMode;
    answerType = gameAnswerType;
    if (answerType === "write") {
        if (answerMode === "artist") {
            obtenerListaArtistas()
                .then(() => {
                    iniciarRonda();
                })
                .catch(error => {
                    console.error('Error obteniendo la lista de artistas:', error);
                });
        } else if (answerMode === "song") {
            obtenerListaCanciones()
                .then(() => {
                    iniciarRonda();
                })
                .catch(error => {
                    console.error('Error obteniendo la lista de canciones:', error);
                });
        }
    } else if (answerType === "options") {
        iniciarRonda();
    }
}

function iniciarRonda() {
    rondaFinalizada = false;
    const csrfToken = config.csrf.value;

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
            currentRound = data.roundNumber;
            currentSongId = data.songId;
            if (answerType === "options") {
                roundSongOptions = data.options; // Guardamos las opciones de la ronda
            }
            actualizarVistaRonda();  // Actualiza la UI con la nueva ronda
            obtenerCancion(currentSongId);       // Siguiente paso.
        })
        .catch(error => {
            console.error('Error iniciando ronda:', error);
        });
}

function finalizarRonda() {
    rondaFinalizada = true;

    actualizarVistaRonda(currentRound);  // Actualiza la UI con la nueva ronda

    clearInterval(countdownTimer);
    let respuesta = "";
    if (answerType === "write") {
        respuesta = selectedAnswer || document.querySelector("#songInput").value;
    } else if (answerType === "options") {
        const selectedOption = document.querySelector('input[name="songOption"]:checked');
        if (selectedOption) {
            respuesta = selectedOption.value;
        } else {
            respuesta = ""; // Si no hay opción seleccionada, se envía vacío
        }
    }
    const csrfToken = config.csrf.value;

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

function finalizarPartida() {
    const csrfToken = config.csrf.value;

    fetch(`/partida/finalizar/${gameId}`, {
        method: 'POST',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        },
    }).then(response => {
        if (!response.ok) throw new Error(`Error finalizando partida: ${response.status}`);
        // Redirigir a la vista de resultados.
        window.shouldConfirmExit = false;
        window.removeEventListener("beforeunload", handleBeforeUnload);

        window.location.href = `/partida/resultados/${gameId}`;
    }).catch(error => {
        console.error('Error al finalizar la partida:', error);
    });
}
//FIN LOGICA PARTIDA
//-----------------------------------------------------
//LOGICA CANCIONES
function obtenerCancion(songId) {
    const csrfToken = config.csrf.value;

    fetch(`/partida/song/${songId}/audio/${gameId}`, {
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
        actualizarCover();
        reproducirCancion();
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
    const csrfToken = config.csrf.value;

    return fetch(`/partida/obtenerTitulos`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    }).then(response => {
        if (!response.ok) throw new Error(`Error al obtener la lista: ${response.status}`);
        return response.json();
    }).then(data => {
        availableAnswers = data; // Guardamos la lista recibida
    });
}

function obtenerListaArtistas() {
    const csrfToken = config.csrf.value;

    return fetch(`/partida/obtenerArtistas`, {
        method: 'GET',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    }).then(response => {
        if (!response.ok) throw new Error(`Error al obtener la lista de artistas: ${response.status}`);
        return response.json();
    }).then(data => {
        availableAnswers = data; // Guardamos la lista recibida
    });
}
document.addEventListener('DOMContentLoaded', () => {
    if (answerType === "write") {
        const input = document.getElementById('songInput');
        input.addEventListener('input', actualizarSugerencias);
    }
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
    const sugerencias = availableAnswers
        .filter(titulo => titulo.toLowerCase().includes(valor))
        .slice(0, 5); // Limitar a 5 sugerencias

    sugerenciasDiv.innerHTML = sugerencias
        .map(titulo => `<div class="sugerencia-item">${titulo}</div>`)
        .join('');
}

function marcarRespuesta() {
    const marca = document.getElementById('songInput');
    if (marca) {
        selectedAnswer = marca.value;
    }
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

function actualizarVistaRonda() {
    document.getElementById('numeroRonda').innerText = `${currentRound}`;
    const overlay = document.getElementById("songTitleOverlay");

    if (answerType === "options") {
        const optionsContainer = document.getElementById("optionsGroup");

        if (!rondaFinalizada) {
            optionsContainer.innerHTML = ""; // Limpiamos las opciones previas
            overlay.style.display = "none";

            roundSongOptions.forEach((option, idx) => {
                const div = document.createElement("div");
                div.className = "col col-sm-12 col-md-5 option-div p-3 m-1 rounded border";
                div.style.cursor = "pointer";
                div.style.transition = "background 0.2s";

                const input = document.createElement("input");
                input.type = "radio";
                input.name = "songOption";
                input.id = "option" + idx;
                input.value = option;
                input.style.display = "none"; // Oculta el círculo

                const label = document.createElement("label");
                label.className = "w-100 h-100 m-0";
                label.htmlFor = input.id;
                label.innerText = option;
                label.style.cursor = "pointer";

                // Selección visual al hacer clic en el div
                div.onclick = () => {
                    // Desmarca todos los divs
                    document.querySelectorAll('.option-div').forEach(d => {
                        d.style.background = "";
                        d.classList.remove("border-success");
                    });
                    // Marca el input y cambia el fondo
                    input.checked = true;
                    div.style.background = "#19875433"; // Verde Bootstrap con transparencia
                    div.classList.add("border-success");
                    selectedAnswer = option;
                };

                div.appendChild(input);
                div.appendChild(label);
                optionsContainer.appendChild(div);
            });
        } else {
            const radios = optionsContainer.querySelectorAll('input[type="radio"]');
            radios.forEach(radio => {
                radio.disabled = true;
            });
        }
    } else if (answerType === "write") {
        const inputRespuesta = document.getElementById('songInput');
        const botonRespuesta = document.getElementById('marcarBtn');

        if (!rondaFinalizada) {
            overlay.style.display = "none";

            inputRespuesta.value = '';
            inputRespuesta.disabled = false;
            botonRespuesta.disabled = false;
            selectedAnswer = "";  // Reinicia la respuesta previa
        } else {
            inputRespuesta.disabled = true;
            botonRespuesta.disabled = true;
            const sugerenciasDiv = document.getElementById('sugerencias');
            sugerenciasDiv.innerHTML = ''; // Limpiamos las sugerencias
        }
    }

}

function mostrarResultadoRonda(data) {
    obtenerCover(data.songId); // obtenemos la cover con el ID 
    const overlay = document.getElementById("songTitleOverlay");
    const overlayText = document.getElementById("songTitleText");
    const overlayArtist = document.getElementById("songArtistText");

    overlayText.textContent = data.songName;
    overlayArtist.textContent = data.artists.join(", ");
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
            setTimeout(() => {  // Solo aquí la pausa de 1 segundo
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
