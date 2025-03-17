Feature: partida solitario

#
# Aqui vamos a testear una aprtida en solitario
#

Scenario: solo-game completo
    #Login de usuario B
    Given driver baseUrl + '/login'
    And input('#username', 'b')
    And input('#password', 'aa')
    When submit().click(".form-signin button")
    Then waitForUrl(baseUrl)

    #Entrar en Partida
    Given waitFor('#btn-jugar')
    When click('#btn-jugar')  

    # Esperar a que el modal se abra
    Then waitFor('#modosModal')

    # Clic en "Solitario" 
    Then waitFor('#modo-solitario a')
    When click('#modo-solitario a')
    Then waitForUrl(baseUrl + '/configuracion-partida')

    #Configuracion de Partida
    # Selección de Playlist
    Given waitFor('#playlist')
    When select('#playlist', '1')
    Then match value('#playlist') == '1'

    # Ajustar el número de rondas a 5
    Given waitFor('#rondas')
    When script("document.querySelector('#rondas').value = '5'; document.querySelector('#rondas').dispatchEvent(new Event('input'))")
    Then match text('#rondasValue') == '5'

    # Configurar duración del fragmento a 20 segundos
    Given waitFor('#tiempo')
    When script("document.querySelector('#tiempo').value = '10'; document.querySelector('#tiempo').dispatchEvent(new Event('input'))")
    Then match text('#tiempoValue') == '10'

    # Seleccionar el modo de juego "Escribir la Canción"
    Given waitFor('#escribir')
    When click('#escribir')
    Then match script("document.querySelector('#escribir').checked") == true

    # Aceptar Configuracion
    Given waitFor('button#btn-guardar-configuracion')
    When click('button#btn-guardar-configuracion')
    Then waitForUrl(baseUrl + '/sala-espera')

    # Iniciar Partida
    Given waitFor('button#startButton')    
    When click('button#startButton')
    Then waitForUrl(baseUrl + '/partida')

    # Jugar / simular partida (ya se añadirá)
    # Bucle de 0 a Rondas
        # 1 Recibir info ronda
        # 2 Comienza Reproducción fragmento
        # 3 Marca nombre cancion 
        # 4 Se resuelve cancion 
        # 5 se reparten puntos
        # 6 Fin ronda

    # Ir a vista resultados y de ahí al inicio
    Given driver baseUrl + '/resultados'
    Given waitFor('a#btn-continue')    
    When click('a#btn-continue')
    Then waitForUrl(baseUrl)






    
