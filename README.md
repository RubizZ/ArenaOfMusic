# Arena of Music (IW 2024/2025)
## Descripción
AoM es un juego interactivo y competitivo basado en la música, donde los jugadores, utilizando una serie de playlist predeterminadas y actualizadas frecuentemente, pueden demostrar sus conocimientos musicales en diferentes modos de juego.
## Funcionalidades Principales (Version Inicial)
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

## Funcionalidades Principales (Version Entrega 8 de abril 2025)
**Partida Individual:** Modo de juego en solitario donde el jugador seleccionará la playlist sobre la que quiera jugar. Dicha playlist se cargará y el jugador decidirá cuantas canciones se reproducen y el número de estas que sonarán. Se seleccionarán aleatoriamente las canciones de la playlist elegida por el jugador y comenzará la partida. En cada ronda sonará una de las canciones seleccionadas y el jugador escribirá el título de la canción. Una vez acabado el tiempo de reproducción se mostrará el resultado y el jugador recibirá puntos en función de cuántos segundos ha tardado el jugador en acertarlo. Cuando acaben de reproducirse todas las canciones, se mostrarán los resultados de la partida. Actualmente se encuentra desarrollada la totalidad de la configuración y practicamente la sala de espera. Desde la Sala de Espera de la partida se podrá modificar la configuración inicial de la partida antes de comenzar para no tener que abandonar la sala en caso de cambiar de idea a la hora de los requisitos de la partida. La partida se encuentra en un estado de desarrollo avanzado en el que ya se encuentra codificado practicamente la totalidad del backend a falta de desarrollar el frontend con las peticiones ajax necesarias para el correcto desarrollo del game loop. La vista de resultados Falta de desarrollar.

**Duelo 1vs1:** En este modo se conectarán 2 jugadores a competir entre ellos, un anfitrión y un invitado. El anfitrión será quien, al igual que en el modo individual, seleccione la playlist, los segundos de reproducción y cuantas canciones sonarán. La partida transcurrirá del mismo modo, salvo que esta vez los jugadores escribirán los títulos hasta que uno acierte o se termine el tiempo. Aquel jugador que acierte se llevará la ronda y en caso de no acertar ninguno se considerará empate. Al terminar se mostrarán los resultados y al ganador del duelo.

**Modo Fiesta:** En este modo se conectarán desde 2 a 4 jugadores. Del mismo modo que en el formato de Duelo habrá un anfitrión, que se encargará de configurar la partida, y el resto invitados. Este modo transcurrirá como en el individual, se reproducirá la canción,cada jugador escribirá el título y al finalizar la reproducción se les asignan puntos en función cuanto se haya tardado en responder.

Ambas versiones serán posibles adiciones con la implementación de web sockets para una correcta experiencia online.

**Personalización de perfil:**  Los jugadores podrán comprar diferentes cosméticos con los puntos obtenidos al finalizar las partidas para darle su toque personal y diferente a sus perfiles. Actualmente, la tienda y la compra de objetos de personalización se encuentra en un limbo de posibles adición en caso de llegar tiempo con el resto de la aplicación.

**Social y Amistad:** Como jugador podrás buscar a otros jugadores a los que solicitarles amistad, ver sus perfiles y estadísticas y poder invitarles a jugar partidas. Recientemente se comenzó su desarrollo. Además, se añadirá un chat privado entre usuarios para poder hablar e invitarse a partidas.

### Administradores
**Gestionar Artistas, Canciones y Playlists:** Los administradores podrán agregar nuevos registros de artistas y canciones a la aplicación con los que conformar y modificar playlists que los usuarios podrán seleccionar para jugar en las partidas. Los artistas no se gestionan, puesto que se redujo la implementación a Canciones y Playlist. Se encuentra completamente desarrollado y es perfectamente funcional la creacion y modificación de playlist, al igual que la adición de nuevas caniones y vinculación a playlist.

**Gestionar Tienda:** Decidirán qué elementos forman parte de la tienda de cosméticos que los jugadores comprarán para la modificación de sus perfiles, eligiendo qué nuevos elementos pasan a formar parte de la tienda y cuales eliminar. Como la tienda no se encuentra en la planificación principal actual, no se desarrollará salvo que quede tiempo para ello.

