import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Model {
    public String stringSeparator = "\\|";
    private int nWinThreshold = 150;
    private int nTurnCount = 0;
    private int winnerId = -1;

    // Parallel Arrays (cause too lazy to make/track a class)
    // Parallel for players
    private ArrayList<String> players = new ArrayList<String> ();
    private ArrayList<Integer> points = new ArrayList<Integer> ();
    private ArrayList<ArrayList<String>> playerRewards = new ArrayList<> ();


    // Parallel for quests
    public ArrayList<String> questNames = new ArrayList<String> ();
    public ArrayList<Integer> questPoints = new ArrayList<Integer> ();
    public ArrayList<String> questRewards = new ArrayList<String> ();
    public ArrayList<Integer> questEasyIndices = new ArrayList<Integer> ();
    public ArrayList<Integer> questMediumIndices = new ArrayList<Integer> ();
    public ArrayList<Integer> questHardIndices = new ArrayList<Integer> ();

    // Parallel for rewards
    public ArrayList<String> rewards = new ArrayList<String> ();

    public void initModel (ArrayList<String> names) {
        this.resetLists ();
        this.initPlayers (names);
        this.initQuests ();
        this.initRewards ();
    }

    public void resetLists () {
        players = new ArrayList<String> ();
        points = new ArrayList<Integer> ();
        playerRewards = new ArrayList<> ();

        questNames = new ArrayList<String> ();
        questPoints = new ArrayList<Integer> ();
        questRewards = new ArrayList<String> ();
        questEasyIndices = new ArrayList<Integer> ();
        questMediumIndices = new ArrayList<Integer> ();
        questHardIndices = new ArrayList<Integer> ();

        rewards = new ArrayList<String> ();
    }

    private void initPlayers (ArrayList<String> names) {
        int nCount = names.size ();
        int i;

        for (i = 0; i < nCount; i++) {
            players.add (names.get (i));
            points.add (0);
            playerRewards.add (new ArrayList<String> ());
        }
    }

    private void initRewards () {
        try {
            String fileName = "rewards.txt";
            BufferedReader reader = new BufferedReader (new FileReader (fileName));
            String line;

            while ((line = reader.readLine ()) != null) {
                rewards.add (line);
            }

            reader.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private void initQuests () {
        try {
            int i = 0;
            String fileName = "quests.txt";
            BufferedReader reader = new BufferedReader (new FileReader (fileName));
            String line;

            while ((line = reader.readLine ()) != null) {
                String questName = (line.split (stringSeparator))[0];
                int questDifficulty = Integer.parseInt ((line.split (stringSeparator))[1]);
                String questReward = line.split (stringSeparator)[2];
                int questPoint = Integer.parseInt ((line.split (stringSeparator))[3]);

                questNames.add (questName);
                questPoints.add (questPoint);
                questRewards.add (questReward);
                if (questDifficulty == 1)
                    questEasyIndices.add (i);
                else if (questDifficulty == 2)
                    questMediumIndices.add (i);
                else if (questDifficulty == 3)
                    questHardIndices.add (i);
                
                i++;
            }

            reader.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    
    public boolean checkWinner () {
        for (int i = 0; i < points.size (); i++)
            if (points.get (i) >= nWinThreshold) {
                winnerId = i;
                return true;
            }

        return false;
    }

    public int getWinner () {
        return winnerId;
    }

    public void incrementTurn () {
        nTurnCount++;
    }

    public void addPoints (int pId, int nPoints) {
        int nPoint = points.get (pId);
        nPoint += nPoints;
        points.set (pId, nPoint);
    }

    public void useAbility (int pId, String abilityName) {
        int rewardIndex = playerRewards.get (pId).indexOf (abilityName);
        playerRewards.remove (rewardIndex);
    }

    public void completeQuest (int pId, int qId) {
        String reward = questRewards.get (qId);
        if (!"Gold Back".equals (reward))
            playerRewards.get (pId).add (questRewards.get (qId));
        
        this.addPoints (pId, questPoints.get (qId));
    }

    
    // public String getRandomEvent () {

    // }

    // Use for random events
    public int generateRandomNumber (int min, int max) {
        if (min > max) {
            int nTemp;
            nTemp = min;
            min = max;
            max = nTemp;
        }

        Random random = new Random ();
        return random.nextInt ((max-min) + 1) + min;
    }

    public int getTurn () {
        return nTurnCount;
    }

    public ArrayList<Integer> getAllPoints () {
        return points;
    }

    public int getPoints (int pId) {
        return points.get (pId);
    }
    
    public ArrayList<String> getPlayerRewards (int pId) {
        return playerRewards.get (pId);
    }
}