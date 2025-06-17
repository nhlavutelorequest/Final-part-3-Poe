/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

// Part 3: This is Extended QuickChat Application with Arrays and Additional Features

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginRegistrationSystem extends JFrame {
    private List<Message> messages;
    private List<Message> sentMessages;
    private List<Message> disregardedMessages;
    private List<Message> storedMessages;
    private List<String> messageHashes;
    private List<String> messageIds;
    
    public LoginRegistrationSystem() {
        messages = new ArrayList<>();
        sentMessages = new ArrayList<>();
        disregardedMessages = new ArrayList<>();
        storedMessages = new ArrayList<>();
        messageHashes = new ArrayList<>();
        messageIds = new ArrayList<>();
        
        // Load stored messages from JSON file on startup
        loadStoredMessages();
        
        showLogin();
    }
    
    // Method to load stored messages from JSON file into array
    private void loadStoredMessages() {
        File jsonFile = new File("stored_messages.json");
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader("stored_messages.json")) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                JSONArray messagesArray = (JSONArray) obj;
                
                for (Object messageObj : messagesArray) {
                    JSONObject messageJson = (JSONObject) messageObj;
                    Message message = new Message(0); // Temporary constructor call
                    message.setRecipient((String) messageJson.get("recipient"));
                    message.setMessageText((String) messageJson.get("messageText"));
                    message.createMessageHash();
                    storedMessages.add(message);
                }
            } catch (IOException | ParseException e) {
                System.out.println("Error loading stored messages: " + e.getMessage());
            }
        }
    }
    
    private void showLogin() {
        String username = JOptionPane.showInputDialog(null, "Enter username:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (username == null) System.exit(0);
        
        String password = JOptionPane.showInputDialog(null, "Enter password:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (password == null) System.exit(0);
        
        if (username.equals("admin") && password.equals("password")) {
            showQuickChatApp();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
            showLogin();
        }
    }
    
    private void showQuickChatApp() {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        
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
            String[] options = {
                "1) Send Messages", 
                "2) Show recently sent messages", 
                "3) Display sender and recipient of all sent messages",
                "4) Display longest sent message",
                "5) Search for message by ID",
                "6) Search messages by recipient",
                "7) Delete message by hash",
                "8) Display full report of all messages",
                "9) Sort messages by recipient",
                "10) Sort messages by message length",
                "11) Display message statistics",
                "12) Find duplicate recipients",
                "13) Export messages to text file",
                "14) Import and send stored messages",
                "15) Clear all message arrays",
                "16) Display messages by date pattern",
                "17) Advanced message filtering",
                "18) Quit"
            };
            
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
                    if (sentMessages.size() >= numMessages) {
                        JOptionPane.showMessageDialog(null, "You have already sent the maximum number of messages (" + numMessages + ").",
                                "Limit Reached", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        composeMessage();
                    }
                }
                case 1 -> sendMessageChart();
                case 2 -> displaySenderRecipientAll();
                case 3 -> displayLongestMessage();
                case 4 -> searchMessageById();
                case 5 -> searchMessagesByRecipient();
                case 6 -> deleteMessageByHash();
                case 7 -> displayFullReport();
                case 8 -> displayFullReport();
                case 9 -> sortMessagesByRecipient();
                case 10 -> sortMessagesByLength();
                case 11 -> displayMessageStatistics();
                case 12 -> findDuplicateRecipients();
                case 13 -> exportMessagesToFile();
                case 14 -> importAndSendStoredMessages();
                case 15 -> clearAllArrays();
                case 16 -> displayMessagesByDatePattern();
                case 17 -> advancedMessageFiltering();
                case 18, -1 -> {
                    if (!messages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Total messages sent: " + sentMessages.size(),
                                "Session Summary", JOptionPane.INFORMATION_MESSAGE);
                    }
                    running = false;
                    System.exit(0);
                }
            }
        }
    }
    
    // Display sender and recipient of all sent messages
    private void displaySenderRecipientAll() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Sender/Recipient List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder display = new StringBuilder("All Sent Messages - Sender & Recipient:\n\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            display.append("Message ").append(i + 1).append(":\n");
            display.append("Sender: admin\n"); // Assuming current user is admin
            display.append("Recipient: ").append(msg.getRecipient()).append("\n");
            display.append("Message ID: ").append(msg.getMessageId()).append("\n\n");
        }
        
        JTextArea textArea = new JTextArea(display.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(null, scrollPane, "Sender/Recipient List", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Display the longest sent message
    private void displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Longest Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Message longestMessage = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.getMessageText().length() > longestMessage.getMessageText().length()) {
                longestMessage = msg;
            }
        }
        
        String display = "Longest Message Details:\n\n" +
                        "Message ID: " + longestMessage.getMessageId() + "\n" +
                        "Recipient: " + longestMessage.getRecipient() + "\n" +
                        "Message Length: " + longestMessage.getMessageText().length() + " characters\n" +
                        "Message: " + longestMessage.getMessageText();
        
        JOptionPane.showMessageDialog(null, display, "Longest Message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Search for a message by ID and display corresponding recipient and message
    private void searchMessageById() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Search by ID", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String searchId = JOptionPane.showInputDialog(null, "Enter Message ID to search:", 
                                                     "Search by ID", JOptionPane.QUESTION_MESSAGE);
        if (searchId == null) return;
        
        Message foundMessage = null;
        for (Message msg : sentMessages) {
            if (msg.getMessageId().equals(searchId)) {
                foundMessage = msg;
                break;
            }
        }
        
        if (foundMessage != null) {
            String display = "Message Found:\n\n" +
                            "Message ID: " + foundMessage.getMessageId() + "\n" +
                            "Recipient: " + foundMessage.getRecipient() + "\n" +
                            "Message Hash: " + foundMessage.getMessageHash() + "\n" +
                            "Message: " + foundMessage.getMessageText();
            JOptionPane.showMessageDialog(null, display, "Message Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No message found with ID: " + searchId, 
                                        "Message Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Search for all messages sent to a particular recipient
    private void searchMessagesByRecipient() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Search by Recipient", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String searchRecipient = JOptionPane.showInputDialog(null, "Enter recipient number to search:", 
                                                           "Search by Recipient", JOptionPane.QUESTION_MESSAGE);
        if (searchRecipient == null) return;
        
        List<Message> foundMessages = new ArrayList<>();
        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals(searchRecipient)) {
                foundMessages.add(msg);
            }
        }
        
        if (!foundMessages.isEmpty()) {
            StringBuilder display = new StringBuilder("Messages to " + searchRecipient + ":\n\n");
            for (int i = 0; i < foundMessages.size(); i++) {
                Message msg = foundMessages.get(i);
                display.append("Message ").append(i + 1).append(":\n");
                display.append("Message ID: ").append(msg.getMessageId()).append("\n");
                display.append("Message Hash: ").append(msg.getMessageHash()).append("\n");
                display.append("Message: ").append(msg.getMessageText()).append("\n\n");
            }
            
            JTextArea textArea = new JTextArea(display.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));
            
            JOptionPane.showMessageDialog(null, scrollPane, "Messages to Recipient", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No messages found for recipient: " + searchRecipient, 
                                        "No Messages Found", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Delete a message using the message hash
    private void deleteMessageByHash() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Delete by Hash", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String searchHash = JOptionPane.showInputDialog(null, "Enter Message Hash to delete:", 
                                                       "Delete by Hash", JOptionPane.QUESTION_MESSAGE);
        if (searchHash == null) return;
        
        Message messageToDelete = null;
        for (Message msg : sentMessages) {
            if (msg.getMessageHash().equals(searchHash)) {
                messageToDelete = msg;
                break;
            }
        }
        
        if (messageToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Are you sure you want to delete this message?\n\n" +
                "Message ID: " + messageToDelete.getMessageId() + "\n" +
                "Recipient: " + messageToDelete.getRecipient() + "\n" +
                "Message: " + messageToDelete.getMessageText(),
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                sentMessages.remove(messageToDelete);
                messages.remove(messageToDelete);
                messageHashes.remove(messageToDelete.getMessageHash());
                messageIds.remove(messageToDelete.getMessageId());
                
                JOptionPane.showMessageDialog(null, "Message deleted successfully!", 
                                            "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No message found with hash: " + searchHash, 
                                        "Message Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Display a report that lists the full details of all sent messages
    private void displayFullReport() {
        JFrame reportFrame = new JFrame("Full Message Report");
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.setSize(900, 500);
        reportFrame.setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Sent Messages Tab
        tabbedPane.addTab("Sent Messages (" + sentMessages.size() + ")", createMessageTable(sentMessages));
        
        // Disregarded Messages Tab
        tabbedPane.addTab("Disregarded Messages (" + disregardedMessages.size() + ")", createMessageTable(disregardedMessages));
        
        // Stored Messages Tab
        tabbedPane.addTab("Stored Messages (" + storedMessages.size() + ")", createMessageTable(storedMessages));
        
        // Arrays Summary Tab
        JPanel summaryPanel = createArraySummaryPanel();
        tabbedPane.addTab("Arrays Summary", summaryPanel);
        
        reportFrame.add(tabbedPane);
        reportFrame.setVisible(true);
    }
    
    private JScrollPane createMessageTable(List<Message> messageList) {
        String[] columnNames = {"Message #", "Message ID", "Recipient", "Message Text", "Message Hash"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (int i = 0; i < messageList.size(); i++) {
            Message message = messageList.get(i);
            Object[] rowData = {
                i + 1,
                message.getMessageId(),
                message.getRecipient(),
                message.getMessageText(),
                message.getMessageHash()
            };
            tableModel.addRow(rowData);
        }
        
        JTable messageTable = new JTable(tableModel);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowHeight(25);
        
        messageTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        messageTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        messageTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        return new JScrollPane(messageTable);
    }
    
    private JPanel createArraySummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        StringBuilder summary = new StringBuilder();
        summary.append("ARRAY SUMMARY REPORT\n");
        summary.append("===================\n\n");
        
        summary.append("1. Sent Messages Array:\n");
        summary.append("   - Contains all messages that were sent\n");
        summary.append("   - Current count: ").append(sentMessages.size()).append("\n\n");
        
        summary.append("2. Disregarded Messages Array:\n");
        summary.append("   - Contains all messages that were disregarded\n");
        summary.append("   - Current count: ").append(disregardedMessages.size()).append("\n\n");
        
        summary.append("3. Stored Messages Array:\n");
        summary.append("   - Contains all messages stored for later sending\n");
        summary.append("   - Current count: ").append(storedMessages.size()).append("\n\n");
        
        summary.append("4. Message Hashes Array:\n");
        summary.append("   - Contains all message hashes for sent messages\n");
        summary.append("   - Current count: ").append(messageHashes.size()).append("\n\n");
        
        summary.append("5. Message IDs Array:\n");
        summary.append("   - Contains all message IDs for sent messages\n");
        summary.append("   - Current count: ").append(messageIds.size()).append("\n\n");
        
        summary.append("DETAILED ARRAYS CONTENT:\n");
        summary.append("========================\n\n");
        
        if (!messageHashes.isEmpty()) {
            summary.append("Message Hashes: ");
            summary.append(String.join(", ", messageHashes)).append("\n\n");
        }
        
        if (!messageIds.isEmpty()) {
            summary.append("Message IDs: ");
            summary.append(String.join(", ", messageIds)).append("\n\n");
        }
        
        JTextArea textArea = new JTextArea(summary.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Sort messages by recipient alphabetically
    private void sortMessagesByRecipient() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to sort.", "Sort Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<Message> sortedMessages = new ArrayList<>(sentMessages);
        sortedMessages.sort(Comparator.comparing(Message::getRecipient));
        
        displaySortedMessages(sortedMessages, "Messages Sorted by Recipient");
    }
    
    // Sort messages by message length (longest first)
    private void sortMessagesByLength() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to sort.", "Sort Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<Message> sortedMessages = new ArrayList<>(sentMessages);
        sortedMessages.sort((m1, m2) -> Integer.compare(m2.getMessageText().length(), m1.getMessageText().length()));
        
        displaySortedMessages(sortedMessages, "Messages Sorted by Length (Longest First)");
    }
    
    private void displaySortedMessages(List<Message> sortedMessages, String title) {
        JFrame sortFrame = new JFrame(title);
        sortFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sortFrame.setSize(800, 400);
        sortFrame.setLocationRelativeTo(null);
        
        String[] columnNames = {"#", "Message ID", "Recipient", "Message Text", "Length", "Hash"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (int i = 0; i < sortedMessages.size(); i++) {
            Message message = sortedMessages.get(i);
            Object[] rowData = {
                i + 1,
                message.getMessageId(),
                message.getRecipient(),
                message.getMessageText(),
                message.getMessageText().length(),
                message.getMessageHash()
            };
            tableModel.addRow(rowData);
        }
        
        JTable messageTable = new JTable(tableModel);
        messageTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(messageTable);
        sortFrame.add(scrollPane);
        sortFrame.setVisible(true);
    }
    
    // Display comprehensive message statistics
    private void displayMessageStatistics() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to analyze.", "Statistics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Calculate statistics
        int totalMessages = sentMessages.size();
        int totalCharacters = sentMessages.stream().mapToInt(m -> m.getMessageText().length()).sum();
        double averageLength = (double) totalCharacters / totalMessages;
        
        String longestMessage = sentMessages.stream()
                .max(Comparator.comparing(m -> m.getMessageText().length()))
                .get().getMessageText();
        
        String shortestMessage = sentMessages.stream()
                .min(Comparator.comparing(m -> m.getMessageText().length()))
                .get().getMessageText();
        
        // Count unique recipients
        Set<String> uniqueRecipients = sentMessages.stream()
                .map(Message::getRecipient)
                .collect(Collectors.toSet());
        
        // Most common words
        Map<String, Integer> wordCount = new HashMap<>();
        for (Message msg : sentMessages) {
            String[] words = msg.getMessageText().toLowerCase().split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z]", "");
                if (!word.isEmpty()) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }
        
        String mostCommonWord = wordCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        
        StringBuilder stats = new StringBuilder();
        stats.append("MESSAGE STATISTICS REPORT\n");
        stats.append("=========================\n\n");
        stats.append("Total Messages Sent: ").append(totalMessages).append("\n");
        stats.append("Total Messages Stored: ").append(storedMessages.size()).append("\n");
        stats.append("Total Messages Disregarded: ").append(disregardedMessages.size()).append("\n\n");
        
        stats.append("CHARACTER ANALYSIS:\n");
        stats.append("Total Characters: ").append(totalCharacters).append("\n");
        stats.append("Average Message Length: ").append(String.format("%.2f", averageLength)).append(" characters\n");
        stats.append("Longest Message: ").append(longestMessage.length()).append(" characters\n");
        stats.append("Shortest Message: ").append(shortestMessage.length()).append(" characters\n\n");
        
        stats.append("RECIPIENT ANALYSIS:\n");
        stats.append("Unique Recipients: ").append(uniqueRecipients.size()).append("\n");
        stats.append("Messages per Recipient: ").append(String.format("%.2f", (double) totalMessages / uniqueRecipients.size())).append("\n\n");
        
        stats.append("CONTENT ANALYSIS:\n");
        stats.append("Most Common Word: '").append(mostCommonWord).append("'\n");
        stats.append("Total Unique Words: ").append(wordCount.size()).append("\n\n");
        
        stats.append("SAMPLE MESSAGES:\n");
        stats.append("Longest: \"").append(longestMessage.substring(0, Math.min(50, longestMessage.length()))).append("...\"\n");
        stats.append("Shortest: \"").append(shortestMessage).append("\"\n");
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(null, scrollPane, "Message Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Find recipients who received multiple messages
    private void findDuplicateRecipients() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to analyze.", "Duplicate Recipients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Map<String, List<Message>> recipientMap = new HashMap<>();
        for (Message msg : sentMessages) {
            recipientMap.computeIfAbsent(msg.getRecipient(), k -> new ArrayList<>()).add(msg);
        }
        
        StringBuilder duplicates = new StringBuilder();
        duplicates.append("DUPLICATE RECIPIENTS REPORT\n");
        duplicates.append("===========================\n\n");
        
        boolean foundDuplicates = false;
        for (Map.Entry<String, List<Message>> entry : recipientMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                foundDuplicates = true;
                duplicates.append("Recipient: ").append(entry.getKey()).append("\n");
                duplicates.append("Messages Sent: ").append(entry.getValue().size()).append("\n");
                duplicates.append("Messages:\n");
                
                for (int i = 0; i < entry.getValue().size(); i++) {
                    Message msg = entry.getValue().get(i);
                    duplicates.append("  ").append(i + 1).append(") ").append(msg.getMessageText()).append("\n");
                }
                duplicates.append("\n");
            }
        }
        
        if (!foundDuplicates) {
            duplicates.append("No duplicate recipients found.\nAll messages were sent to unique recipients.");
        }
        
        JTextArea textArea = new JTextArea(duplicates.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        
        JOptionPane.showMessageDialog(null, scrollPane, "Duplicate Recipients", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Export all messages to a text file
    private void exportMessagesToFile() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to export.", "Export Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            String filename = "QuickChat_Export_" + System.currentTimeMillis() + ".txt";
            FileWriter writer = new FileWriter(filename);
            
            writer.write("QUICKCHAT MESSAGE EXPORT\n");
            writer.write("========================\n");
            writer.write("Export Date: " + new Date() + "\n");
            writer.write("Total Messages: " + sentMessages.size() + "\n\n");
            
            for (int i = 0; i < sentMessages.size(); i++) {
                Message msg = sentMessages.get(i);
                writer.write("Message " + (i + 1) + ":\n");
                writer.write("  ID: " + msg.getMessageId() + "\n");
                writer.write("  Recipient: " + msg.getRecipient() + "\n");
                writer.write("  Hash: " + msg.getMessageHash() + "\n");
                writer.write("  Text: " + msg.getMessageText() + "\n");
                writer.write("  Length: " + msg.getMessageText().length() + " characters\n\n");
            }
            
            writer.close();
            JOptionPane.showMessageDialog(null, "Messages exported successfully to: " + filename, 
                                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                                        
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting messages: " + e.getMessage(), 
                                        "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Import stored messages and allow sending them
    private void importAndSendStoredMessages() {
        if (storedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored messages to import.", "Import Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder messageList = new StringBuilder("Select messages to send:\n\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message msg = storedMessages.get(i);
            messageList.append((i + 1)).append(") To: ").append(msg.getRecipient())
                      .append(" - \"").append(msg.getMessageText()).append("\"\n");
        }
        
        String input = JOptionPane.showInputDialog(null, messageList.toString() + 
                                                  "\nEnter message numbers to send (comma-separated, e.g., 1,3,5):", 
                                                  "Import and Send", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;
        
        try {
            String[] numbers = input.split(",");
            int sentCount = 0;
            
            for (String numStr : numbers) {
                int index = Integer.parseInt(numStr.trim()) - 1;
                if (index >= 0 && index < storedMessages.size()) {
                    Message msg = storedMessages.get(index);
                    
                    // Move to sent messages
                    sentMessages.add(msg);
                    messages.add(msg);
                    messageHashes.add(msg.getMessageHash());
                    messageIds.add(msg.getMessageId());
                    
                    sentCount++;
                }
            }
            
            // Remove sent messages from stored messages
            for (String numStr : numbers) {
                int index = Integer.parseInt(numStr.trim()) - 1;
                if (index >= 0 && index < storedMessages.size()) {
                    storedMessages.remove(index);
                }
            }
            
            JOptionPane.showMessageDialog(null, sentCount + " messages sent successfully!", 
                                        "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                                        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please use numbers separated by commas.", 
                                        "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Clear all message arrays with confirmation
    private void clearAllArrays() {
        String[] options = {"Clear Sent Messages", "Clear Stored Messages", "Clear Disregarded Messages", "Clear All", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, 
                                                "What would you like to clear?", 
                                                "Clear Arrays", 
                                                JOptionPane.DEFAULT_OPTION, 
                                                JOptionPane.QUESTION_MESSAGE, 
                                                null, options, options[4]);
        
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure? This action cannot be undone.", 
                                                  "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        switch (choice) {
            case 0 -> {
                sentMessages.clear();
                messages.clear();
                messageHashes.clear();
                messageIds.clear();
                JOptionPane.showMessageDialog(null, "Sent messages cleared.", "Clear Complete", JOptionPane.INFORMATION_MESSAGE);
            }
            case 1 -> {
                storedMessages.clear();
                // Also clear the JSON file
                try (FileWriter writer = new FileWriter("stored_messages.json")) {
                    writer.write("[]");
                } catch (IOException e) {
                    // Handle error silently
                }
                JOptionPane.showMessageDialog(null, "Stored messages cleared.", "Clear Complete", JOptionPane.INFORMATION_MESSAGE);
            }
            case 2 -> {
                disregardedMessages.clear();
                JOptionPane.showMessageDialog(null, "Disregarded messages cleared.", "Clear Complete", JOptionPane.INFORMATION_MESSAGE);
            }
            case 3 -> {
                sentMessages.clear();
                messages.clear();
                storedMessages.clear();
                disregardedMessages.clear();
                messageHashes.clear();
                messageIds.clear();
                
                // Clear JSON file
                try (FileWriter writer = new FileWriter("stored_messages.json")) {
                    writer.write("[]");
                } catch (IOException e) {
                    // Handle error silently
                }
                JOptionPane.showMessageDialog(null, "All message arrays cleared.", "Clear Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // Display messages with date-like patterns in their hashes
    private void displayMessagesByDatePattern() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to analyze.", "Date Pattern Analysis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder result = new StringBuilder("MESSAGES WITH DATE-LIKE PATTERNS\n");
        result.append("=================================\n\n");
        
        boolean foundPatterns = false;
        for (Message msg : sentMessages) {
            String hash = msg.getMessageHash();
            
            // Look for numeric patterns that might represent dates
            if (hash.matches(".*\\d{2}.*") || hash.contains("20") || hash.contains("19")) {
                foundPatterns = true;
                result.append("Message ID: ").append(msg.getMessageId()).append("\n");
                result.append("Hash: ").append(hash).append("\n");
                result.append("Recipient: ").append(msg.getRecipient()).append("\n");
                result.append("Message: ").append(msg.getMessageText()).append("\n\n");
            }
        }
        
        if (!foundPatterns) {
            result.append("No messages with date-like patterns found in hashes.");
        }
        
        JTextArea textArea = new JTextArea(result.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        
        JOptionPane.showMessageDialog(null, scrollPane, "Date Pattern Analysis", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Advanced filtering options
    private void advancedMessageFiltering() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to filter.", "Advanced Filtering", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] filterOptions = {
            "Messages longer than X characters",
            "Messages shorter than X characters", 
            "Messages containing specific word",
            "Messages to international numbers",
            "Messages with specific hash pattern",
            "Messages by recipient country code"
        };
        
        int choice = JOptionPane.showOptionDialog(null, "Select filter type:", "Advanced Filtering",
                                                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                                null, filterOptions, filterOptions[0]);
        
        List<Message> filteredMessages = new ArrayList<>();
        
        switch (choice) {
            case 0 -> { // Longer than X characters
                String input = JOptionPane.showInputDialog("Enter minimum character count:");
                try {
                    int minLength = Integer.parseInt(input);
                    filteredMessages = sentMessages.stream()
                            .filter(m -> m.getMessageText().length() > minLength)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            case 1 -> { // Shorter than X characters
                String input = JOptionPane.showInputDialog("Enter maximum character count:");
                try {
                    int maxLength = Integer.parseInt(input);
                    filteredMessages = sentMessages.stream()
                            .filter(m -> m.getMessageText().length() < maxLength)
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            case 2 -> { // Containing specific word
                String word = JOptionPane.showInputDialog("Enter word to search for:");
                if (word != null) {
                    filteredMessages = sentMessages.stream()
                            .filter(m -> m.getMessageText().toLowerCase().contains(word.toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
            case 3 -> { // International numbers
                filteredMessages = sentMessages.stream()
                        .filter(m -> m.getRecipient().startsWith("+"))
                        .collect(Collectors.toList());
            }
            case 4 -> { // Specific hash pattern
                String pattern = JOptionPane.showInputDialog("Enter hash pattern (e.g., '12:' for hashes starting with 12:):");
                if (pattern != null) {
                    filteredMessages = sentMessages.stream()
                            .filter(m -> m.getMessageHash().contains(pattern))
                            .collect(Collectors.toList());
                }
            }
            case 5 -> { // By country code
                String countryCode = JOptionPane.showInputDialog("Enter country code (e.g., +27 for South Africa):");
                if (countryCode != null) {
                    filteredMessages = sentMessages.stream()
                            .filter(m -> m.getRecipient().startsWith(countryCode))
                            .collect(Collectors.toList());
                }
            }
        }
        
        if (filteredMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages match the filter criteria.", 
                                        "Filter Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            displaySortedMessages(filteredMessages, "Filtered Messages (" + filteredMessages.size() + " found)");
        }
    }
    
    private void sendMessageChart() {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet.", 
                                        "Message Chart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFrame chartFrame = new JFrame("Recent Messages Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(800, 400);
        chartFrame.setLocationRelativeTo(null);
        
        String[] columnNames = {"Message #", "Message ID", "Recipient", "Message Text", "Message Hash"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
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
        
        JTable messageTable = new JTable(tableModel);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowHeight(25);
        
        messageTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        messageTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        messageTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(messageTable);
        
        JPanel summaryPanel = new JPanel(new FlowLayout());
        JLabel summaryLabel = new JLabel("Total Messages: " + messages.size());
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(summaryLabel);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> chartFrame.dispose());
        summaryPanel.add(closeButton);
        
        chartFrame.setLayout(new BorderLayout());
        chartFrame.add(scrollPane, BorderLayout.CENTER);
        chartFrame.add(summaryPanel, BorderLayout.SOUTH);
        
        chartFrame.setVisible(true);
    }
    
    private void composeMessage() {
        Message message = new Message(messages.size());
        
        String recipient = JOptionPane.showInputDialog(null, "Enter recipient cell number (with international code):", 
                                                     "Recipient", JOptionPane.QUESTION_MESSAGE);
        if (recipient == null) return;
        
        if (message.checkRecipientCell(recipient) == 0) {
            JOptionPane.showMessageDialog(null, "Invalid recipient number. Must be 12 characters or less with international code.", 
                                         "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        message.setRecipient(recipient);
        
        String messageText = JOptionPane.showInputDialog(null, "Enter your message (max 250 characters):", 
                                                      "Message", JOptionPane.QUESTION_MESSAGE);
        if (messageText == null) return;
        
        if (messageText.length() > 250) {
            JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.", 
                                         "Message Too Long", JOptionPane.ERROR_MESSAGE);
            return;
        }
        message.setMessageText(messageText);
        
        message.createMessageHash();
        
        String action = message.sentMessage();
        
        if (action.equals("send")) {
            messages.add(message);
            sentMessages.add(message);
            messageHashes.add(message.getMessageHash());
            messageIds.add(message.getMessageId());
            
            JOptionPane.showMessageDialog(null, 
                "Message Details:\n" +
                "Message ID: " + message.getMessageId() + "\n" +
                "Message Hash: " + message.getMessageHash() + "\n" +
                "Recipient: " + message.getRecipient() + "\n" +
                "Message: " + message.getMessageText(),
                "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                
            JOptionPane.showMessageDialog(null, "Message sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (action.equals("store")) {
            message.storeMessage();
            storedMessages.add(message);
            JOptionPane.showMessageDialog(null, "Message stored for later", "Stored", JOptionPane.INFORMATION_MESSAGE);
        } else {
            disregardedMessages.add(message);
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
        this.messageNum = messageCount + 1;
        this.messageId = generateMessageId();
    }
    
    private String generateMessageId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    public boolean checkMessageID() {
        return messageId.length() <= 10;
    }
    
    public int checkRecipientCell(String cellNumber) {
        if (cellNumber.length() <= 12 && cellNumber.startsWith("+")) {
            return 1;
        }
        return 0;
    }
    
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        String idStart = messageId.substring(0, Math.min(2, messageId.length()));
        
        messageHash = idStart + ":" + messageNum + ":" + firstWord.toUpperCase() + lastWord.toUpperCase();
        return messageHash;
    }
    
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
            case 0: return "send";
            case 1: return "discard";
            case 2: return "store";
            default: return "discard";
        }
    }
    
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
    
    public String printMessages() {
        return "Message ID: " + messageId + 
               "\nMessage Hash: " + messageHash + 
               "\nRecipient: " + recipient + 
               "\nMessage: " + messageText;
    }
    
    public int returnTotalMessages() {
        return messageNum;
    }
    
    // Getters and setters
    public String getMessageId() { return messageId; }
    public int getMessageNum() { return messageNum; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public String getMessageHash() { return messageHash; }
}