**Reportes de Usuarios:** Los usuarios podrán reportar a otros jugadores. Los administradores podrán ver una lista de jugadores reportados y seleccionar las sanciones que se les impondrá. Con el desarrollo de los chats se introducirá para tener una herramienta de control sobre acciones indeseadas a traves de mensajes de texto, así como la posibilidad de bannear a usuarios de la app.

**Visualización de partidas:** Mientras las partidas estén en curso los administradores podrán ser espectadores de ellas en caso de que los jugadores reporten conductas inadecuadas. Solo en caso de llegar a tiempo se inclurá la opción de visualizar partidas en directo si se ha recibido un reporte de posibles trampas o conductas inapropiadas en el transcurso de la partida.

**Estadísticas de la app:** Los administradores podrán ver una serie de estadísticas tales como el número de usuarios registrados, de partidas jugadas, etc. Al igual que la anterior, no la consideramos proiritaria y se desarrollará en caso de tener tiempo.

## Funcionalidades Principales (Version Entrega Examen 22 de mayo 2025)

### Credenciales para usuarios iniciales:
//TODO

### Partida
- **Partida Individual:** 
Modo de juego en solitario donde el jugador seleccionará la playlist sobre la que quiera jugar. Dicha playlist se cargará y el jugador decidirá cuantas canciones se reproducen y el número segundos que se reproducirán. Se seleccionarán aleatoriamente las canciones de la playlist elegida por el jugador y comenzará la partida. En cada ronda sonará un fragmento aleatorio de la cancion seleccionad y el jugador escribir el título de la canción. Una vez acabado el tiempo de reproducción se mostrará la información de la canción que sonó, volverá a reproducirse el fragmento y el jugador recibirá 10 puntos si acertó la canción o 0 puntos si no. Cuando acaben de reproducirse todas las canciones, se mostrarán los resultados de la partida. 

- Actualmente se encuentra desarrollada la totalidad de la partida en solitario: configuración, sala de espera, partida y resultados, pero no se han añadido nuevos modos de juego ni se ha implementado las partidas de **Duelo 1vs1** ni **Modo Multijugador**.

**Personalización de perfil:**  
La página de perfil ofrece a los usuarios un espacio completo para gestionar y personalizar su experiencia en el juego. En esta sección, es posible ver y editar la información personal, como el nombre, el correo electrónico y la imagen de perfil.

**Social y Amistad:** //TODO

### Administradores
**Gestionar Artistas, Canciones y Playlists:** //TODO

**Gestionar Tienda:** //TODO

**Reportes de Usuarios:** En esta sección, los administradores pueden visualizar los reportes enviados por los usuarios de la aplicación. Cada reporte contiene información detallada sobre de quien a quien va dirigido el reporte así como el hecho que se reporta, como conductas inapropiadas, trampas o cualquier otro problema relevante. Desde aquí, los administradores tienen la capacidad de tomar decisiones en función del contenido reportado: pueden resolver los reportes si el problema ha sido solucionado o, en caso necesario, aplicar sanciones como el baneo de usuarios que hayan incumplido las normas de la comunidad.

**Visualización de partidas:** //TODO

**Estadísticas de la app:** //TODO

## Vistas (Versión inicial)
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
Al acceder en la tienda se verán separadas en filas los actuales tipos de cosméticos que se podrán comprar en la tienda. Actualmente hay 3: la primera imagenes para la foto de perfil, la segunda marcos para las fotos y la tercera para los banners del perfil. Cada uno de los componentes está representado en una tarjeta con su nombre y un botón que al pulsarse (en el futuro) mostrará un modal en el que se mostrará el titulo, el coste, la imagen más ampliada, más información relevante acerca de ese componente y un boton con el que confirmar la compra de dicho componente.

Todos los iconos y los marcos incluidos hns sido obtenidas de flaticon.com con licencia gratuita con atribución (al ser imagenes actualmente de placeholder no se incluye la atribución, pero en caso de ser incluidos en la versión final los autores serán debidamente referenciados).
Todos los banners incluidos han sido obtenidos de Canva.es que pertenecen al contenido gratuito
### Vista Amigos
En esta vista, los jugadores pueden gestionar su lista de amigos y las solicitudes de amistad recibidas, así como consultar el perfil de un usuario concreto. La pantalla se compone de dos paneles principales.

