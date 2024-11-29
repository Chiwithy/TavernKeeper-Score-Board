import java.util.ArrayList;

public class Controller {
    public String stringSeparator = "|";
    private Model model;

    public Controller (Model model) {
        this.model = model;
    }

    public void incrementTurn () {
        model.incrementTurn ();
    }

    public int getTurn () {
        return model.getTurn ();
    }


    public void initModel (ArrayList<String> names) {
        model.initModel (names);
    }

    public boolean checkWinner () {
        return model.checkWinner ();
    }

    public int getWinner () {
        return model.getWinner ();
    }

    public void useAbility (int pId, String abilityName) {
        model.useAbility (pId, abilityName);
    }

    public void completeQuest (int pId, int qId) {
        model.completeQuest(pId, qId);
    }

    public int getRandomNumber (int min, int max) {
        return model.generateRandomNumber (min, max);
    }

    public ArrayList<Integer> getAllPoints () {
        return model.getAllPoints ();
    }

    public int getScore (int pId) {
        return model.getPoints (pId);
    }

    public String getQuestById (int qId) {
        return model.questNames.get (qId);
    }
    public int getQuestId (String questName) {
        return model.questNames.indexOf ((String) questName);
    }

    public ArrayList<String> getAllQuestNames () {
        return model.questNames;
    }

    public ArrayList<String> getPlayerAbilities (int pId) {
        return model.getPlayerRewards (pId);
    }

    public ArrayList<Integer> getQuestIndicesByDifficulty (int difficulty) {
        if (difficulty == 2)
            return model.questMediumIndices;
        else if (difficulty == 3)
            return model.questHardIndices;
        else
            return model.questEasyIndices;
    }

    public ArrayList<String> getAllAbilityNames () {
        return model.rewards;
    }

    public String getCardInfo (int cId) {
        return model.rewards.get (cId);
    }
}