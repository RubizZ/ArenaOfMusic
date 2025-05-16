window.shouldConfirmExit = true; // control global

//Función para enviar la petición POST al abandonar
function abandonarPartida() {
    const csrfToken = config.csrf.value;
    const gameId = window.gameId // Obtener el ID del juego
    console.log("Abandonando partida con ID:", gameId);
    fetch(`/partida/abandonar/${gameId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'X-CSRF-TOKEN': csrfToken
        },
    }).finally(() => {
        window.shouldConfirmExit = false;
        window.removeEventListener("beforeunload", handleBeforeUnload);
        window.location.href = "/"; // Redirigir después de abandonar
    });
}

//Manejo del diálogo nativo al intentar salir por cerrar/recargar
function handleBeforeUnload(e) {
    if (window.shouldConfirmExit) {
        e.preventDefault();
        e.returnValue = ""; // Obligatorio para mostrar el diálogo nativo
    }
}

//Manejo del botón "Salir"
function handleBotonSalir() {
    console.log("Abandonando partida con ID:", window.gameId);

    const confirmado = confirm("¿Estás seguro de que deseas abandonar la partida?");
    if (confirmado) {
        abandonarPartida();
    }
}


//Prevenir navegación hacia atrás
function handlePopState(e) {
    if (window.shouldConfirmExit) {
        const confirmado = confirm("¿Seguro que quieres salir de la partida?");
        if (!confirmado) {
            history.pushState(null, null, location.href); // Cancelar navegación
        } else {
            abandonarPartida();
        }
    }
}

// 🧷 Agregar listeners una vez cargue la página
window.addEventListener("DOMContentLoaded", () => {
    document.getElementById("botonSalir")?.addEventListener("click", handleBotonSalir);
    window.addEventListener("beforeunload", handleBeforeUnload);
    window.addEventListener("popstate", handlePopState); // atrapa botón "atrás"
    history.pushState(null, null, location.href); // previene salir directo con "atrás"
});
