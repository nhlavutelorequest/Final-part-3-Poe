
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author RC_Student_lab
 */
 

/**
 * Message Management System - Part 3(testing the program using JUnit)
 * Handles storing, retrieving, and managing messages with various operations
 */
public class MessageManagementSystem {
    
    // Arrays to store different types of messages and data
    private ArrayList<Message> sentMessages;
    private ArrayList<Message> disregardedMessages;
    private ArrayList<Message> storedMessages;
    private ArrayList<String> messageHashes;
    private ArrayList<String> messageIDs;
    
    // JSON file for persistent storage
    private static final String STORED_MESSAGES_FILE = "stored_messages.json";
    private ObjectMapper objectMapper;

    private void loadStoredMessagesFromFile() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Message class to represent individual messages
     */
    public static class Message {
        private String recipient;
        private String messageText;
        private String flag;                            
        
        private String messageID;
        private String messageHash;
        
        // Constructors
        public Message() {}
        
        public Message(String recipient, String messageText, String flag) {
            this.recipient = recipient;
            this.messageText = messageText;
            this.flag = flag;
            this.messageID = generateMessageID();
            this.messageHash = generateMessageHash();
        }
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public String getMessageText() { return messageText; }
        public void setMessageText(String messageText) { this.messageText = messageText; }
        
        public String getFlag() { return flag; }
        public void setFlag(String flag) { this.flag = flag; }
        
        public String getMessageID() { return messageID; }
        public void setMessageID(String messageID) { this.messageID = messageID; }
        
        public String getMessageHash() { return messageHash; }
        public void setMessageHash(String messageHash) { this.messageHash = messageHash; }
        
        // Generate unique message ID
        private String generateMessageID() {
            return String.valueOf(System.currentTimeMillis() + (int)(Math.random() * 1000));
        }
        
        // Generate message hash using SHA-256
        private String generateMessageHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String data = recipient + messageText + System.currentTimeMillis();
                byte[] hash = digest.digest(data.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString().substring(0, 16); // Return first 16 characters
            } catch (NoSuchAlgorithmException e) {
                return String.valueOf(System.currentTimeMillis());
            }
        }
        
