Feature: partida solitario

#
# En esta historia, el usuario 'b' se embarca en una aventura musical en solitario:
# Primero inicia sesión, se prepara para jugar, compite en una partida, visita su perfil,
# se anima a configurar otra partida pero se arrepiente y la abandona, vuelve a intentarlo,
# juega una ronda más, abandona la partida frustrado y finalmente cierra sesión para irse a jugar al LoL.
#

Scenario: solo-game completo

    # El usuario 'b' llega a la página de inicio de sesión y accede con sus credenciales.
    Given driver baseUrl + '/login'
    And input('#username', 'b')
    And input('#password', 'aom_user')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl)

    # Con la sesión iniciada, siente la emoción y pulsa el botón para empezar a jugar.
    Given waitFor('#btn-jugar')
    When click('#btn-jugar')  

    # Espera con expectación a que se abra el menú de modos de juego.
    Then waitFor('#modosModal')

    # Decide jugar en solitario y selecciona esa opción.
    Then waitFor('#modo-solitario a')
    When click('#modo-solitario a')
    Then waitForUrl(baseUrl + '/partida/configuracion-partida')

    # Llega a la sala de configuración y elige su playlist favorita.
    Given waitFor('#playlist994')
    When click('#playlist994')
    Then match script("document.querySelector('#playlist994').checked") == true

    # Ajusta el reto: solo una ronda para calentar motores.
    Given waitFor('#rounds')
    When script("document.querySelector('#rounds').value = '1'; document.querySelector('#rounds').dispatchEvent(new Event('input'))")
    Then match text('#roundsValue') == '1'

    # Decide que los fragmentos musicales duren solo un segundo, ¡todo un desafío!
    Given waitFor('#time')
    When script("document.querySelector('#time').value = '1'; document.querySelector('#time').dispatchEvent(new Event('input'))")
    Then match text('#timeValue') == '1'

    # Escoge el modo de juego donde adivinar el nombre de la canción.
    Given waitFor('#song')
    When click('#song')
    Then match script("document.querySelector('#song').checked") == true

    # Escoge el modo de respuesta donde debe escribir el nombre de la canción.
    Given waitFor('#write')
    When click('#write')
    Then match script("document.querySelector('#write').checked") == true

    # Con todo listo, pulsa el botón para comenzar la partida.
    Given waitFor('#buttonAccion')
    When click('#buttonAccion')
    Then waitForUrl(baseUrl + '/partida/sala-espera')

    # Cuando está listo, inicia la partida.
    Given waitFor('#startButton')    
    When click('#startButton')
    Then waitForUrl(baseUrl + '/partida')

    # Se enfrenta al reto e intenta adivinar la canción escribiendo "1".
    Given waitFor('#songInput')
    And input('#songInput', 'Uno más uno son 7')

    # Tras la ronda, revisa los resultados y decide volver al inicio.
    Then waitForUrl(baseUrl + '/partida/resultados')  
    Given waitFor('a#btn-continue')    
    When click('a#btn-continue')
    Then waitForUrl(baseUrl)

    # Con curiosidad, visita su perfil para ver su progreso.
    Then waitFor('#profile')
    When click('#profile')
    Then waitForUrl(baseUrl + '/perfil')

    # Desde su perfil, decide consultar los resultados de una partida anterior.
    Given waitFor("a[href*='/partida/resultados']")
    When click("a[href*='/partida/resultados']")

    # Tras ver los resultados, opta por jugar de nuevo y vuelve a la configuración de partida.
    Then waitForUrl(baseUrl + '/partida/resultados')
    Given waitFor("a[href*='/partida/configuracion-partida']")
    When click("a[href*='/partida/configuracion-partida']")
    Then waitForUrl(baseUrl + '/partida/configuracion-partida')

    # Selecciona la misma playlist y se prepara para una partida más larga.
    Given waitFor('#playlist994')
    When click('#playlist994')
    Then match script("document.querySelector('#playlist994').checked") == true

    # Decide que la partida tendrá 10 rondas, ¡quiere superarse!
    Given waitFor('#rounds')
    When script("document.querySelector('#rounds').value = '10'; document.querySelector('#rounds').dispatchEvent(new Event('input'))")
    Then match text('#roundsValue') == '10'

    # Ajusta la duración de los fragmentos a 15 segundos para pensárselo mejor.
    Given waitFor('#time')
    When script("document.querySelector('#time').value = '15'; document.querySelector('#time').dispatchEvent(new Event('input'))")
    Then match text('#timeValue') == '15'

    # Vuelve a elegir el modo de adivinar la canción.
    Given waitFor('#song')
    When click('#song')
    Then match script("document.querySelector('#song').checked") == true

    # Y esta vez elige el modo de respuestas Opciones.
    Given waitFor('#options')
    When click('#options')
    Then match script("document.querySelector('#options').checked") == true

    # Está a punto de empezar, pero en el último momento se arrepiente y abandona la sala de espera.
    Given waitFor('#buttonAccion')
    When click('#buttonAccion')
    Then waitForUrl(baseUrl + '/partida/sala-espera')
    Given waitFor('#botonSalir')
    When click('#botonSalir')
    * dialog(true)
    Then waitForUrl(baseUrl)

    # Sin rendirse, vuelve a intentarlo y se lanza a una nueva partida.
    Given waitFor('#btn-jugar')
    When click('#btn-jugar')  
    Then waitFor('#modosModal')
    Then waitFor('#modo-solitario a')
    When click('#modo-solitario a')
    Then waitForUrl(baseUrl + '/partida/configuracion-partida')

    # Configura la partida con 5 rondas y fragmentos de 3 segundos.
    Given waitFor('#playlist994')
    When click('#playlist994')
    Then match script("document.querySelector('#playlist994').checked") == true
    Given waitFor('#rounds')
    When script("document.querySelector('#rounds').value = '5'; document.querySelector('#rounds').dispatchEvent(new Event('input'))")
    Then match text('#roundsValue') == '5'
    Given waitFor('#time')
    When script("document.querySelector('#time').value = '3'; document.querySelector('#time').dispatchEvent(new Event('input'))")
    Then match text('#timeValue') == '3'
    Given waitFor('#song')
    When click('#song')
    Then match script("document.querySelector('#song').checked") == true
    Given waitFor('#write')
    When click('#write')
    Then match script("document.querySelector('#write').checked") == true

    # Comienza la partida una vez más.
    Given waitFor('#buttonAccion')
    When click('#buttonAccion')
    Then waitForUrl(baseUrl + '/partida/sala-espera')
    Given waitFor('#startButton')    
    When click('#startButton')
    Then waitForUrl(baseUrl + '/partida')

    # Intenta adivinar la canción escribiendo "Uno más uno son 7", pero tras 10 segundos, se cansa y decide abandonar.
    Given waitFor('#songInput')
    And input('#songInput', 'Uno más uno son 7')
    * delay(10000)
    Given waitFor('#botonSalir')
    When click('#botonSalir')
    * dialog(true)
    Then waitForUrl(baseUrl)

    # Finalmente, decide cerrar sesión y marcharse a jugar al LoL.
    Given waitFor('#logout')
    When click('#logout')
    Then waitForUrl(baseUrl + '/login')
