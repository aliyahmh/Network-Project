// ClientGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String currentUser;
    
    // GUI Components
    private JTabbedPane tabbedPane;
    private JPanel loginPanel, registerPanel, reservationPanel, accountPanel;
    private JTextArea messageArea;
    
    // Login components
    private JTextField loginUsername, registerUsername;
    private JPasswordField loginPassword, registerPassword;
    
    // Reservation components
    private JComboBox<String> roomTypeCombo, dayCombo;
    private JList<String> availableSlotsList;
    private DefaultListModel<String> slotsModel;
    
    // Account components
    private JList<String> myReservationsList;
    private DefaultListModel<String> reservationsModel;
    
    public ClientGUI() {
        initializeGUI();
        connectToServer();
    }
    
    private void initializeGUI() {
        setTitle("Online Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        createLoginPanel();
        createRegisterPanel();
        createReservationPanel();
        createAccountPanel();
        
        // Message area
        messageArea = new JTextArea(5, 50);
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        
        // Layout
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(messageScrollPane, BorderLayout.SOUTH);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        loginUsername = new JTextField(15);
        loginPanel.add(loginUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        loginPassword = new JPasswordField(15);
        loginPanel.add(loginPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Connect & Login");
        loginButton.addActionListener(e -> loginUser());
        loginPanel.add(loginButton, gbc);
        
        tabbedPane.addTab("Login", loginPanel);
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        registerUsername = new JTextField(15);
        registerPanel.add(registerUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        registerPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        registerPassword = new JPasswordField(15);
        registerPanel.add(registerPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton registerButton = new JButton("Connect & Register");
        registerButton.addActionListener(e -> registerUser());
        registerPanel.add(registerButton, gbc);
        
        tabbedPane.addTab("Register", registerPanel);
    }
    
    private void createReservationPanel() {
        reservationPanel = new JPanel(new BorderLayout(10, 10));
        
        // Top panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        inputPanel.add(new JLabel("Room Type:"));
        String[] roomTypes = {"Lab", "Meeting Room", "Regular Room", "Research Room", "Conference Room"};
        roomTypeCombo = new JComboBox<>(roomTypes);
        inputPanel.add(roomTypeCombo);
        
        inputPanel.add(new JLabel("Day:"));
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        dayCombo = new JComboBox<>(days);
        inputPanel.add(dayCombo);
        
        reservationPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Center panel for available slots
        JPanel slotsPanel = new JPanel(new BorderLayout());
        slotsPanel.add(new JLabel("Available Time Slots:"), BorderLayout.NORTH);
        
        slotsModel = new DefaultListModel<>();
        availableSlotsList = new JList<>(slotsModel);
        slotsPanel.add(new JScrollPane(availableSlotsList), BorderLayout.CENTER);
        
        reservationPanel.add(slotsPanel, BorderLayout.CENTER);
        
        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton showSlotsButton = new JButton("Show Available Times");
        showSlotsButton.addActionListener(e -> showAvailableSlots());
        buttonPanel.add(showSlotsButton);
        
        JButton reserveButton = new JButton("Reserve Selected Slot");
        reserveButton.addActionListener(e -> makeReservation());
        buttonPanel.add(reserveButton);
        
        reservationPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Make Reservation", reservationPanel);
    }
    
    private void createAccountPanel() {
        accountPanel = new JPanel(new BorderLayout());
        
        accountPanel.add(new JLabel("My Reservations:"), BorderLayout.NORTH);
        
        reservationsModel = new DefaultListModel<>();
        myReservationsList = new JList<>(reservationsModel);
        accountPanel.add(new JScrollPane(myReservationsList), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Refresh My Reservations");
        refreshButton.addActionListener(e -> refreshReservations());
        buttonPanel.add(refreshButton);
        
        JButton cancelButton = new JButton("Cancel Selected Reservation");
        cancelButton.addActionListener(e -> cancelReservation());
        buttonPanel.add(cancelButton);
        
        accountPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Account", accountPanel);
    }
    
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Start thread to listen for server messages
            new Thread(this::listenToServer).start();
            
            addMessage("Connected to server successfully!");
        } catch (IOException e) {
            addMessage("ERROR: Could not connect to server: " + e.getMessage());
        }
    }
    
    private void listenToServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                processServerResponse(response);
            }
        } catch (IOException e) {
            addMessage("Disconnected from server");
        }
    }
    
    private void processServerResponse(String response) {
        String[] parts = response.split(":", 2);
        if (parts.length < 2) return;
        
        String command = parts[0];
        String data = parts[1];
        
        SwingUtilities.invokeLater(() -> {
            switch (command) {
                case "REGISTER_RESPONSE":
                    addMessage("Registration: " + data);
                    if (data.startsWith("SUCCESS")) {
                        registerUsername.setText("");
                        registerPassword.setText("");
                    }
                    break;
                    
                case "LOGIN_RESPONSE":
                    addMessage("Login: " + data);
                    if (data.startsWith("SUCCESS")) {
                        currentUser = loginUsername.getText();
                        loginUsername.setText("");
                        loginPassword.setText("");
                        addMessage("Welcome, " + currentUser + "!");
                    }
                    break;
                    
                case "AVAILABLE_RESPONSE":
                    if (data.startsWith("ERROR")) {
                        addMessage("Availability: " + data);
                        slotsModel.clear();
                    } else {
                        slotsModel.clear();
                        String[] slots = data.split("\\|");
                        for (String slot : slots) {
                            if (!slot.isEmpty()) {
                                slotsModel.addElement(slot);
                            }
                        }
                        addMessage("Loaded " + slotsModel.size() + " available slots");
                    }
                    break;
                    
                case "RESERVATION_RESPONSE":
                    addMessage("Reservation: " + data);
                    if (data.startsWith("SUCCESS")) {
                        slotsModel.clear();
                    }
                    break;
                    
                case "MY_RESERVATIONS_RESPONSE":
                    if (data.startsWith("ERROR")) {
                        addMessage("My Reservations: " + data);
                        reservationsModel.clear();
                    } else {
                        reservationsModel.clear();
                        String[] reservations = data.split("\\|");
                        for (String reservation : reservations) {
                            if (!reservation.isEmpty()) {
                                String[] details = reservation.split(":");
                                if (details.length >= 4) {
                                    reservationsModel.addElement(details[0] + " - " + 
                                                               details[1] + " on " + 
                                                               details[2] + " at " + details[3]);
                                }
                            }
                        }
                        addMessage("Loaded " + reservationsModel.size() + " reservations");
                    }
                    break;
                    
                case "CANCEL_RESPONSE":
                    addMessage("Cancellation: " + data);
                    if (data.startsWith("SUCCESS")) {
                        refreshReservations();
                    }
                    break;
            }
        });
    }
    
    private void registerUser() {
        String username = registerUsername.getText();
        String password = new String(registerPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            addMessage("ERROR: Please enter both username and password");
            return;
        }
        
        out.println("REGISTER:" + username + "," + password);
    }
    
    private void loginUser() {
        String username = loginUsername.getText();
        String password = new String(loginPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            addMessage("ERROR: Please enter both username and password");
            return;
        }
        
        out.println("LOGIN:" + username + "," + password);
    }
    
    private void showAvailableSlots() {
        String roomType = (String) roomTypeCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        
        out.println("GET_AVAILABLE:" + roomType + "," + day);
    }
    
    private void makeReservation() {
        String selectedSlot = availableSlotsList.getSelectedValue();
        if (selectedSlot == null) {
            addMessage("ERROR: Please select a time slot first");
            return;
        }
        
        out.println("MAKE_RESERVATION:" + selectedSlot);
    }
    
    private void refreshReservations() {
        if (currentUser != null) {
            out.println("GET_MY_RESERVATIONS:");
        } else {
            addMessage("ERROR: Please login first");
        }
    }
    
    private void cancelReservation() {
        String selectedReservation = myReservationsList.getSelectedValue();
        if (selectedReservation == null) {
            addMessage("ERROR: Please select a reservation to cancel");
            return;
        }
        
        // Extract reservation ID (first part before space)
        String reservationId = selectedReservation.split(" ")[0];
        out.println("CANCEL_RESERVATION:" + reservationId);
    }
    
    private void addMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
}