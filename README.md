# Arena of Music (IW 2024/2025)

## Descripción

**Arena of Music (AoM)** es un juego interactivo y competitivo basado en la música, donde los jugadores, utilizando una serie de playlists predeterminadas y actualizadas frecuentemente, pueden demostrar sus conocimientos musicales en diferentes modos de juego.

---

## Funcionalidades Principales (Versión entrega final: 4 de junio 2025)

### Partidas

#### Partida Individual

Modo de juego en solitario donde el jugador:

- Configura la partida:
  1. Selecciona la playlist sobre la que deseas jugar.
  2. Decide el numero de rondas que quieres jugar y la duración que tendrá cada fragmento de canción.
  3. Puedes intentar adivinar o el título de la canción o los artistas que la interpretan.
  4. Elige el formato de respuesta: 
      - Escribiendo el nombre y completando con las sugerencias que se mostrarán debajo del input. Pulsando sobre ellas se autocompletará directamente.
      - Seleccionando una de entre 4 opciones que estarán disponibles al comienzo de cada ronda.
- Una vez configurada la partida accedes a la sala de espera para comenzar cuando te sientas preparado para el desafio y poder modificar las configuraciones previas.
- Durante el transcurso de cada ronda sonará un fragmento aleatorio de una de las canciones de la playlist y antes de que termine el tiempo hay que intentar responder.
- Ganaras 10 puntos si acertaste o 0 en caso contrario.
- Una vez termina la ronda se visualiza la solución mientras se reproduce el fragmento de nuevo.
- Al finalizar, se muestran los resultados finales de la partida.

<span style="background-color: rgba(255, 165, 0, 0.5); padding: 4px 8px; border-radius: 4px;">
<b>Aviso:</b> Actualmente solo está implementado el modo en solitario. Los modos <b>Duelo 1vs1</b> y <b>Multijugador</b> no han sido desarrollados. 
</span>
  


---

### Perfil de Usuario

Los usuarios pueden:

- Editar su perfil (nombre, correo, descripción e imagen).
- Ver su historial de partidas.
- Acceder a los detalles de cada partida completada.

---

### Sistema Social

Los usuarios tienen acceso a funcionalidades sociales:

- Agregar, aceptar o rechazar solicitudes de amistad.
- Ver el estado online/offline de sus amigos y tiempo entre conexiones.
- Chatear en tiempo real mediante WebSockets.
- Visualizar información de perfiles de amigos.
- Bloquear o reportar usuarios.
- El sistema avisa en tiempo real sobre nuevos mensajes o solicitudes.

---

### Credenciales de prueba

| Usuario | Rol   | Username | Password  |
|--------|--------|----------|-----------|
| A      | Admin | a        | aom_admin |
| B      | User  | b        | aom_user  |

---

## Administradores

### Gestión de Contenido

- Crear, editar y eliminar canciones.
- Crear playlists desde cero añadiendo canciones nuevas o existentes.
- Subida y conversión automática de archivos:
  - Los audios con convertidos a formato `.opus` (48kbps, mediante `FFmpeg`). Es un formato muy ligero que permite almacenar más canciones.
  - Conversión de imágenes a `.webp` (a través de la libreria estandar de java `ImageIO`). Imágenes de alta calidad con archivos mucho más ligeros que JPEG o PNG, lo que acelera la carga de las páginas.

### Gestión de Usuarios

- Buscar usuarios con filtros avanzados.
- Banear o desactivar usuarios.
- Envio de mensajes informativos.

### Gestión de Reportes

- Visualizar reportes hechos por los usuarios.
- Tomar acciones como resolver reportes o banear usuarios.
- Confirmación de decisiones mediante interfaz segura y filtros por estado y fecha.

<div style="background-color: rgba(255, 165, 0, 0.5); padding-top: 4px; border-radius: 4px;">
  <p style="padding: 8px 0px 0px 4px; text-align-center"><b>Funcionalidades no desarrolladas:</b></p>
  <ul style="background-color: rgba(255, 165, 0, 0.3); padding: 4px 4px 4px 24px; border-radius: 0px 0px 4px 4px;">
    <li> Visualización de partidas en tiempo real.
    <li> Estadísticas generales.
    <li> Tienda (funcionalidad descontinuada).
  </ul> 
</div>


---

## Vistas Principales

### Vista Principal

- Pantalla de inicio con título, explicación del juego, imagen decorativa y botón para jugar.
- Imágenes generadas con ChatGPT bajo licencia OpenAI.

### Vista de Perfil

- Muestra información del usuario
- Listado del historial partidas jugadas.
- Modal para editar los datos personales.

### Vista Amigos

- Listado de amigos con un input para buscar amigos por su nombre de usuario.
- Botón que abre un modal para añadir amigos por su ombre de usuario.
- Boton para cambiar entre la vista de solicitudes de amistas pendientes y amigos.
- Pulsando encima de cada amigo o solicitud se abre la información del pefil de ese usuraio.
- A la derecha de cada amigo hay un botón que muestra 3 opciones:
  1. **Enviar Mensaje**: Abre el chat que compartis ese amigo y tú.
  2. **Eliminar**: Para dejar de ser amigo de ese usuario.
  3. **Bloquear y Reportar**: Permite bloquear a ese usuario y reportarlo si lo consideras necesario.

### Vistas de Partida
---
### Configuración de partida

- Selector de playlist con previsualización de portada y nombre.
- Información acerca del máximo de jugadores en función del modo seleccionado (1 para Solitario, 2 para Duelos y 4 para Multijugador)
- Inputs para elegir número de rondas, duración de cada fragmento y número máximo de jugadores.
- Selección del modo de juego (adivinar canción o artista).
- Selección del tipo de respuesta: escrita (con sugerencias automáticas) u opciones múltiples (4 posibles opciones).