En el panel izquierdo se puede alternar entre la lista de amigos y las solicitudes recibidas mediante dos botones, mostrando las listas correspondientes. En el caso de la lista de amigos, se muestra el nombre de usuario, su imagen de perfil y el estado de conexión, así como un botón para añadir amigos. Para la lista de solicitudes, se muestra el nombre de usuario, el nivel y el porcentaje de victorias, además de los botones correspondientes para aceptar o rechazar la solicitud. En ambos se presenta un área de búsqueda para filtrar o localizar usuarios concretos.

En el panel derecho se presenta la información detallada del usuario seleccionado en el panel izquierdo. Al elegir un usuario de cualquier lista, se presenta esta sección que incluye la imagen de perfil, el nombre de usuario y el estado de conexión, seguido de sus estadísticas personales con el número de victorias, derrotas y empates. Además, se incluyen las playlists que más ha jugado, junto con el nombre del autor de la misma.

## Vistas (Version Entrega 8 de abril 2025)
### Panel de Administrador
El panel de administrador contiene un titulo y 6 botones (hechos con cards) que lleva a las diferentes subpaginas (actualmente no completas) a las que un admin puede acceder para controlar la aplicacion. Estas son las siguiente:
- **Playlists**: Añade, modifica o elimina una playlist que se puede jugar. También incluye la subida de archivos. (Completamente desarrollada. Incluye filtros y paginación en la visualización de playlist y canciones, asi como la posibiladad de escuchar las canciones añadidas, añadir nuevas y crear, eliminar o modificar playlists) La conversión de archivos a .mp3 en caso de audio, la fragmentación de canciones y la conversión de imagenes a .webp se hace por completo con la herramienta ffmpeg.
- **Tienda**: Añade, modifica o elinmina un cosmetico (logo, marco, banner, etc) de la tienda. (Abandonada hasta ver si se desarrolla la tienda)
- **Administrar usuarios**: Cambia las pertenencias de un usuario, sus atributos, o banea a un perpetrador de las normas de la aplicacion. (Empezada. Actualmente versión estática pero en desarrollo)
- **Reportes**: Accede a una lista de reportes que usuarios hayan hecho a otros usuarios para imponer castigos según el comportamiento del usuario, como banear tramposos. (Empezada. Actualmente versión estática pero en desarrollo)
- **Espectar partidas**: Accede a una lista de partidas a las que poder unirte como espectador en tiempo real (para moderarlas). (Misma situación que la tienda)
- **Estadisticas generales**: Recibe un insight sobre la aplicacion. (Misma situación que la tienda)

Los iconos de los botones pertenecen a Bootstrap Icons y estan bajo la licencia MIT
### Vista Principal
Como su propio nombre indica, esta es la vista principal de nuestra página web. En esta pantalla se encuentran varios elementos clave: un título, un mensaje de bienvenida y una sección con una imagen donde se pondrán las reglas del juego o algún mensaje de interés para el usuario. Además, hay un botón JUGAR, que llevaría al usuario a crear la partida para poder empezar a jugar. En la esquina superior derecha se encuentra el perfil de usuario, desde donde puede acceder a su vista de perfil con solo hacer clic. (Desarrollada a falta de ampliar con más información sobre la app: intrucciones de juegos, posibilidad de ver las canciones de las playlist...)

Las imágenes utilizadas en esta vista han sido generadas con ChatGPT, bajo la licencia de OpenAI.
### Vista Perfil
La página de perfil ofrece a los usuarios un espacio completo para gestionar y personalizar su experiencia en el juego. En esta sección, es posible ver y editar la información personal, como el nombre, el correo electrónico y la imagen de perfil. (Completamente funcional)

Además, la página muestra una lista detallada de los objetos poseídos, permitiendo al usuario visualizar todos los elementos adquiridos a lo largo del juego. (Ampliable en caso de incluir la tienda)

También se presentan estadísticas personales, proporcionando un resumen del rendimiento, logros alcanzados y progresos realizados. (Solo registro de resultados de partida)

