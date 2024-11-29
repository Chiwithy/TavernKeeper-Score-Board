import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class View {
    private JFrame frame;
    private JPanel mainMenuPanel;
    private JPanel configPanel;
    private JPanel playerNameContainer;
    private JPanel scorePanel;

    private ArrayList<String> players;
    private ArrayList<JTextField> playerFields;

    private ArrayList<JLabel> playerScores;
    private ArrayList<String> questEasy;
    private ArrayList<String> questMedium;
    private ArrayList<String> questHard;

    private Controller controller;

    private String currCardName = "";
    private String currCardImage = "";
    private int currCardDifficulty = 1;
    private JPanel lastClickedCard = null;

    private int questNum = 3;

    public View (Controller controller) {
        frame = new JFrame ("Game Configuration");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setSize (400, 500);

        this.controller = controller;

        showMainMenu ();

        frame.setVisible (true);
    }

    private void showMainMenu () {
        mainMenuPanel = new JPanel ();
        mainMenuPanel.setLayout (new BorderLayout());
        mainMenuPanel.setBorder (BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel ("Game Menu", JLabel.CENTER);
        titleLabel.setFont (new Font("Arial", Font.BOLD, 24));
        mainMenuPanel.add (titleLabel, BorderLayout.NORTH);

        // Main Menu buttons
        JButton startButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        startButton.addActionListener(e -> showConfigScreen());
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel ();
        buttonPanel.add (startButton);
        buttonPanel.add (exitButton);

        mainMenuPanel.add (buttonPanel, BorderLayout.CENTER);

        frame.getContentPane ().removeAll();
        frame.getContentPane ().add (mainMenuPanel);
        frame.pack ();
        frame.revalidate ();
        frame.repaint ();
    }

    private void showConfigScreen () {
        configPanel = new JPanel ();
        configPanel.setLayout (new BorderLayout ());
        configPanel.setBorder (BorderFactory.createEmptyBorder (15, 15, 15, 15));

        players = new ArrayList<String> ();
        playerFields = new ArrayList<JTextField> ();

        JLabel configTitle = new JLabel ("Tavernkeeper Registration", JLabel.CENTER);
        configTitle.setFont(new Font ("Arial", Font.BOLD, 20));
        configPanel.add (configTitle, BorderLayout.NORTH);

        playerNameContainer = new JPanel ();
        playerNameContainer.setLayout (new BoxLayout (playerNameContainer, BoxLayout.Y_AXIS));

        // 5 players change if want more
        for (int i = 0; i < 5; i++) {
            addPlayerField(i < 2);  //First two should be enabled (Min number of players)
        }

        JScrollPane scrollPane = new JScrollPane (playerNameContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        configPanel.add (scrollPane, BorderLayout.CENTER);


        JButton startGameButton = new JButton ("Start Game");
        startGameButton.addActionListener (e -> startGame ());

        JPanel buttonPanel = new JPanel (new FlowLayout ());
        buttonPanel.add (startGameButton);

        configPanel.add (buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane ().removeAll ();
        frame.getContentPane ().add (configPanel);
        frame.pack ();
        frame.revalidate ();
        frame.repaint ();
    }

    private void addPlayerField(boolean isInitial) {
        JPanel playerPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
        playerPanel.setBorder (BorderFactory.createEmptyBorder (0, 0, 5, 25)); // Margin for spacing between player list

        JLabel nameLabel = new JLabel ("Name:");
        JTextField nameField = new JTextField (20);
        nameField.setEnabled (isInitial);
        playerFields.add (nameField);
        JButton interactButton = new JButton ("Add Keeper");

        // Enables/Disables the buttons changing from add/remove
        if (!isInitial) {
            interactButton.addActionListener (e -> {
                if ("Add Keeper".equals (interactButton.getText ())) {  //If current button is add then change to remove
                    nameField.setEnabled (true);
                    interactButton.setText ("Remove Keeper");
                }
                else {
                    nameField.setEnabled (false);       //Vice versa
                    nameField.setText ("");
                    interactButton.setText ("Add Keeper");
                }
            });
        }

        playerPanel.add(nameLabel);
        playerPanel.add(nameField);

        if (!isInitial)
            playerPanel.add(interactButton);

        playerNameContainer.add(playerPanel);
    }

    private void startGame() {
        // Loop through the player text fields
        for (int i = 0; i < playerFields.size (); i++) {
            // Only count as players those with enabled text fields (they pressed the [Add] button)
            if (playerFields.get (i).isEnabled ()) {
                String playerName = playerFields.get (i).getText ();
                if ("".equals (playerName.trim ()))     // Default name if empty fields
                    playerName = "Keeper " + (i + 1);
                players.add (playerName);   //Add to players
            }
        }

        System.out.println("Starting game with players: " + players);

        initLists ();
        showScorePanel();
    }

    private void initLists () {
        controller.initModel (players);
        playerScores = new ArrayList<JLabel> ();

        //Get loaded quest and ability names from model
        questEasy = new ArrayList<String> ();
        questMedium = new ArrayList<String> ();
        questHard = new ArrayList<String> ();

        addToQuestList(1, questNum);
        addToQuestList(2, questNum);
        addToQuestList(3, questNum);
    }

    private void showScorePanel () {
        scorePanel = new JPanel ();
        scorePanel.setLayout (new BoxLayout (scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBorder (BorderFactory.createEmptyBorder (15, 15, 15, 15));

        for (int i = 0; i < players.size (); i++) {
            int pId = i;
            String playerName = players.get (pId);
            JPanel scorePanelItem = new JPanel ();
            JLabel playerScore = new JLabel ("0");
            scorePanelItem.setLayout (new FlowLayout(FlowLayout.LEFT));
            
            // Add "Abilities" button / Opens up player's list of abilities
            JButton abilitiesButton = new JButton ("Abilities");
            abilitiesButton.addActionListener (e -> showAbilitiesDialog (playerName, pId));
            
            // Visually added from left to right
            scorePanelItem.add (abilitiesButton);
            scorePanelItem.add (new JLabel (playerName + " Score: "));
            scorePanelItem.add (playerScore);
            playerScores.add (playerScore); // Put JLabel in arraylist for updating later
            
            scorePanel.add (scorePanelItem);
        }
    
        // Main Menu button
        JButton backButton = new JButton ("End Game Session");
        backButton.addActionListener (e -> showMainMenu ());
    
        // Quest Board button / Opens up list of quests
        JButton questBoardButton = new JButton ("Quest Board");
        questBoardButton.addActionListener (e -> showQuestBoardDialog ());
    
        // Elements are added right first
        JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.RIGHT));
        buttonPanel.add (backButton);
        buttonPanel.add (questBoardButton); // Add "Quest Board" button
    
        scorePanel.add (buttonPanel);
    
        frame.getContentPane ().removeAll ();
        frame.getContentPane ().add (scorePanel);
        frame.pack ();
        frame.revalidate ();
        frame.repaint ();
    }
    
    // Show list of abilities
    private void showAbilitiesDialog (String playerName, int pId) {
        JDialog abilitiesDialog = new JDialog (frame, playerName + "'s Abilities", true);
        abilitiesDialog.setSize (900, 700);
        abilitiesDialog.setLayout (new BorderLayout ());
    
        JButton closeButton = new JButton ("Close");
        closeButton.addActionListener (e -> abilitiesDialog.dispose());
    
        // View button / Show bigger image on separate window
        JButton viewButton = new JButton("View");
        viewButton.setEnabled(false); // Initially disabled
        
        viewButton.addActionListener(x -> {
            // Placeholder for the "View" button action
            viewCard(currCardName, currCardImage, false);
        });

        // Use Ability button / should just remove the ability from player's list
        JButton useAbilityButton = new JButton("Use Ability");
        useAbilityButton.setEnabled(false); // Initially disabled enables only when a card is clicked

        // Panel to hold the cards
        JPanel cardsPanel = new JPanel ();
        cardsPanel.setLayout (new GridLayout(0, 4, 20, 20)); // 4 cards per row
        ArrayList<String> playerAbilities = controller.getPlayerAbilities (pId);

        // Create image
        for (int i = 0; i < playerAbilities.size (); i++) {
            String abilityName = playerAbilities.get (i);
            JPanel card;
    
            // Filenames are in lower case
            String imagePath = "resources/rewards/" + abilityName.toLowerCase () + ".png"; // Adjust path as needed

            card = createCard (abilityName, imagePath, viewButton, useAbilityButton, -1);
            cardsPanel.add(card);
        }
    
        // Wrap the cardsPanel in a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane (cardsPanel);
        scrollPane.setBorder (BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds margins around the gallery
        abilitiesDialog.add (scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.RIGHT));

        // Action button clicked
        useAbilityButton.addActionListener (e -> {
            JDialog confirmDialog = new JDialog ((JFrame) null, "Use Ability", true);
            
            confirmDialog.setLayout (new BorderLayout ());
            confirmDialog.setSize (300, 150);
        
            // Create a label with the ability name
            JLabel promptLabel = new JLabel ("Use " + currCardName + "?", JLabel.CENTER); // Replace "[Ability Name]" with the actual ability name
            promptLabel.setBorder (BorderFactory.createEmptyBorder (5, 5, 5, 5));
        
            // Cancel and Use button
            JButton cancelButton = new JButton ("Cancel");
            cancelButton.addActionListener (cancelEvent -> {
                confirmDialog.dispose (); // Close the dialog
            });

            JButton useButton = new JButton ("Use Ability");
            useButton.addActionListener (useEvent -> {
                controller.useAbility (pId, currCardName);
                confirmDialog.dispose ();
                abilitiesDialog.dispose ();
            });
        
            JPanel actionButtonPanel = new JPanel ();
            actionButtonPanel.add (cancelButton);
            actionButtonPanel.add (useButton);
        
            confirmDialog.add (promptLabel, BorderLayout.CENTER);
            confirmDialog.add (actionButtonPanel, BorderLayout.SOUTH);
        
            confirmDialog.setLocationRelativeTo (null); // Center of screen
            confirmDialog.setVisible (true);
        });
    
        buttonPanel.add (viewButton);
        buttonPanel.add (useAbilityButton);
        buttonPanel.add (closeButton);
    
        abilitiesDialog.add (buttonPanel, BorderLayout.SOUTH);
        abilitiesDialog.pack ();
    
        abilitiesDialog.setLocationRelativeTo (frame);
        abilitiesDialog.setVisible (true);    
    }
    
    // Show quests
    private void showQuestBoardDialog () {
        // Create a dialog for the Quest Board
        JDialog questBoardDialog = new JDialog (frame, "Quest Board", true);
        questBoardDialog.setLayout (new BorderLayout ());
        questBoardDialog.setSize (1000, 700);
    
        // Main panel to hold all quest categories
        JPanel questBoardPanel = new JPanel ();
        questBoardPanel.setLayout (new BoxLayout (questBoardPanel, BoxLayout.Y_AXIS));
        questBoardPanel.setBorder (BorderFactory.createEmptyBorder (15, 15, 15, 15));
        
        // View button
        JButton viewButton = new JButton ("View");
        viewButton.setEnabled (false); // Initially disabled

        JButton actionButton = new JButton ("Claim Quest");
        actionButton.setEnabled (false); // Initially disabled
    
        // Add sections for Easy, Medium, Hard quests
        questBoardPanel.add (createQuestSection (1, viewButton, actionButton));
        questBoardPanel.add (createQuestSection (2, viewButton, actionButton));
        questBoardPanel.add (createQuestSection (3, viewButton, actionButton));
    
        // Wrap the quest board panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane (questBoardPanel);
        scrollPane.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20)); // Padding around the content
        questBoardDialog.add (scrollPane, BorderLayout.CENTER);
    
        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> questBoardDialog.dispose());

        viewButton.addActionListener(e -> {
            viewCard(currCardName, currCardImage, true);
        });

        actionButton.addActionListener (e -> {
            showQuestDialog (currCardName, questBoardDialog);
        });
        

        JPanel buttonPanel = new JPanel();
        buttonPanel.add (closeButton);
        buttonPanel.add (viewButton);
        buttonPanel.add (actionButton);
        questBoardDialog.add (buttonPanel, BorderLayout.SOUTH);
    
        questBoardDialog.setLocationRelativeTo (frame);
        questBoardDialog.setVisible (true);
    }
    
    private JPanel createQuestSection (int difficulty, JButton viewButton, JButton actionButton) {
        String sectionHeader = "Easy Quests - 2 Reputation Each";
        String diffFolder = "easy";
        ArrayList<String> questList = questEasy;

        if (difficulty == 2) {
            sectionHeader = "Medium Quests - 10 Reputation Each";
            questList = questMedium;
            diffFolder = "medium";
        }
        else if (difficulty == 3) {
            sectionHeader = "Hard Quests - 50 Reputation Each";
            questList = questHard;
            diffFolder = "hard";
        }
        
        // Panel for difficulty section
        JPanel sectionPanel = new JPanel ();
        sectionPanel.setLayout (new BorderLayout ());
        sectionPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 20, 0));

        // Section header
        JLabel headerLabel = new JLabel (sectionHeader, SwingConstants.LEFT);
        headerLabel.setFont (new Font ("Arial", Font.BOLD, 16));
        sectionPanel.add (headerLabel, BorderLayout.NORTH);
    
        // Grid layout for quest cards
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(0, 4, 15, 15)); // Max 4 cards per row with gaps (adjusted to 4 columns)
    
        // Add quest cards to the grid
        int numQuests = questList.size () > questNum ? questNum : questList.size ();
        for (int i = 0; i < numQuests; i++) {
            String questName = questList.get (i);

            String imagePath = "resources/quests/" + diffFolder + "/" + questName.toLowerCase() + ".png"; // Adjust path as needed
            JPanel card = createCard (questName, imagePath, viewButton, actionButton, difficulty);
            cardsPanel.add (card);
        }
    
        sectionPanel.add (cardsPanel, BorderLayout.CENTER);
        return sectionPanel;
    }

    private void addToQuestList (int difficulty, int quantity) {
        ArrayList<Integer> diffIndices = controller.getQuestIndicesByDifficulty (difficulty);   // Quest indices of a difficulty
        quantity = diffIndices.size () > quantity ? quantity : diffIndices.size ();

        for (int i = 0; i < quantity; i++) {
            int randId = controller.getRandomNumber (0, diffIndices.size ());
            String questName = controller.getQuestById (diffIndices.get (randId));
            diffIndices.remove (randId);
            
            if (difficulty == 2)
                questMedium.add (questName);
            else if (difficulty == 3)
                questHard.add (questName);
            else questEasy.add (questName);
        }
    }

    private void showQuestDialog (String questName, JDialog parentDialog) {
        int cId = controller.getQuestId (questName);

        JDialog questDialog = new JDialog (parentDialog, "Claim Quest", true);
        questDialog.setLayout (new BorderLayout());
    
        // Panel for radio buttons
        JPanel radioPanel = new JPanel ();
        radioPanel.setLayout (new BoxLayout (radioPanel, BoxLayout.Y_AXIS));
        ButtonGroup group = new ButtonGroup ();
        ArrayList<JRadioButton> radioButtons = new ArrayList<JRadioButton> ();  // Arraylist for player-radio button index
    
        JButton claimButton = new JButton ("Claim Quest");
        claimButton.setEnabled (false);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(ev -> questDialog.dispose());

        // Make radio button for each player / choose which player completed the quest
        for (String playerName : players) {
            JRadioButton radioButton = new JRadioButton (playerName);
            group.add (radioButton);
            radioPanel.add (radioButton);
            radioButtons.add (radioButton);

            radioButton.addActionListener (e -> {
                if (!claimButton.isEnabled ())
                    claimButton.setEnabled (true);
            });
        }
    
        // Padding for radio panel
        radioPanel.setBorder (BorderFactory.createEmptyBorder(20, 20, 20, 20));
        questDialog.add (radioPanel, BorderLayout.CENTER);
    
        // Buttons at the bottom
        JPanel buttonPanel = new JPanel ();
    
        buttonPanel.add (backButton);

        claimButton.addActionListener (e -> {
            int selectedIndex = -1;    
            ArrayList<String> questList = questEasy;
            if (currCardDifficulty == 2)
                questList = questMedium;
            else if (currCardDifficulty == 3)
                questList = questHard;

            for (int i = 0; i < radioButtons.size (); i++) {
                if (radioButtons.get (i).isSelected ()) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex != -1) {
                controller.completeQuest (selectedIndex, cId);
                questList.remove ((String) questName);
                addToQuestList (currCardDifficulty, 1);
                
                questDialog.dispose ();
                parentDialog.dispose ();

                updateScore (selectedIndex);
            } else {
                claimButton.setEnabled (false);
            }
        });
        buttonPanel.add (claimButton);
    
        questDialog.add (buttonPanel, BorderLayout.SOUTH);
    
        // Automatically size the dialog to fit its content
        questDialog.pack ();
    
        // Center the dialog relative to the parent
        questDialog.setLocationRelativeTo (parentDialog);
    
        // Show the dialog
        questDialog.setVisible (true);
    }

    private JPanel createCard (String cardName, String imagePath, JButton viewButton, JButton actionButton, int cardDifficulty) {
        JPanel card = new JPanel ();
        card.setLayout (new BorderLayout ());
    
        ImageIcon icon = new ImageIcon (imagePath);
        int originalWidth = icon.getIconWidth ();
        int originalHeight = icon.getIconHeight ();
        double scaleFactor = 0.1;
        int scaledWidth = (int) (originalWidth * scaleFactor);
        int scaledHeight = (int) (originalHeight * scaleFactor);
    
        Image scaledImage = icon.getImage ().getScaledInstance (scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel (new ImageIcon (scaledImage));
        imageLabel.setHorizontalAlignment (SwingConstants.CENTER);
    
        card.add (imageLabel, BorderLayout.CENTER);
    
        JLabel cardLabel = new JLabel(cardName, SwingConstants.CENTER);
        card.add (cardLabel, BorderLayout.SOUTH);
        card.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
        
        // Mouse listener for when card is clicked / Enables the view/action button
        card.addMouseListener (new MouseAdapter() {
            @Override
            public void mouseClicked (MouseEvent e) {
                cardClicked (card, viewButton, actionButton, cardName, imagePath, cardDifficulty);
            }
        });

        return card;
    }

    private void cardClicked (JPanel cardPanel, JButton viewButton, JButton actionButton, String cardName, String imagePath, int cardDifficulty) {
        // Enable the view/action buttons
        viewButton.setEnabled (true);
        actionButton.setEnabled (true);

        // Set current selected cards
        currCardName = cardName;
        currCardImage = imagePath;
        currCardDifficulty = cardDifficulty;

        try {
            // Remove previously selected card's border/background
            if (lastClickedCard != null) {
                lastClickedCard.setBorder (null);
                lastClickedCard.setBackground(null);
            }
        } catch (Exception e) {}
        
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        cardPanel.setBackground(Color.LIGHT_GRAY); // Set the desired background color
        cardPanel.setOpaque(true); // Ensure the background color is visible

        lastClickedCard = cardPanel;
    }

    private void viewCard (String cardName, String imagePath, boolean isQuest) {
        String title = cardName;
        JDialog imageDialog = new JDialog (frame, title, true);
        imageDialog.setLayout (new BorderLayout ());
    
        // Load the image
        ImageIcon icon = new ImageIcon (imagePath);
    
        // Fixed dimensions for the scaled image
        double scaleFactor = 0.25;
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();
        int scaledWidth = (int) (originalWidth * scaleFactor);
        int scaledHeight = (int) (originalHeight * scaleFactor);
    
        // Scale the image to the fixed dimensions
        Image scaledImage = icon.getImage().getScaledInstance(
                scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
    
        // Add the scaled image to the dialog
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        imagePanel.add(imageLabel, BorderLayout.CENTER);
    
        imageDialog.add(imagePanel, BorderLayout.CENTER);
    
        // Add buttons at the bottom
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(ev -> imageDialog.dispose());

        buttonPanel.add(closeButton);
        imageDialog.add(buttonPanel, BorderLayout.SOUTH);
    
        imageDialog.pack();
        imageDialog.setLocationRelativeTo(frame);
        imageDialog.setVisible(true);
    }

    public void updateScore (int pId) {
        int newScore = controller.getScore (pId);
        playerScores.get (pId).setText ("" + newScore);
        scorePanel.revalidate();
        scorePanel.repaint();

        checkForWinner ();
    }

    public void restartGame () {
        initLists ();
        showScorePanel ();
    }

    public void checkForWinner () {
        boolean winnerFound = controller.checkWinner();
        if (winnerFound) {
            int winnerId = controller.getWinner ();
            String winnerMessage = players.get (winnerId) + " has achieved Legendary Tavernkeeper status";

            Object[] options = {"Exit to Main Menu", "Start New Game with Current Players"};

            int choice = JOptionPane.showOptionDialog (
                    null,
                    winnerMessage,
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            
            if (choice == 0) {  // To Main Menu
                showMainMenu ();
            } else if (choice == 1) {   // Restart Game
                restartGame();
            }
        }
    }
}