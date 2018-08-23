package gameBoard;

import javafx.beans.property.*;

import java.util.Objects;

public class Participant{
    private static int symbolNum;

    private SimpleStringProperty name;
    private SimpleBooleanProperty isBot;
    private SimpleIntegerProperty turnsTaken;
    private int participantSymbol;
    private SimpleIntegerProperty idNum;

    public Participant(String name, boolean isBot, int idNum){
        this.name = new SimpleStringProperty(name);
        this.isBot = new SimpleBooleanProperty(isBot);
        this.turnsTaken = new SimpleIntegerProperty(0);
        this.participantSymbol = ++symbolNum;
        this.idNum = new SimpleIntegerProperty(idNum);
    }

    public String getName() {
        return name.getValue();
    }

    public Boolean getIsBot() {
        return isBot.getValue();
    }

    public Integer getTurnsTaken() {
        return turnsTaken.getValue();
    }

    public Integer getIdNum(){ return idNum.getValue();}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant participant = (Participant) o;
        return isBot == participant.isBot &&
                turnsTaken == participant.turnsTaken &&
                Objects.equals(name, participant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isBot, turnsTaken);
    }

    public void addTurnPlayed() {
        turnsTaken.set(turnsTaken.getValue()+1);
    }

    public int getParticipantSymbol() {
        return participantSymbol;
    }

    public SimpleIntegerProperty turnsTakenProperty() {
        return turnsTaken;
    }
}