Por último, el historial de partidas ofrece un registro completo de las partidas jugadas, con detalles sobre los resultados y el progreso en el juego. (Se desarrollará al terminar las partidas)

### Vista Tienda (Abandonada por el momento)
Al acceder en la tienda se verán separadas en filas los actuales tipos de cosméticos que se podrán comprar en la tienda. Actualmente hay 3: la primera imagenes para la foto de perfil, la segunda marcos para las fotos y la tercera para los banners del perfil. Cada uno de los componentes está representado en una tarjeta con su nombre y un botón que al pulsarse (en el futuro) mostrará un modal en el que se mostrará el titulo, el coste, la imagen más ampliada, más información relevante acerca de ese componente y un boton con el que confirmar la compra de dicho componente.

Todos los iconos y los marcos incluidos hns sido obtenidas de flaticon.com con licencia gratuita con atribución (al ser imagenes actualmente de placeholder no se incluye la atribución, pero en caso de ser incluidos en la versión final los autores serán debidamente referenciados). Todos los banners incluidos han sido obtenidos de Canva.es que pertenecen al contenido gratuito

Todo el contenido ha sido removido en este momento. Se volverá a incluir si llega a desarrollarse.
### Vista Amigos
En esta vista, los jugadores pueden gestionar su lista de amigos y las solicitudes de amistad recibidas, así como consultar el perfil de un usuario concreto. La pantalla se compone de dos paneles principales.

En el panel izquierdo se puede alternar entre la lista de amigos y las solicitudes recibidas mediante dos botones, mostrando las listas correspondientes. En el caso de la lista de amigos, se muestra el nombre de usuario, su imagen de perfil y el estado de conexión, así como un botón para añadir amigos. Para la lista de solicitudes, se muestra el nombre de usuario, el nivel y el porcentaje de victorias, además de los botones correspondientes para aceptar o rechazar la solicitud. En ambos se presenta un área de búsqueda para filtrar o localizar usuarios concretos.

En el panel derecho se presenta la información detallada del usuario seleccionado en el panel izquierdo. Al elegir un usuario de cualquier lista, se presenta esta sección que incluye la imagen de perfil, el nombre de usuario y el estado de conexión, seguido de sus estadísticas personales con el número de victorias, derrotas y empates. Además, se incluyen las playlists que más ha jugado, junto con el nombre del autor de la misma.

Actualmente está la versión estática, pero se encuentra en desarrollo

###Vistas Login y Registro
Se encuentran completamente desarrolladas y funcionales.



## Vistas (Version Entrega Examen 22 de mayo 2025)
### Panel de Administrador
El panel de administrador contiene un titulo y 6 botones (hechos con cards) que lleva a las diferentes subpaginas (actualmente no completas) a las que un admin puede acceder para controlar la aplicacion. Estas son las siguiente:
- **Playlists**: //TODO
- **Tienda**: //TODO
- **Administrar usuarios**: Permite visualizar todos los usuarios registrados mediante filtros avanzados. Para cada usuario es posible realizar acciones específicas, como banearlo en caso de que haya infringido las normas de la aplicación, o simplemente desactivar su perfil temporalmente.

- **Reportes**: Accede a una lista de reportes que usuarios hayan hecho a otros usuarios para imponer castigos según el comportamiento del usuario, como banear tramposos. Esta vista ya está completamente desarrollada, siendo dinámica y haciendose efectivas las decisiones del administracion al resolver los reportes. Para resolver un reporte tiene que escoger una de las tres acciones posibles: pendiente, resolver (que no banea al usuario y marca como resuelto ese reporte, guardando quien lo resuelve y cuando) y banear (que banea al usuario reportado y resuelve ese reporte) y posteriormente confirmar las resoluciones, que hacen efectivas esas acciones. También encontramos filtros para la búsqueda de reportes como filtrar por fecha y por el estado del reporte Los iconos de las acciones pertenecen a Bootstrap Icons.

Desde esta vista también se puede llegar a la vista ver-perfil, que muestra información al usuario sobre un usuario distinto, es decir muestra datos como su foto de perfil, correo, nombre de usuario...
- **Espectar partidas**: //TODO
- **Estadisticas generales**: //TODO

