let shouldConfirmExit = true;

// POST para abandonar partida (con CSRF y keepalive)
function abandonarPartida() {
    const csrfToken = config.csrf.value;
    const gameId = window.gameId;
    console.log("Abandonando partida con ID:", gameId);
    return fetch(`/partida/abandonar/${gameId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        },
        keepalive: true // Importante para funcionar en beforeunload
    }).finally(() => {
        shouldConfirmExit = false;
        window.removeEventListener("beforeunload", handleBeforeUnload);
        window.location.href = "/";
    });
}

// Botón "Salir"
function handleBotonSalir() {
    const confirmado = confirm("¿Estás seguro de que deseas abandonar la partida?");
    if (confirmado) {
        abandonarPartida();
    }
}

function handleBeforeUnload(e) {
    if (shouldConfirmExit) {
        abandonarPartida(); // Se hace la petición sin redirección
        e.preventDefault();
        e.returnValue = ""; // Esto es lo que activa el diálogo nativo
    }
}


// Navegar hacia atrás
function handlePopState() {
    if (shouldConfirmExit) {
        const confirmado = confirm("¿Seguro que quieres salir de la partida?");
        if (confirmado) {
            abandonarPartida(); // no redirige
        } else {
            history.pushState(null, null, location.href); // cancela navegación
        }
    }
}

// Excepciones donde no debe activarse la confirmación
function registrarExcepcionTemporal(selector) {
    const elem = document.querySelector(selector);
    if (elem) {
        elem.addEventListener("click", () => {
            shouldConfirmExit = false;
        });
    }
}

window.addEventListener("DOMContentLoaded", () => {
    if (document.cookie.includes("partidaAbandonada=true")) {
        // Borrar la cookie para evitar redirección futura
        document.cookie = "partidaAbandonada=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        window.location.href = "/";
    }
    shouldConfirmExit = true;
    document.getElementById("botonSalir").addEventListener("click", handleBotonSalir);
    window.addEventListener("beforeunload", handleBeforeUnload);
    window.addEventListener("popstate", handlePopState);
    history.pushState(null, null, location.href);

    // Desactivar confirmación si se hace clic en estos botones
    registrarExcepcionTemporal("#startButton");
});
