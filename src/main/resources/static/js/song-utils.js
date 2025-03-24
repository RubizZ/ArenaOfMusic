// Función para obtener la portada de una canción
function getSongCover(songId, input) {
    fetch(`/admin/playlists/song/${songId}/cover`)
        .then(response => {
            if (response.status === 404) {
                // Si la ID no existe (canción no encontrada)
                alert('La canción no fue encontrada.');
                return;
            }
            if (response.status === 410) {
                // Si el archivo ya no está disponible (archivo no encontrado)
                alert('La portada de la canción ya no está disponible.');
                return;
            }
            if (!response.ok) {
                alert('Hubo un problema al obtener la portada.');
                return;
            }

            return response.blob();
        })
        .then(blob => {
            if (blob) {
                const imageUrl = URL.createObjectURL(blob);
                input.src = imageUrl;
            }
        })
        .catch(error => {
            console.error('Error fetching song cover:', error);
        });
}

// Función para obtener el archivo de audio de una canción
function getSongAudio(songId, player) {
    fetch(`/admin/playlists/song/${songId}/audio`)
        .then(response => {
            if (response.status === 404) {
                // Si la ID no existe (canción no encontrada)
                alert('La canción no fue encontrada.');
                return;
            }
            if (response.status === 410) {
                // Si el archivo de audio ya no está disponible (archivo no encontrado)
                alert('El audio de la canción ya no está disponible.');
                return;
            }
            if (!response.ok) {
                alert('Hubo un problema al obtener el archivo de audio.');
                return;
            }

            return response.blob();
        })
        .then(blob => {
            if (blob) {
                const audioUrl = URL.createObjectURL(blob);
                const audioElement = player;
                audioElement.src = audioUrl;
            }
        })
        .catch(error => {
            console.error('Error fetching song audio:', error);
        });
}

function showLoader() {
    document.querySelector('.audio-loader-container').style.display = 'flex';
}

function hideLoader() {
    document.querySelector('.audio-loader-container').style.display = 'none';
}
