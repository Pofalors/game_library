import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.net.URISyntaxException;

public class GameDatabaseApp extends JFrame implements ActionListener {

    private JTextField idField, nameField, ageField, userIdField, nameOfGameField, releaseDateField, hoursPlayedField;
    private JTextArea queryField;
    private JButton insertButton, deleteButton, updateButton, setQueryButton, exitButton, insertGameButton, deleteGameButton, updateGameButton, showTablesButton, showGameTablesButton, musicButton;
    // Στατικές μεταβλητές για τα στοιχεία σύνδεσης με τη βάση δεδομένων
    private static final String URL = "jdbc:mysql://localhost:3306/myAppDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    // Μεταβλητές για τον ήχο
    private Clip clip;
    private boolean isPlaying = true;

    // Μέθοδος σύνδεσης με τη βάση δεδομένων
    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to MySQL has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public GameDatabaseApp() {
        setTitle("GamesDB-SQL");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background image
        JLabel background = new JLabel(new ImageIcon("C:\\Users\\Go - Gaming Station\\Desktop\\ΜΕΤΑΠΤΥΧΙΑΚΟ\\2ο Εξάμηνο\\ΒΑΣΕΙΣ ΔΕΔΟΜΕΝΩΝ\\εργασίες\\Εργασία 3η Εφαρμογή (προαιρετική)\\Animal_Crossing.jpg"));
        background.setBounds(0, 0, 581, 500); 
        add(background);

        // Labels and TextFields
        JLabel idLabel = new JLabel("ID");
        idLabel.setBounds(20, 20, 100, 25);
        background.add(idLabel);

        idField = new JTextField();
        idField.setBounds(120, 20, 150, 25);
        background.add(idField);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(20, 60, 100, 25);
        background.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(120, 60, 150, 25);
        background.add(nameField);

        JLabel ageLabel = new JLabel("Age");
        ageLabel.setBounds(20, 100, 100, 25);
        background.add(ageLabel);

        ageField = new JTextField();
        ageField.setBounds(120, 100, 150, 25);
        background.add(ageField);

        // Buttons
        insertButton = new JButton("Insert");
        insertButton.setBounds(20, 140, 80, 25);
        background.add(insertButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(110, 140, 80, 25);
        background.add(deleteButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(200, 140, 80, 25);
        background.add(updateButton);

        showTablesButton = new JButton("Show Tables");
        showTablesButton.setBounds(290, 140, 120, 25);
        background.add(showTablesButton);

        // TextFields for game details
        JLabel userIdLabel = new JLabel("User ID");
        userIdLabel.setBounds(20, 180, 100, 25);
        background.add(userIdLabel);

        userIdField = new JTextField();
        userIdField.setBounds(120, 180, 150, 25);
        background.add(userIdField);

        JLabel nameOfGameLabel = new JLabel("Name of Game");
        nameOfGameLabel.setBounds(20, 220, 100, 25);
        background.add(nameOfGameLabel);

        nameOfGameField = new JTextField();
        nameOfGameField.setBounds(120, 220, 150, 25);
        background.add(nameOfGameField);

        JLabel releaseDateLabel = new JLabel("Release Date");
        releaseDateLabel.setBounds(20, 260, 100, 25);
        background.add(releaseDateLabel);

        releaseDateField = new JTextField();
        releaseDateField.setBounds(120, 260, 150, 25);
        background.add(releaseDateField);

        JLabel hoursPlayedLabel = new JLabel("Hours Played");
        hoursPlayedLabel.setBounds(20, 300, 100, 25);
        background.add(hoursPlayedLabel);

        hoursPlayedField = new JTextField();
        hoursPlayedField.setBounds(120, 300, 150, 25);
        background.add(hoursPlayedField);

        // Buttons for game actions
        insertGameButton = new JButton("Insert Game");
        insertGameButton.setBounds(20, 340, 120, 25);
        background.add(insertGameButton);

        deleteGameButton = new JButton("Delete Game");
        deleteGameButton.setBounds(150, 340, 120, 25);
        background.add(deleteGameButton);

        updateGameButton = new JButton("Update Game");
        updateGameButton.setBounds(280, 340, 120, 25);
        background.add(updateGameButton);

        showGameTablesButton = new JButton("Show Games");
        showGameTablesButton.setBounds(410, 340, 120, 25);
        background.add(showGameTablesButton);

        // Query TextArea
        queryField = new JTextArea();
        queryField.setBounds(20, 380, 380, 75);
        queryField.setLineWrap(true);
        queryField.setWrapStyleWord(true);
        background.add(queryField);

        setQueryButton = new JButton("Enter your Query");
        setQueryButton.setBounds(400, 400, 150, 25);
        background.add(setQueryButton);

        // Exit and Music button (upper right)
        exitButton = new JButton("Exit");
        exitButton.setBounds(500, 10, 80, 25);
        background.add(exitButton);

        musicButton = new JButton("🎼");
        musicButton.setBounds(500, 60, 80, 25);
        background.add(musicButton);
        loadBackgroundMusic("Octopath Traveler 2.wav");

        // Action Listeners
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        insertButton.addActionListener(this);
        deleteButton.addActionListener(this);
        updateButton.addActionListener(this);
        insertGameButton.addActionListener(this);
        deleteGameButton.addActionListener(this);
        updateGameButton.addActionListener(this);
        setQueryButton.addActionListener(this);
        showTablesButton.addActionListener(this);
        showGameTablesButton.addActionListener(this);
        musicButton.addActionListener(this);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        // Ανάλογα με το κουμπί εκτελείται και το αντίστοιχο μπλοκ κώδικα!
        if (source == insertButton) {
            String idStr = idField.getText().trim();
            String nameStr = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            try {
                playSound(); // Παίζει ήχος κατά το πάτημα του κουμπιού
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!idStr.isEmpty() && !nameStr.isEmpty() && !ageStr.isEmpty()) {
                try {
                    int ID = Integer.parseInt(idStr);
                    int age = Integer.parseInt(ageStr);
                    insert(ID, nameStr, age);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid ID or Age format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == deleteButton) {
            String idStr = idField.getText().trim();
            String nameStr = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!idStr.isEmpty() && nameStr.isEmpty() && ageStr.isEmpty()) {
                try {
                    int ID = Integer.parseInt(idStr);
                    deleteUser(ID);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid ID format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (!idStr.isEmpty() && !nameStr.isEmpty() | !ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill only the ID field!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill the ID field!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == updateButton) {
            String idStr = idField.getText().trim();
            String nameStr = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!idStr.isEmpty() && !nameStr.isEmpty() && !ageStr.isEmpty()) {
                try {
                    int ID = Integer.parseInt(idStr);
                    int age = Integer.parseInt(ageStr);
                    update(ID, nameStr, age);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid ID or Age format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (source == showTablesButton) {
            String sql = "SELECT id, name, age FROM users";
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }

            try (Connection conn = connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)) {

                StringBuilder sb = new StringBuilder(String.format("%-5s %-25s %-5s", "ID", "Name", "Age \n"));
                sb.append("-------------------------------------- \n");
                while (rs.next()) {
                    sb.append(String.format("%-5d %-25s %-5d", rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
                    sb.append("\n");
                }
                showMessageDialog("Users Table", sb.toString());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == insertGameButton) {
            String useridStr = userIdField.getText().trim();
            String nameOGStr = nameOfGameField.getText().trim();
            String rdStr = releaseDateField.getText().trim();
            String hoursStr = hoursPlayedField.getText().trim();
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!useridStr.isEmpty() && !nameOGStr.isEmpty() && !rdStr.isEmpty()) {
                try {
                    int userID = Integer.parseInt(useridStr);
                    int hours = Integer.parseInt(hoursStr);
                    insertGame(userID, nameOGStr, rdStr, hours);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid userID or hours played format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == deleteGameButton) {
            String useridStr = userIdField.getText().trim();
            String nameOGStr = nameOfGameField.getText().trim();
            String rdStr = releaseDateField.getText().trim();
            String hoursStr = hoursPlayedField.getText().trim();
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!useridStr.isEmpty() && !nameOGStr.isEmpty() && rdStr.isEmpty() && hoursStr.isEmpty()) {
                try {
                    int userID = Integer.parseInt(useridStr);
                    deleteGame(userID, nameOGStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid userID format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (!useridStr.isEmpty() && !nameOGStr.isEmpty() && !rdStr.isEmpty() | !hoursStr.isEmpty()) {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill only the userID and Name of Game field!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill the userID and Name of Game field!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (source == updateGameButton) {
            String useridStr = userIdField.getText().trim();
            String nameOGStr = nameOfGameField.getText().trim();
            String rdStr = releaseDateField.getText().trim();
            String hoursStr = hoursPlayedField.getText().trim();
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            if (!useridStr.isEmpty() && nameOGStr.isEmpty() && rdStr.isEmpty() && hoursStr.isEmpty()) {
                try {
                    int userID = Integer.parseInt(useridStr);
                    int hours = Integer.parseInt(hoursStr);
                    updateGame(userID, rdStr, hoursStr, hours);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Invalid userID or hours format!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                        "Please fill in all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (source == showGameTablesButton) {
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            String sql = "SELECT user_id, name_of_game, release_date, hours_played FROM games";

            try (Connection conn = connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sql)) {

                StringBuilder sb = new StringBuilder(String.format("%-10s %-30s %-15s %-12s", "User ID", "Game Name", "Release Date", "Hours Played \n"));
                sb.append("---------------------------------------------------------------------- \n");
                while (rs.next()) {
                    sb.append(String.format("%-10d %-30s %-15s %-12d", rs.getInt("user_id"), rs.getString("name_of_game"), rs.getDate("release_date"), rs.getInt("hours_played")));
                    sb.append("\n");
                }
                showMessageDialog("Games Table", sb.toString());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == setQueryButton) {
            try {
                playSound();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
                    ex.printStackTrace();
            }
            String query = queryField.getText().trim();
            executeUserQuery(query);
        } else if (source == musicButton) {
            toggleBackgroundMusic();
        }
    }
    // Μέθοδος για ενημερωτικό Popup μήνυμα
    private void showMessageDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    // Μέθοδος για τη δημιουργία νέων πινάκων
    private static void createNewTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (\n"
                                 + "    id INTEGER PRIMARY KEY,\n"
                                 + "    name TEXT NOT NULL,\n"
                                 + "    age INTEGER\n"
                                 + ");";
        
        String createGamesTable = "CREATE TABLE IF NOT EXISTS games (\n"
                                 + "    user_id INTEGER,\n"
                                 + "    name_of_game TEXT(255) NOT NULL,\n"
                                 + "    release_date DATE NOT NULL,\n"
                                 + "    hours_played INTEGER,\n"
                                 + "    PRIMARY KEY (user_id, name_of_game(255)),\n"
                                 + "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n"
                                 + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createGamesTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Μέθοδος για την εισαγωγή δεδομένων
    private void insert(int id, String name, int age) {
        String sql = "INSERT INTO users(id, name, age) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.executeUpdate();
            showMessageDialog("Insert", "Users inserted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδος για την εισαγωγή παιχνιδιών
    private void insertGame(int userId, String nameOfGame, String releaseDate, int hoursPlayed) {
        // Έλεγχος αν υπάρχει ήδη εγγραφή με το ίδιο user_id και name_of_game
        String checkSql = "SELECT COUNT(*) FROM games WHERE user_id = ? AND name_of_game = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, nameOfGame);
            ResultSet rs = checkStmt.executeQuery();

            // rs.next(): Ελέγχει αν το αποτέλεσμα του ερωτήματος περιέχει τουλάχιστον μία γραμμή.
            // rs.getInt(1) > 0: Ελέγχει αν η τιμή της πρώτης στήλης (ο αριθμός των εγγραφών που ταιριάζουν με τα κριτήρια) είναι μεγαλύτερη από το μηδέν.
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "The game already exists for this user. No duplicate entry allowed.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Εισαγωγή νέας εγγραφής αν δεν υπάρχει διπλότυπο
        String insertSql = "INSERT INTO games(user_id, name_of_game, release_date, hours_played) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, nameOfGame);
            pstmt.setDate(3, Date.valueOf(releaseDate));
            pstmt.setInt(4, hoursPlayed);
            pstmt.executeUpdate();
            showMessageDialog("Insert Game", "Game inserted successfully.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Give proper values inside the text fields",
                            "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδος για τη διαγραφή χρηστών
    private void deleteUser(int id) {
        String sqlgame = "DELETE FROM games WHERE user_id = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sqlgame)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            String sql = "DELETE FROM users WHERE id = ?";

            try (Connection conn1 = connect();
                PreparedStatement pstmt1 = conn.prepareStatement(sql)) {
                pstmt1.setInt(1, id);
                pstmt1.executeUpdate();
                JOptionPane.showMessageDialog(GameDatabaseApp.this,
                "User and related games deleted successfully.", 
                "Delete", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Μέθοδος για τη διαγραφή συγκεκριμένων παιχνιδιών
    private void deleteGame(int userId, String nameOfGame) {
        String sql = "DELETE FROM games WHERE user_id = ? AND name_of_game = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, nameOfGame);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                "Game deleted successfully.", 
                "Delete", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδος για την ενημέρωση δεδομένων
    private void update(int id, String name, int age) {
        String sql = "UPDATE users SET name = ?, age = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            showMessageDialog("Update User", "User updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδος για την ενημέρωση παιχνιδιών
    private void updateGame(int userId, String nameOfGame, String releaseDate, int hoursPlayed) {
        String sql = "UPDATE games SET hours_played = ? WHERE user_id = ? AND name_of_game = ? AND release_date = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hoursPlayed);
            pstmt.setInt(2, userId);
            pstmt.setString(3, nameOfGame);
            pstmt.setDate(4, Date.valueOf(releaseDate));
            pstmt.executeUpdate();
            showMessageDialog("Update Game", "Game updated successfully.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            "Give proper values inside the text fields",
                            "Error", JOptionPane.ERROR_MESSAGE);
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδος για την εκτέλεση δυναμικών SQL ερωτημάτων από τον χρήστη
    public void executeUserQuery(String userQuery) {

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(userQuery)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            StringBuilder rb = new StringBuilder();

            // Εκτύπωση των κεφαλίδων των στηλών
            for (int i = 1; i <= columnCount; i++) {
                rb.append(String.format("%-20s", rsmd.getColumnName(i)));
            }
            rb.append("\n");
            rb.append("----------------------------------------------------------------- \n");

            // Εκτύπωση των δεδομένων
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                   rb.append(String.format("%-20s", rs.getString(i)));
                }
                rb.append("\n");
            }
            showMessageDialog("Answer of Query", rb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(GameDatabaseApp.this,
                            e.getMessage(),
                            "Error Executing Query", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Μέθοδοι Ήχου BackGround και Κουμπιών
    private void playSound() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        try {
            File file = new File(getClass().getResource("Button Click.wav").toURI());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (URISyntaxException | IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    private void loadBackgroundMusic(String soundFile) {
        try {
            File file = new File(getClass().getResource(soundFile).toURI());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (URISyntaxException | IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }
    private void toggleBackgroundMusic() {
        if (clip != null) {
            if (isPlaying) {
                clip.stop();
            } else {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
            isPlaying = !isPlaying;
        }
    }

    public static void main(String[] args) {
        // Δημιουργία πινάκων
        createNewTables();
        new GameDatabaseApp();
    }
}