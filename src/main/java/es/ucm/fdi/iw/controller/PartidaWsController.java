package es.ucm.fdi.iw.controller;

import java.util.UUID;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.ucm.fdi.iw.dto.game.GameMessage;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.dto.game.PlayerAction;
import es.ucm.fdi.iw.dto.game.PlayerJoinMessage;
import es.ucm.fdi.iw.service.PartidaService;

@Controller
public class PartidaWsController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PartidaService partidaService;

    public PartidaWsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Cuando un jugador entra en la sala de espera
    @MessageMapping("/game/wait/{gameId}")
    public void waitForPlayers(@DestinationVariable UUID gameId, @Payload PlayerJoinMessage payload) {
        Long playerId = payload.getPlayerId();
        partidaService.addPlayerToGame(gameId, playerId);
        Set<GamePlayerDTO> players = partidaService.getGamePlayers(gameId);
        // Notificar a todos los jugadores en la sala de espera
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/waiting",
                new GameMessage("player_joined", players));
    }

    // Cuando el juego comienza
    @MessageMapping("/game/start/{gameId}")
    public void startGame(@DestinationVariable String gameId) {
        // Actualiza el estado del juego

        // Notificar a todos los jugadores que el juego ha comenzado
        GameMessage message = new GameMessage("game_start", "El juego ha comenzado!");
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
    }

    // Cuando una ronda comienza
    @MessageMapping("/game/startRound/{gameId}")
    public void startRound(@DestinationVariable String gameId) {
        // Lógica para iniciar la ronda

        // Notificar a todos los jugadores que la ronda ha comenzado
        GameMessage message = new GameMessage("round_start", "La ronda ha comenzado!");
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
    }

    // Cuando un jugador realiza una acción (por ejemplo, responde)
    @MessageMapping("/game/action/{gameId}")
    public void playerAction(PlayerAction action, @DestinationVariable String gameId) {
        // Aquí se valida la acción del jugador (respuesta, acción especial, etc.)

        // Notificar a todos los jugadores sobre la acción realizada
        GameMessage message = new GameMessage("player_answer",
                action.getPlayerId() + " ha respondido: " + action.getAnswer());
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
    }

    // Cuando una ronda termina
    @MessageMapping("/game/endRound/{gameId}")
    public void endRound(@DestinationVariable String gameId) {
        // Lógica para finalizar la ronda

        // Notificar a todos los jugadores que la ronda ha terminado
        GameMessage message = new GameMessage("round_end", "La ronda ha terminado.");
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
    }

    // Cuando el juego ha terminado
    @MessageMapping("/game/end/{gameId}")
    public void endGame(@DestinationVariable String gameId) {
        // Lógica para finalizar el juego

        // Notificar a todos los jugadores que el juego ha terminado
        GameMessage message = new GameMessage("game_over", "El juego ha terminado.");
        messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
    }
}
