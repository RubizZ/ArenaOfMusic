# Arena of Music (IW 2024/2025)
## Descripción
AoM es un juego interactivo y competitivo basado en la música, donde los jugadores, utilizando una serie de playlist predeterminadas y actualizadas frecuentemente, pueden demostrar sus conocimientos musicales en diferentes modos de juego.
## Funcionalidades Principales
### Jugadores
**Partida Individual:** Modo de juego en solitario donde el jugador seleccionará la playlist sobre la que quiera jugar. Dicha playlist se cargará y el jugador decidirá cuantas canciones se reproducen y el número de estas que sonarán. Se seleccionarán aleatoriamente las canciones de la playlist elegida por el jugador y comenzará la partida. En cada ronda sonará una de las canciones seleccionadas y el jugador escribirá el título de la canción. Una vez acabado el tiempo de reproducción se mostrará el resultado y el jugador recibirá puntos en función de cuántos segundos ha tardado el jugador en acertarlo. Cuando acaben de reproducirse todas las canciones, se mostrarán los resultados de la partida.

**Duelo 1vs1:** En este modo se conectarán 2 jugadores a competir entre ellos, un anfitrión y un invitado. El anfitrión será quien, al igual que en el modo individual, seleccione la playlist, los segundos de reproducción y cuantas canciones sonarán. La partida transcurrirá del mismo modo, salvo que esta vez los jugadores escribirán los títulos hasta que uno acierte o se termine el tiempo. Aquel jugador que acierte se llevará la ronda y en caso de no acertar ninguno se considerará empate. Al terminar se mostrarán los resultados y al ganador del duelo.

**Modo Fiesta:** En este modo se conectarán desde 2 a 4 jugadores. Del mismo modo que en el formato de Duelo habrá un anfitrión, que se encargará de configurar la partida, y el resto invitados. Este modo transcurrirá como en el individual, se reproducirá la canción,cada jugador escribirá el título y al finalizar la reproducción se les asignan puntos en función cuanto se haya tardado en responder. 

**Registros y estadísticas:** Cada jugador podrá ver en su perfil los registros de las últimas partidas y estadísticas relevantes: cuántas veces ha jugado con una misma playlist, media de las partidas, número de victorias o derrotas en el modo duelo…

**Personalización de perfil:**  Los jugadores podrán comprar diferentes cosméticos con los puntos obtenidos al finalizar las partidas para darle su toque personal y diferente a sus perfiles.

**Social y Amistad:** Como jugador podrás buscar a otros jugadores a los que solicitarles amistad, ver sus perfiles y estadísticas y poder invitarles a jugar partidas.
### Administradores
**Gestionar Artistas, Canciones y Playlists:** Los administradores podrán agregar nuevos registros de artistas y canciones a la aplicación con los que conformar y modificar playlists que los usuarios podrán seleccionar para jugar en las partidas.

**Gestionar Tienda:** Decidirán qué elementos forman parte de la tienda de cosméticos que los jugadores comprarán para la modificación de sus perfiles, eligiendo qué nuevos elementos pasan a formar parte de la tienda y cuales eliminar.

**Reportes de Usuarios:** Los usuarios podrán reportar a otros jugadores. Los administradores podrán ver una lista de jugadores reportados y seleccionar las sanciones que se les impondrá.

**Visualización de partidas:** Mientras las partidas estén en curso los administradores podrán ser espectadores de ellas en caso de que los jugadores reporten conductas inadecuadas.

**Estadísticas de la app:** Los administradores podrán ver una serie de estadísticas tales como el número de usuarios registrados, de partidas jugadas, etc.

## Vistas
### Panel de Administrador
El panel de administrador contiene un titulo y 6 botones (hechos con cards) que lleva a las diferentes subpaginas (actualmente no completas) a las que un admin puede acceder para controlar la aplicacion. Estas son las siguiente:
- **Playlists**: Añade, modifica o elimina una playlist que se puede jugar. También incluye la subida de archivos.
- **Tienda**: Añade, modifica o elinmina un cosmetico (logo, marco, banner, etc) de la tienda.
- **Administrar usuarios**: Cambia las pertenencias de un usuario, sus atributos, o banea a un perpetrador de las normas de la aplicacion.
- **Reportes**: Accede a una lista de reportes que usuarios hayan hecho a otros usuarios para imponer castigos según el comportamiento del usuario, como banear tramposos.
- **Espectar partidas**: Accede a una lista de partidas a las que poder unirte como espectador en tiempo real (para moderarlas).
- **Estadisticas generales**: Recibe un insight sobre la aplicacion.

Los iconos de los botones pertenecen a Bootstrap Icons y estan bajo la licencia MIT
### Vista Principal
Como su propio nombre indica, esta es la vista principal de nuestra página web. En esta pantalla se encuentran varios elementos clave: un título, un mensaje de bienvenida y una sección con una imagen donde se pondrán las reglas del juego o algún mensaje de interés para el usuario. Además, hay un botón JUGAR, que llevaría al usuario a crear la partida para poder empezar a jugar. En la esquina superior derecha se encuentra el perfil de usuario, desde donde puede acceder a su vista de perfil con solo hacer clic.

Las imágenes utilizadas en esta vista han sido generadas con ChatGPT, bajo la licencia de OpenAI.
### Vista Perfil
La página de perfil ofrece a los usuarios un espacio completo para gestionar y personalizar su experiencia en el juego. En esta sección, es posible ver y editar la información personal, como el nombre, el correo electrónico y la imagen de perfil.

Además, la página muestra una lista detallada de los objetos poseídos, permitiendo al usuario visualizar todos los elementos adquiridos a lo largo del juego. 

También se presentan estadísticas personales, proporcionando un resumen del rendimiento, logros alcanzados y progresos realizados.

Por último, el historial de partidas ofrece un registro completo de las partidas jugadas, con detalles sobre los resultados y el progreso en el juego.
### Vista Tienda

