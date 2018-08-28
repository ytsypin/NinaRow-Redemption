package xmlExtractor;

import Exceptions.*;
import circularGame.CircularGame;
import gameBoard.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import popoutGame.PopoutGame;
import regularGame.RegularGame;
import resources.generated.GameDescriptor;
import resources.generated.Player;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;


public class XMLExtractionTask extends Task<RegularGame> {
    private final int SLEEP_TIME = 300;
    private String fileName;

    @Override
    protected RegularGame call() throws Exception{
        RegularGame retGame = null;
        try {
            File file = new File(String.valueOf(fileName));

            JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            GameDescriptor gameDescriptor = (GameDescriptor) jaxbUnmarshaller.unmarshal(file);

            int rows = gameDescriptor.getGame().getBoard().getRows();

            int cols = gameDescriptor.getGame().getBoard().getColumns().intValue();

            int N = gameDescriptor.getGame().getTarget().intValue();

            List<Player> allPlayers = gameDescriptor.getPlayers().getPlayer();

            updateMessage("Checking rows");
            Thread.sleep(SLEEP_TIME);
            if(rows < 5 || 50 < rows){
                throw new InvalidNumberOfRowsException(rows);
            }

            updateMessage("Checking columns");
            Thread.sleep(SLEEP_TIME);
            if(cols < 6 || 30 < cols){
                throw new InvalidNumberOfColsException(cols);
            }

            updateMessage("Checking target value");
            Thread.sleep(SLEEP_TIME);
            if(N >= Math.min(rows, cols) || N < 2){
                throw new InvalidTargetException(N);
            }

            updateMessage("Checking number of players");
            Thread.sleep(SLEEP_TIME);
            if(allPlayers.size() < 2 || 6 < allPlayers.size()){
                throw new InvalidNumberOfPlayersException(allPlayers.size());
            }

            ObservableList<Participant> playerList = FXCollections.observableArrayList();
            List<Short> idList = new ArrayList<>();

            updateMessage("Checking ID number uniqueness");
            Thread.sleep(SLEEP_TIME);
            int playerNum = 1;
            for(Player player : allPlayers){
                Short playerID = player.getId();

                boolean isBot;
                if(player.getType().equals("Computer")){
                    isBot = true;
                } else if(player.getType().equals("Human")){
                    isBot = false;
                } else {
                    throw new ParticipantTypeException();
                }
                Participant participant = new Participant(player.getName(), isBot, player.getId(), playerNum);
                playerNum++;
                playerList.add(participant);
                if(idList.contains(player.getId())){
                    throw new IDDuplicateException(player.getId());
                } else {
                    idList.add(player.getId());
                }
            }

            updateMessage("Checking game type");
            Thread.sleep(SLEEP_TIME);
            if(gameDescriptor.getGame().getVariant().equals("Circular")){
                retGame = new CircularGame(N, playerList, rows, cols);
            } else if (gameDescriptor.getGame().getVariant().equals("Popout")) {
                retGame = new PopoutGame(N, playerList, rows, cols);
            } else if (gameDescriptor.getGame().getVariant().equals("Regular")) {
                retGame = new RegularGame(N, playerList, rows, cols);
            } else {
                throw new GameTypeException();
            }

            updateMessage("Done! All good!");

            this.succeeded();
            updateValue(retGame);
        } catch(JAXBException e){
            throw new WrongFileException();
        } catch (InvalidTargetException e) {
            updateMessage("Invalid goal value N.");
        } catch (GameTypeException e) {
            updateMessage("Invalid game type.");
        } catch (InvalidNumberOfColsException e) {
            updateMessage("Invalid number of columns.");
        } catch (InvalidNumberOfPlayersException e) {
            updateMessage("Invalid number of players.");
        } catch (IDDuplicateException e) {
            updateMessage("Duplicate ID found, please make sure each participant has a unique ID number.");
        } catch (InvalidNumberOfRowsException e) {
            updateMessage("Invalid number of rows.");
        } catch (ParticipantTypeException e) {
            updateMessage("Wrong participant type.");
        } finally {
            return retGame;
        }
    }

    public XMLExtractionTask(String fileName){
        this.fileName = fileName;
    }
}