Los iconos de los botones pertenecen a Bootstrap Icons y estan bajo la licencia MIT
### Vista Principal
Como su propio nombre indica, esta es la vista principal de nuestra página web. En esta pantalla se encuentran varios elementos clave: un título, un mensaje de bienvenida y una sección con una imagen donde se explica cómo jugar. Además, hay un botón JUGAR, que llevaría al usuario a escoger el modo de juego, para posteriormente crear la partida para poder empezar a jugar.

Las imágenes utilizadas en esta vista han sido generadas con ChatGPT, bajo la licencia de OpenAI.

### Vista Perfil
En esta pantalla se muestra una tarjeta con la información del usuario (foto de perfil, nombre de usuario, correo electrónico y descripción), acompañada de un botón que abre un modal para editar dichos datos.
A continuación, se presenta una lista con el historial de partidas del usuario, donde se detalla el nombre de la playlist, la posición obtenida, el número de aciertos y un botón para acceder a los detalles de cada partida.
El modal de edición de perfil permite actualizar el nombre de usuario, la descripción, el correo electrónico, la contraseña y la imagen de perfil.

### Vista Tienda (Abandonada por el momento)
//TODO ?

Todo el contenido ha sido removido en este momento. Se volverá a incluir si llega a desarrollarse.
### Vista Amigos
La vista de amigos permite a los usuarios gestionar su lista de amistades y solicitudes, así como consultar el perfil de un usuario concreto. El diseño está centrado en pantalla, desplazando el contenido al seleccionar un usuario para acceder a los detalles de este. Estos detalles incluyen la foto de perfil, el nombre de usuario, su estado de conexión, playlists recientes jugadas y estadísticas generales, como victorias totales, puntuación media y máxima puntuación. El usuario puede cerrar esta ventana de detalles y volver a la disposición anterior en cualquier momento.

En la pantalla principal, el usuario puede alternar entre la lista de amigos y la de solicitudes. En el caso de la lista de amigos, se muestra el nombre de usuario, su imagen de perfil, el nivel de experiencia y un indicador de mensajes pendientes de leer, así como un botón para añadir amigos y un buscador. Para la lista de solicitudes, se muestra el nombre de usuario, el nivel y botones para aceptar o rechazar la solicitud. Si hay solicitudes sin responder, aparece un indicador de notificación.

Para cada amigo, se pueden realizar acciones mediante un menú de acciones rápidas, las cuales incluyen enviar un mensaje, eliminar de la lista de amigas y bloquear o reportar al usuario.

### Vistas Login y Registro
Incluyen las vistas que permiten a los usuarios iniciar sesión o registrarse. Al momento del registro, se verifica si ya existe un usuario con la misma dirección de correo electrónico. Durante el inicio de sesión, se comprueba también si el usuario ha sido baneado: en ese caso, se muestra un aviso y no se permite el acceso a la aplicación.
(Se encuentran completamente desarrolladas y funcionales.)

### Vistas Partida
Desde el Home, una vez decidido a jugar la partida, al pulsar en el botón "Jugar" se abrirá un modal para hacer la selección del tipo de partida que se desee jugar "Solitario", "Duelo" o "Multijugador" y pasar a configurar la partida.

#### Configuración inicial de la partida
Una vez acccedemos a la configuración podemos ver los distintos campos para personalizar la partida:
1. Selección de playlist: El jugador elige entre las distintas listas de canciones disponibles.

2. Número máximo de jugadores: En función de la opción que seleccionaste será de 1 siescogiste "Solitario", 2 si decidiste por un "Duelo" o 4 si optaste por una partida "Multijugador".

3. Ajustes de juego: Puedes configurar el número de rondas que deseas jugar, entre 1 y 5 y la duración de cada fragmento que se reproducirá por ronda, comprendido entre 1 y 30 segundos.

4. Existen distintas modalidades de juego, de las cuales algunas están disponibles y otras se encuentran en desarrollo:
    - Adivina la canción (activo): El jugador debe escribir el nombre exacto de la canción.
    - Opciones múltiples (próximamente): El jugador elige la respuesta correcta entre varias opciones.
    - Adivina el artista (próximamente): El jugador debe escribir el nombre del artista que interpreta la canción.

