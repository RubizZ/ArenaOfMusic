// Función para obtener la portada de una canción
async function getSongCover(songId) {
    try {
        const response = await fetch(`/admin/playlists/song/${songId}/cover`);

        if (response.status === 404) {
            // Si la ID no existe (canción no encontrada)
            alert('La canción no fue encontrada.');
            return null;
        }
        if (response.status === 410) {
            // Si el archivo ya no está disponible (archivo no encontrado)
            alert('La portada de la canción ya no está disponible.');
            return null;
        }
        if (!response.ok) {
            alert('Hubo un problema al obtener la portada.');
            return null;
        }

        const blob = await response.blob();
        return URL.createObjectURL(blob);
    } catch (error) {
        console.error('Error fetching song cover:', error);
        return null;
    }
}

// Función para obtener el archivo de audio de una canción
async function getSongAudio(songId) {
    try {
        const response = await fetch(`/admin/playlists/song/${songId}/audio`);

        if (response.status === 404) {
            // Si la ID no existe (canción no encontrada)
            alert('La canción no fue encontrada.');
            return null;
        }
        if (response.status === 410) {
            // Si el archivo de audio ya no está disponible (archivo no encontrado)
            alert('El audio de la canción ya no está disponible.');
            return null;
        }
        if (!response.ok) {
            alert('Hubo un problema al obtener el archivo de audio.');
            return null;
        }

        const blob = await response.blob();
        return URL.createObjectURL(blob);
    } catch (error) {
        console.error('Error fetching song audio:', error);
        return null;
    }

}

function showLoader() {
    document.querySelector('.audio-loader-container').style.display = 'flex';
}

function hideLoader() {
    document.querySelector('.audio-loader-container').style.display = 'none';
}