### Sala de espera

- Visualización de la configuración actual de la partida (playlist, rondas, duración, modo de respuesta y juego).
- Lista de jugadores conectados con avatar, nombre, experiencia y victorias.
- Distinción visual del anfitrión.
- Código de acceso para invitar a otros jugadores.
- Botón para modificar la configuración (abre modal con el mismo formulario que la configuración inicial).
- Botón para comenzar la partida (solo visible para el anfitrión).

### Vista de partida

- Contador de ronda y tiempo restante (con diseño destacado).
- Imagen de la canción (oculta hasta mostrar la solución).
- Sistema de respuesta: input de texto con sugerencias y botón para marcar respuesta o selección entre 4 opciones.
- Indicador visual de acierto/fallo tras cada ronda.
- Lista de jugadores con avatar, nombre y puntuación actualizada en tiempo real.
- Botón para abandonar la partida en cualquier momento.

### Resultados

- Podio visual (en multijugador) o mensaje de finalización (en solitario).
- Lista de jugadores ordenada por puntuación, mostrando avatar, nombre, puntuación y número de aciertos.
- Botones para volver a jugar o regresar al inicio.
- Detalles de la partida: playlist utilizada, número de canciones jugadas, tabla de resultados por ronda (canción, artistas, aciertos/fallos de cada jugador).
- Imagen de portada de la playlist y resumen de la configuración de la partida.

### Panel de Administrador

Incluye accesos a:

- Gestión de usuarios.
- Gestión de canciones y playlists.
- Gestión de reportes.

- <span style="background-color: rgba(255, 165, 0, 0.5); padding: 4px 8px; border-radius: 4px;">Accesos desactivados a estadísticas, espectador de partidas y gestión de tienda.</span>
---

## Vistas de Administrador

### Gestión de usuarios

- Filtros avanzados por ID, nombre de usuario, email y roles (con select múltiple).
- Ordenación por ID, fecha de registro o última conexión, con cambio de dirección ascendente/descendente.
- Listado de usuarios con información clave: avatar, nombre, ID, roles, email, fecha de registro, última conexión, estado (activo/inactivo/baneado).
- Botones para banear/desbanear y habilitar/deshabilitar usuarios (con modales de confirmación).
- Acceso rápido al perfil de cada usuario haciendo clic en su imagen.

### Gestión de playlists y canciones

- Selector para alternar entre la vista de playlists y canciones.
- Listado de playlists con búsqueda y filtros, previsualización de portada y acceso a edición/eliminación.
- Listado de canciones con búsqueda y filtros, mostrando nombre, artistas, álbum, duración y portada.
- Formularios para crear nuevas playlists y canciones, con validación de campos obligatorios.
- Subida de archivos de audio (convertidos automáticamente a `.opus`) e imágenes de portada (convertidas a `.webp`).
- Gestión de canciones dentro de playlists: añadir, eliminar y reordenar canciones.
- Rollback automático en caso de error durante la creación de playlists o canciones.

### Gestión de reportes

- Filtros por fecha y estado del reporte (pendiente, resuelto sin baneo, resuelto con baneo).
- Tabla con todos los reportes: ID, usuario reportado, usuario que reporta, razón, partida asociada, fechas de creación y resolución, y administrador que resolvió.
- Acceso directo al perfil de los usuarios implicados y a la partida relacionada.
- Acciones rápidas para resolver, banear o reabrir reportes, tanto de forma individual como masiva (con modal de confirmación).
- Eliminación de reportes desde la propia tabla.

---

## Licencias y Créditos

- **[FFmpeg](https://github.com/FFmpeg/FFmpeg)**: Utilizado para la conversión y análisis de archivos de audio. FFmpeg es software libre, licenciado bajo la LGPL v2.1 o posterior, con componentes opcionales bajo GPL. Arena of Music no distribuye FFmpeg directamente, sino que lo invoca externamente, cumpliendo así con los términos de la licencia. Más información en su documentación legal.

- **[SpotDL](https://github.com/spotDL/spotify-downloader)**: Utilizado en la fase de recopilación para la descarga de canciones desde Spotify (vía YouTube). SpotDL es un proyecto de código abierto bajo licencia MIT.

- **[Bootstrap Icons](https://icons.getbootstrap.com/)**: Todos los íconos de la aplicación provienen de Bootstrap Icons, distribuidos bajo la licencia MIT.

- **Imágenes:** Generadas con **[ChatGPT](https://chatgpt.com/)** (licencia OpenAI) y **[Gemini](https://gemini.google.com/)** (Licencia sujeta a los [Términos de Uso de Gemini](https://support.google.com/gemini/answer/13594961))

---

## ✅ Estado del Proyecto

| Módulo                     | Estado                                                 |
|----------------------------|--------------------------------------------------------|
| Partida en solitario       | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Chat en tiempo real        | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Sistema de amistad         | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Personalización de Perfil  | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Administración de música   | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Reportes de usuarios       | <span style="color: green; padding-right: 4px;">&#x25CF; </span> Completado |
| Duelo 1vs1 / Multijugador  | <span style="color: red; padding-right: 4px;">&#x25CF; </span> Pendiente    |
| Visualización de partidas  | <span style="color: red; padding-right: 4px;">&#x25CF; </span> No hecho     |
| Estadísticas generales     | <span style="color: red; padding-right: 4px;">&#x25CF; </span> No hecho     |
| Tienda                     | <span style="color: red; padding-right: 4px;">&#x25CF; </span> Cancelado    |