5. Controles
    - Volver: Retorna a la pantalla anterior.

    - Iniciar: Se creala partida con la configuración seleccionada y se accede a la Sala de Espera para comenzar la partida.

#### Sala de Espera
Una vez que se accede a la Sala de Espera se puede ver una serie de información antes de comenzar la Partida.

- **Configuración de la partida:**
  - El nombre y la caratula de la playlist ue se creó.
  - El número de rondas y duración que tendrá cada fragmento que se va a reproducir.
  - El modo de juego que se seleccionó.

- **Código de sala:** 
  - Un UUID único que podrá ser copiado y compartido con otros jugadores para unirse a la partida.

- **Lista de jugadores conectados:**
  - Se muestra el nombre del jugador, su imagen de perfil (si posee una), experiencia total (EXP) y número de victorias.
  - El jugador que creó la sala aparece marcado como **ANFITRIÓN**.

- **Controles:**
  - **Botón "Comenzar partida":** Inicia la partida cuando todos los jugadores estén listos.
  - **Botón "Salir":** Permite abandonar la sala en cualquier momento.

#### Partida
Durante una partida, los jugadores se enfrentan a rondas sucesivas donde deben adivinar el título de canciones en un tiempo limitado. En la vista se encuentran:

- **Encabezado de ronda:**
  - Indica la ronda actual sobre el total que se haya marcado.

- **Contador** 
  - En el lado izquierdo se muestra un contador que marcará el inicio de cada ronda **"Preparados...", "Listos...", "YA!"** y comenzará la reproducción del fragmento. Durante la reproducción se mostrará una cuenta atrás desde el tiempo seleccionado hasta llegar a 0, que marcará el fin del tiempo de respuesta y de la reproducción del fragmento.

- **Imagen de la canción:** 
  - En el centro se muestra una imagen genérica con un signo de interrogación mientras la fase de responder está sucediendo. Una vez el tiempo se agota la imagen cambia por la caratula y el nombre de la canción a la que correspondía el fragmento que estuvo sonando, mientras se vuelve a reproducir.

- **Campo de respuesta:**
  - Debajo del la imagen se encuentra el campo de donde el jugador deberá dar su respuesta, que será un input de texto que facilitará opciones similares a la que se habrá introducido para poder seleccionarlas rápido o varias opciones a seleccionar en función del modo de juego que se seleccionara.
  - En caso de ser un modo de juego de escritura, pulsando en el botón verde **"Marcar"** o presionando la tecla 'Enter' se confirmará la respuesta.

- **Información del jugador:**
  - En el lado derecho, se muestran las tarjetas de los jugadores, incluyendo:
    - Imagen de perfil (genérica si no se ha configurado).
    - Nombre de usuario.
    - Puntos acumulados durante la partida.

- **Botón de "Salir":**
  - Posicionado en la esquina superior derecha, permite abandonar la partida en cualquier momento.

Esta vista está diseñada para centrarse en la interacción rápida del jugador, reduciendo distracciones y destacando los elementos clave para la respuesta en tiempo real.


#### Resultados
Una vez terminada la partida, los jugadores se erán redirigidos automáticamente a la vista final de resultados. En ella podrán ver:

- **El podio o los vencedores** de la partida junto con un **Ranking** ordenado de los jugadores en función de sus aciertos sobre las rondas jugadas.

- **Información de la Partida** tal como la playlist jugada y el número de canciones reproducidas de la lista. 

- Debajo de la información se observará un tabla en la que la primera columna se pondrán ordenados los **títulos** las canciones que se reprodujeron y los **nombres** de los **artistas** a los que pertenecen en orden de ronda y después, tantas columnas como jugadoresdisputaron la partida, colocandose un punto rojo en caso de fallado o un punto verde en caso de haber acertar la canción o el artista que sonaba en esa ronda

- Por último, hay 2 botonoes. Uno para **Volver a Jugar**, que redirije al jugador de nuevo a la **Configuración de la Partida** con el modo de juego (Solo, Duelo o Multijugador) que se habí ajugado en esta partida y un botón de **Continuar** que te redirige de nuevo a la vista **Principal** de **Arena of Music**.