        @Override
        public String toString() {
            return String.format("ID: %s | To: %s | Message: %s | Flag: %s | Hash: %s", 
                    messageID, recipient, messageText, flag, messageHash);
        }
    }
    
    /**
     * Constructor - Initialize all arrays and JSON mapper
     */
    public MessageManagementSystem() {
        sentMessages = new ArrayList<>();
        disregardedMessages = new ArrayList<>();
        storedMessages = new ArrayList<>();
        messageHashes = new ArrayList<>();
        messageIDs = new ArrayList<>();
        objectMapper = new ObjectMapper();
        
        // Load stored messages from JSON file
        loadStoredMessagesFromFile();
        
        // Populate with test data
        populateTestData();
    }
    
    /**
     * Populate arrays with test data from the requirements
     */
    private void populateTestData() {
        // Test Data Message 1
        Message msg1 = new Message("+27834557896", "Did you get the cake?", "Sent");
        addMessage(msg1);
        
        // Test Data Message 2
        Message msg2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
        addMessage(msg2);
        
        // Test Data Message 3
        Message msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        addMessage(msg3);
        
        // Test Data Message 4 
        Message msg4 = new Message("0838884567", "It is dinner time !", "Sent");
        addMessage(msg4);
        
        // Test Data Message 5
        Message msg5 = new Message("+27838884567", "Ok, I am leaving without you.", "Stored");
        addMessage(msg5);
    }
    
    /**
     * Add message to appropriate array based on flag
     */
    public void addMessage(Message message) {
        switch (message.getFlag().toLowerCase()) {
            case "sent":
                sentMessages.add(message);
                break;
            case "stored":
                storedMessages.add(message);
                saveStoredMessagesToFile();
                break;
            case "disregard":
                disregardedMessages.add(message);
                break;
        }
        
        // Add to hash and ID arrays
        messageHashes.add(message.getMessageHash());
        messageIDs.add(message.getMessageID());
    }
    
    /**
     * Display sender and recipient of all sent messages
     */
    public void displaySentMessages() {
        System.out.println("\n=== SENT MESSAGES ===");
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages found.");
            return;
        }
        
        for (Message msg : sentMessages) {
            System.out.printf("Sender: System | Recipient: %s | Message: %s%n", 
                    msg.getRecipient(), msg.getMessageText());
        }
    }
    
    /**
     * Display the longest sent message
     */
    public String displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            return "No sent messages available.";
        }
        
        Message longestMessage = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.getMessageText().length() > longestMessage.getMessageText().length()) {
                longestMessage = msg;
            }
        }
        
        String result = String.format("Longest Message: %s (Length: %d characters)", 
                longestMessage.getMessageText(), longestMessage.getMessageText().length());
        System.out.println("\n=== LONGEST MESSAGE ===");
        System.out.println(result);
        return longestMessage.getMessageText();
    }
    
    /**
     * Search for a message ID and display corresponding recipient and message
     */
    public void searchByMessageID(String messageID) {
        System.out.println("\n=== SEARCH BY MESSAGE ID ===");
        System.out.println("Searching for Message ID: " + messageID);
        
        // Search in all message arrays
        ArrayList<ArrayList<Message>> allArrays = new ArrayList<>();
        allArrays.add(sentMessages);
        allArrays.add(storedMessages);
        allArrays.add(disregardedMessages);
        
        for (ArrayList<Message> messageArray : allArrays) {
            for (Message msg : messageArray) {
                if (msg.getMessageID().equals(messageID)) {
                    System.out.printf("Found! Recipient: %s | Message: %s | Flag: %s%n", 
                            msg.getRecipient(), msg.getMessageText(), msg.getFlag());
                    return;
                }
            }
        }
        
        System.out.println("Message ID not found.");
    }
    
    /**
     * Search for all messages sent to a particular recipient
     */
    public void searchMessagesByRecipient(String recipient) {
        System.out.println("\n=== MESSAGES TO RECIPIENT: " + recipient + " ===");
        boolean found = false;
        
        ArrayList<ArrayList<Message>> allArrays = new ArrayList<>();
        allArrays.add(sentMessages);
        allArrays.add(storedMessages);
        allArrays.add(disregardedMessages);
        
        for (ArrayList<Message> messageArray : allArrays) {
            for (Message msg : messageArray) {
                if (msg.getRecipient().equals(recipient)) {
                    System.out.println(msg.toString());
                    found = true;
                }
            }
        }
        
        if (!found) {
            System.out.println("No messages found for recipient: " + recipient);
        }
    }
    
    /**
     * Delete a message using the message hash
     */
    public boolean deleteMessageByHash(String messageHash) {
        System.out.println("\n=== DELETE MESSAGE BY HASH ===");
        System.out.println("Attempting to delete message with hash: " + messageHash);
        
        // Search and remove from sent messages
        if (removeMessageFromArray(sentMessages, messageHash)) {
            messageHashes.remove(messageHash);
            System.out.println("Message deleted from sent messages.");
            return true;
        }
        
        // Search and remove from stored messages
        if (removeMessageFromArray(storedMessages, messageHash)) {
            messageHashes.remove(messageHash);
            saveStoredMessagesToFile();
            System.out.println("Message deleted from stored messages.");
            return true;
        }
        
        // Search and remove from disregarded messages
        if (removeMessageFromArray(disregardedMessages, messageHash)) {
            messageHashes.remove(messageHash);
            System.out.println("Message deleted from disregarded messages.");
            return true;
        }
        
        System.out.println("Message hash not found.");
        return false;
    }
    
    /**
     * Helper method to remove message from array by hash
     */
    private boolean removeMessageFromArray(ArrayList<Message> messageArray, String messageHash) {
        Iterator<Message> iterator = messageArray.iterator();
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            if (msg.getMessageHash().equals(messageHash)) {
                iterator.remove();
                // Also remove from messageIDs array
                messageIDs.remove(msg.getMessageID());
                return true;
            }
        }
        return false;
    }
    
    /**
     *report that lists the full details of all sent messages
     */
    public void displayFullSentMessagesReport() {
        System.out.println("\n=== FULL SENT MESSAGES REPORT ===");
        System.out.println("Total Sent Messages: " + sentMessages.size());
        System.out.println("=" + "=".repeat(80));
        
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages to display.");
            return;
        }
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            System.out.printf("Message #%d:%n", (i + 1));
            System.out.printf("  Message ID: %s%n", msg.getMessageID());
            System.out.printf("  Recipient: %s%n", msg.getRecipient());
            System.out.printf("  Message Text: %s%n", msg.getMessageText());
            System.out.printf("  Flag: %s%n", msg.getFlag());
            System.out.printf("  Message Hash: %s%n", msg.getMessageHash());
            System.out.printf("  Message Length: %d characters%n", msg.getMessageText().length());
            System.out.println("  " + "-".repeat(60));
        }
    }
    
    /**
     * Save stored messages to JSON file
     */
    private void saveStoredMessagesToFile() {
        objectMapper.writeValue(new File(STORED_MESSAGES_FILE), storedMessages);
    }
    
    /**
     * Load stored messages from JSON file
     */
   public class MessageLoader {
    private HashSet<String> messageIDs = new HashSet<>();
    private ObjectMapper objectMapper = new ObjectMapper();


    
    static class Message {
        private String messageHash;
        private String messageID;

        public String getMessageHash() {
            return messageHash;
        }

        public String getMessageID() {
            return messageID;
        }
    }
    }
    
    /**
     * Display all arrays status
     */
    public void displayArraysStatus() {
        System.out.println("\n=== ARRAYS STATUS ===");
        System.out.println("Sent Messages: " + sentMessages.size());
        System.out.println("Stored Messages: " + storedMessages.size());
        System.out.println("Disregarded Messages: " + disregardedMessages.size());
        System.out.println("Total Message Hashes: " + messageHashes.size());
        System.out.println("Total Message IDs: " + messageIDs.size());
    }
    
    /**
     * Unit Tests
     */
    public void runUnitTests() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RUNNING UNIT TESTS");
        System.out.println("=".repeat(50));
        
        testSentMessagesArrayPopulation();
        testLongestMessageDisplay();
        testMessageIDSearch();
        testMessagesByRecipientSearch();
    }
    
    private void testSentMessagesArrayPopulation() {
        System.out.println("\nTEST: Sent Messages Array Correctly Populated");
        System.out.println("Expected: Messages array contains the expected test data");
        
        boolean test1Passed = false;
        boolean test4Passed = false;
        
        for (Message msg : sentMessages) {
            if (msg.getMessageText().equals("Did you get the cake?")) {
                test1Passed = true;
            }
            if (msg.getMessageText().equals("It is dinner time !")) {
                test4Passed = true;
            }
        }
        
        if (test1Passed && test4Passed) {
            System.out.println("✓ PASS: Sent messages array correctly populated with test data");
        } else {
            System.out.println("✗ FAIL: Sent messages array not correctly populated");
        }
    }
    
    private void testLongestMessageDisplay() {
        System.out.println("\nTEST: Display the Longest Message");
        System.out.println("Expected: 'Where are you? You are late! I have asked you to be on time.'");
        
        String longestMessage = displayLongestMessage();
        if (longestMessage.equals("Where are you? You are late! I have asked you to be on time.")) {
            System.out.println("✓ PASS: Correct longest message identified");
        } else {
            System.out.println("✗ FAIL: Incorrect longest message identified");
            System.out.println("Actual: " + longestMessage);
        }
    }
    
    private void testMessageIDSearch() {
        System.out.println("\nTEST: Search for Message ID");
        System.out.println("Expected: '0838884567' returns 'It is dinner time!'");
        
        // Find a sent message with developer number
        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals("0838884567")) {
                searchByMessageID(msg.getMessageID());
                if (msg.getMessageText().equals("It is dinner time !")) {
                    System.out.println("✓ PASS: Message ID search working correctly");
                } else {
                    System.out.println("✗ FAIL: Message ID search returned wrong message");
                }
                return;
            }
        }
        System.out.println("✗ FAIL: Test message not found");
    }
    
    private void testMessagesByRecipientSearch() {
        System.out.println("\nTEST: Search All Messages Sent or Stored");
        System.out.println("Expected: Multiple messages for recipient +27838884567");
        
        int messageCount = 0;
        String testRecipient = "+27838884567";
        
        ArrayList<ArrayList<Message>> allArrays = new ArrayList<>();
        allArrays.add(sentMessages);
        allArrays.add(storedMessages);
        allArrays.add(disregardedMessages);
        
        for (ArrayList<Message> messageArray : allArrays) {
            for (Message msg : messageArray) {
                if (msg.getRecipient().equals(testRecipient)) {
                    messageCount++;
                }
            }
        }
        
        if (messageCount >= 2) {
            System.out.println("✓ PASS: Multiple messages found for recipient");
        } else {
            System.out.println("✗ FAIL: Insufficient messages found for recipient");
        }
        
        searchMessagesByRecipient(testRecipient);
    }
    
    /**
     * Interactive menu system
     */
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("MESSAGE MANAGEMENT SYSTEM - PART 3");
            System.out.println("=".repeat(50));
            System.out.println("1. Display all sent messages");
            System.out.println("2. Display longest message");
            System.out.println("3. Search by message ID");
            System.out.println("4. Search messages by recipient");
            System.out.println("5. Delete message by hash");
            System.out.println("6. Display full sent messages report");
            System.out.println("7. Display arrays status");
            System.out.println("8. Add new message");
            System.out.println("9. Run unit tests");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    displaySentMessages();
                    break;
                case 2:
                    displayLongestMessage();
                    break;
                case 3:
                    System.out.print("Enter message ID to search: ");
                    String messageID = scanner.nextLine();
                    searchByMessageID(messageID);
                    break;
                case 4:
                    System.out.print("Enter recipient to search: ");
                    String recipient = scanner.nextLine();
                    searchMessagesByRecipient(recipient);
                    break;
                case 5:
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine();
                    deleteMessageByHash(hash);
                    break;
                case 6:
                    displayFullSentMessagesReport();
                    break;
                case 7:
                    displayArraysStatus();
                    break;
                case 8:
                    addNewMessageInteractive(scanner);
                    break;
                case 9:
                    runUnitTests();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
        
        scanner.close();
    }
    
    /**
     * Interactive method to add new message
     */
    private void addNewMessageInteractive(Scanner scanner) {
        System.out.println("\n=== ADD NEW MESSAGE ===");
        System.out.print("Enter recipient: ");
        String recipient = scanner.nextLine();
        
        System.out.print("Enter message text: ");
        String messageText = scanner.nextLine();
        
        System.out.print("Enter flag (Sent/Stored/Disregard): ");
        String flag = scanner.nextLine();
        
        Message newMessage = new Message(recipient, messageText, flag);
        addMessage(newMessage);
        
        System.out.println("Message added successfully!");
        System.out.println("Generated ID: " + newMessage.getMessageID());
        System.out.println("Generated Hash: " + newMessage.getMessageHash());
    }
    
    /**
     * Main method to run the application
     */
    public static void main(String[] args) {
        System.out.println("Starting Message Management System - Part 3");
        MessageManagementSystem system = new MessageManagementSystem();
        
        // Run unit tests automatically
        system.runUnitTests();
        
        // Show interactive menu
        system.showMenu();
    }
}
