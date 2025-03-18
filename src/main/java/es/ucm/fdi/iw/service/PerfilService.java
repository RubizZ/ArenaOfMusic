package es.ucm.fdi.iw.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PerfilService {

    public class ObjectCard {
        private String imageUrl;
        private String name;
        private String description;

        public ObjectCard(String imageUrl, String name, String description) {
            this.imageUrl = imageUrl;
            this.name = name;
            this.description = description;
        }

        // Getters e Setters
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class Match {
        private String opponent;
        private Integer result;

        public Match(String opponent, Integer result) {
            this.opponent = opponent;
            this.result = result;
        }

        public String getOpponent() {
            return opponent;
        }

        public Integer getResult() {
            return result;
        }
    }

    public List<ObjectCard> getObjectCards() {
        return List.of(
                new ObjectCard("/img/default-obj.png", "Object 1", "Description 1"),
                new ObjectCard("/img/default-obj.png", "Object 2", "Description 2"),
                new ObjectCard("/img/default-obj.png", "Object 3", "Description 3"));
    }

    public List<Match> getMatches() {
        return List.of(
                new Match("Juan", 1),
                new Match("Maria", -1),
                new Match("Carlos", 0));
    }
}
