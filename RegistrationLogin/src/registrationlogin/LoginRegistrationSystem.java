/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package registrationlogin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class LoginRegistrationSystem {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField phoneNumberField;
    private JPasswordField confirmPasswordField;
    private final ArrayList<Message> messages = new ArrayList<>();

    Connection connection;

    public LoginRegistrationSystem() {
        connectToDatabase();
        createUsersTable();
        createAndShowGUI();
    }

    // Connecting to MySQL database
    private void connectToDatabase() {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL server first
            String url = "jdbc:mysql://localhost:3306/";
            String username = "root";
            String password = "Request10?";
            
            // Firstly establishing a connection to MySQL
            connection = DriverManager.getConnection(url, username, password);
            
            // Database
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS userdb");
            connection.close();
            
            // database
            connection = DriverManager.getConnection(url + "userdb", username, password);
            System.out.println("Connected to the database successfully.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found!");
            JOptionPane.showMessageDialog(null, 
                "Database driver not found. Please add the MySQL connector JAR to your project.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to connect to database: " + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    //  users table 
    private void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "name VARCHAR(100)," +
                     "surname VARCHAR(100)," +
                     "phone VARCHAR(20)," +
                     "username VARCHAR(100) UNIQUE," +
                     "password VARCHAR(255))"; // Using a larger size for password
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "Error creating database table: " + e.getMessage());
        }
    }

    // Validating login using database
    public boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Login validation error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error during login: " + e.getMessage());
            return false;
        }
    }

    // Registering user in database
    public boolean registerUser(String name, String surname, String phone, String username, String password) {
        String sql = "INSERT INTO users(name, surname, phone, username, password) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, phone);
            stmt.setString(4, username);
            stmt.setString(5, password);
            stmt.executeUpdate();
            System.out.println("User registered successfully: " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(frame, "Error: Username already exists.");
            } else {
                JOptionPane.showMessageDialog(frame, "Registration error: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean validateRegistration(String password, String confirmPassword, String phoneNumber) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Password fields cannot be empty.");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match.");
            return false;
        }
        
        if (!phoneNumber.matches("^\\+27\\d{9}$")) {
            JOptionPane.showMessageDialog(frame, "Phone number must be in format: +27XXXXXXXXX");
            return false;
        }
        
        return true;
    }

    private void createAndShowGUI() {
        frame = new JFrame("Login/Registration System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500); 
        frame.setLayout(new CardLayout());

        // Login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty");
                return;
            }
            
            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose(); // Close the login window
                showQuickChatApp(); // Open the messaging app
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password");
            }
        });

        
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);

        // Registration panel
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(new BoxLayout(registrationPanel, BoxLayout.Y_AXIS));
        registrationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameField = new JTextField(15);

        JLabel phoneNumberLabel = new JLabel("Phone Number (Format: +27XXXXXXXXX):");
        phoneNumberField = new JTextField(15);

        JLabel usernameRegistrationLabel = new JLabel("Username:");
        JTextField usernameRegistrationField = new JTextField(15);

        JLabel passwordRegistrationLabel = new JLabel("Password:");
        JPasswordField passwordRegistrationField = new JPasswordField(15);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(15);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String username = usernameRegistrationField.getText();
            String password = new String(passwordRegistrationField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Basic validation
            if (name.isEmpty() || surname.isEmpty() || username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.");
                return;
            }
            
            if (validateRegistration(password, confirmPassword, phoneNumber)) {
                boolean success = registerUser(name, surname, phoneNumber, username, password);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "Registration successful!");
                    // Clear fields after successful registration
                    nameField.setText("");
                    surnameField.setText("");
                    phoneNumberField.setText("");
                    usernameRegistrationField.setText("");
                    passwordRegistrationField.setText("");
                    confirmPasswordField.setText("");
                }
            }
        });

        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(nameLabel);
        registrationPanel.add(nameField);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(surnameLabel);
        registrationPanel.add(surnameField);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(phoneNumberLabel);
        registrationPanel.add(phoneNumberField);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(usernameRegistrationLabel);
        registrationPanel.add(usernameRegistrationField);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(passwordRegistrationLabel);
        registrationPanel.add(passwordRegistrationField);
        registrationPanel.add(Box.createVerticalStrut(5));
        registrationPanel.add(confirmPasswordLabel);
        registrationPanel.add(confirmPasswordField);
        registrationPanel.add(Box.createVerticalStrut(15));
        registrationPanel.add(registerButton);

        // Switch panels
        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();

        JButton switchToRegister = new JButton("Need an account? Register");
        switchToRegister.addActionListener(e -> cardLayout.show(frame.getContentPane(), "registration"));
        
        JButton switchToLogin = new JButton("Already have an account? Login");
        switchToLogin.addActionListener(e -> cardLayout.show(frame.getContentPane(), "login"));

        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(switchToRegister);
        
        registrationPanel.add(Box.createVerticalStrut(20));
        registrationPanel.add(switchToLogin);

        frame.add(loginPanel, "login");
        frame.add(registrationPanel, "registration");
        
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // Part 2: QuickChat Application after successful login
    private void showQuickChatApp() {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        
        // Get number of messages to send
        String input = JOptionPane.showInputDialog(null, "How many messages would you like to enter?", "Message Count", JOptionPane.QUESTION_MESSAGE);
        int numMessages;
        try {
            numMessages = Integer.parseInt(input);
            if (numMessages <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number. Setting to 1 by default.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                numMessages = 1;
            }
        } catch (NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Setting to 1 by default.", "Input Error", JOptionPane.WARNING_MESSAGE);
            numMessages = 1;
        }
        
        boolean running = true;
        while (running) {
            String[] options = {"1) Send Messages", "2) Show recently sent messages", "3) Quit"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "Choose an option:",
                "QuickChat Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            switch (choice) {
                case 0 -> {
                    // Send Messages
                    if (messages.size() >= numMessages) {
                        JOptionPane.showMessageDialog(null, "You have already sent the maximum number of messages (" + numMessages + ").",
                                "Limit Reached", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        composeMessage();
                    }
                }
                    
                case 1 -> // Show recently sent messages
                    sendMessageChart();
                    
                case 2, -1 -> // Quit
                {
                    // Window close button
                    if (!messages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Total messages sent: " + messages.size(),
                                "Session Summary", JOptionPane.INFORMATION_MESSAGE);
                    }
                    running = false;
                    System.exit(0);
                }
            }
            // Quit
                    }
    }
    
    // Method to display sent messages in a chart/table format
    private void sendMessageChart() {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Message Chart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a new JFrame for the message chart
        JFrame chartFrame = new JFrame("Sent Messages Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 400);
        chartFrame.setLocationRelativeTo(null);
        
        // Create table columns
        String[] columnNames = {"Message #", "Message ID", "Recipient", "Message Text", "Message Hash"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Add message data to table
        for (Message message : messages) {
            Object[] rowData = {
                message.getMessageNum(),
                message.getMessageId(),
                message.getRecipient(),
                message.getMessageText(),
                message.getMessageHash()
            };
            tableModel.addRow(rowData);
        }
        
        // Create JTable with the model
        JTable messageTable = new JTable(tableModel);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowHeight(25);
        
        // Set column widths
        messageTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Message #
        messageTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Message ID
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Recipient
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Message Text
        messageTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Message Hash
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(messageTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout());
        JLabel summaryLabel = new JLabel("Total Messages Sent: " + messages.size());
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(summaryLabel);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> chartFrame.dispose());
        summaryPanel.add(closeButton);
        
        // Add components to frame
        chartFrame.setLayout(new BorderLayout());
        chartFrame.add(scrollPane, BorderLayout.CENTER);
        chartFrame.add(summaryPanel, BorderLayout.SOUTH);
        
        // Show the chart window
        chartFrame.setVisible(true);
    }
    
    private void composeMessage() {
        Message message = new Message(messages.size());
        
        // Get recipient
        String recipient = JOptionPane.showInputDialog(null, "Enter recipient cell number (with international code):", 
                                                     "Recipient", JOptionPane.QUESTION_MESSAGE);
        if (recipient == null) {
            return; // User canceled
        }
        
        if (message.checkRecipientCell(recipient) == 0) {
            JOptionPane.showMessageDialog(null, "Invalid recipient number. Must be 10 characters or less with international code.", 
                                         "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        message.setRecipient(recipient);
        
        // Get message text
        String messageText = JOptionPane.showInputDialog(null, "Enter your message (max 250 characters):", 
                                                      "Message", JOptionPane.QUESTION_MESSAGE);
        if (messageText == null) {
            return; // User canceled
        }
        
        if (messageText.length() > 250) {
            JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.", 
                                         "Message Too Long", JOptionPane.ERROR_MESSAGE);
            return;
        }
        message.setMessageText(messageText);
        
        // Create message hash
        message.createMessageHash();
        
        // Choosing what to do with the message
        String action = message.sentMessage();
        
        if (action.equals("send")) {
            // Add message to our list
            messages.add(message);
            
            // Display message details
            JOptionPane.showMessageDialog(null, 
                """
                Message Details:
                Message ID: """ + message.getMessageId() + "\n" +
                "Message Hash: " + message.getMessageHash() + "\n" +
                "Recipient: " + message.getRecipient() + "\n" +
                "Message: " + message.getMessageText(),
                "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                
            JOptionPane.showMessageDialog(null, "Message sent", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (action.equals("store")) {
            message.storeMessage();
            JOptionPane.showMessageDialog(null, "Message stored for later", "Stored", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Message discarded", "Discarded", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginRegistrationSystem::new);
    }
}


class Message {
    private final String messageId;
    private final int messageNum;
    private String recipient;
    private String messageText;
    private String messageHash;
    
    public Message(int messageCount) {
        this.messageNum = messageCount + 1; // Start from 1, not 0
        this.messageId = generateMessageId();
    }
    
    private String generateMessageId() {
        // Generate a random 10-digit number
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    // message ID is not more than 10 characters
    public boolean checkMessageID() {
        return messageId.length() <= 10;
    }
    
    // recipient number is valid (10 or fewer chars, has international code)
    public int checkRecipientCell(String cellNumber) {
        if (cellNumber.length() <= 12 && cellNumber.startsWith("+")) {
            return 1; // Valid
        }
        return 0; // Invalid
    }
    
    // Creating message hash from ID, message number, and first
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        // First two digits of message ID
        String idStart = messageId.substring(0, Math.min(2, messageId.length()));
        
        messageHash = idStart + ":" + messageNum + ":" + firstWord.toUpperCase() + lastWord.toUpperCase();
        return messageHash;
    }
    
    // Method to handle sending, storing, or discarding a message
    public String sentMessage() {
        String[] options = {"Send Message", "Disregard Message", "Store Message to send later"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "What would you like to do with this message?",
            "Message Options",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch(choice) {
            case 0:
                return "send";
            case 1:
                return "discard";
            case 2:
                return "store";
            default:
                return "discard";
        }
    }
    
    // Method to store message as JSON
    @SuppressWarnings("unchecked")
    public void storeMessage() {
        JSONObject messageJson = new JSONObject();
        messageJson.put("messageId", messageId);
        messageJson.put("messageNum", messageNum);
        messageJson.put("recipient", recipient);
        messageJson.put("messageText", messageText);
        messageJson.put("messageHash", messageHash);
        
        
        JSONArray messagesArray = new JSONArray();
        File jsonFile = new File("stored_messages.json");
        
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader("stored_messages.json")) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                messagesArray = (JSONArray) obj;
            } catch (IOException | ParseException e) {
                JOptionPane.showMessageDialog(null, "Error reading stored messages: " + e.getMessage(),
                                            "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
       
        messagesArray.add(messageJson);
        
        try (FileWriter file = new FileWriter("stored_messages.json")) {
            file.write(messagesArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving message: " + e.getMessage(),
                                        "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Return messages list (for displaying all messages)
    public String printMessages() {
        return "Message ID: " + messageId + 
               "\nMessage Hash: " + messageHash + 
               "\nRecipient: " + recipient + 
               "\nMessage: " + messageText;
    }
    
    // Return total messages count
    public int returnTotalMessages() {
        return messageNum;
    }
    
    // Getters and setters
    public String getMessageId() {
        return messageId;
    }
    
    public int getMessageNum() {
        return messageNum;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getMessageText() {
        return messageText;
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public String getMessageHash() {
        return messageHash;
    }
}
