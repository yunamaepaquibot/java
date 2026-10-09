package StudentPortal;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/*
 * ================================================================
 * ELEGANT STUDENT PORTAL - MIDTERM EXAM IMPLEMENTATION
 * ================================================================
 * Questions 1-25 are identified directly beside the code that
 * satisfies each requirement.
 *
 * File format (users.txt), one account per line:
 * username|passwordHash|studentNo|firstName|lastName|middleName|
 * email|course|yearLevel|major|contactNo|birthdate|gender|address|
 * dateRegistered
 *
 * Profile pictures are stored in the "profile_pictures" folder.
 *
 * Run:
 * javac -d . LoginApp.java
 * java StudentsPortal.LoginApp
 * ================================================================
 */

public class LoginApp extends JFrame {

    // Q3 - Primary color changed to dark green.
    private static final Color GREEN = new Color(22, 101, 52);
    private static final Color GREEN_DARK = new Color(15, 75, 38);
    private static final Color GREEN_HOVER = new Color(18, 83, 43);

    // Q4 - Original assignment background: RGB 235,238,242.
    private static final Color BACKGROUND = new Color(235, 238, 242);
    private static final Color PINK = new Color(252, 239, 246);
    private static final Color PINK_LIGHT = new Color(255, 248, 252);
    private static final Color PINK_DARK = new Color(225, 178, 202);
    private static final Color BORDER = new Color(224, 204, 215);

    private static final Color TEXT = new Color(55, 55, 55);
    private static final Color SECONDARY = new Color(110, 100, 108);
    private static final Color WHITE = Color.WHITE;

    // Q19 - Main application font changed to Segoe UI.
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    private static final String USER_FILE = "users.txt";
    private static final String PHOTO_DIR = "profile_pictures";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    // ----------------------------------------------------------------
    // Drop-down data (courses and majors from many different fields)
    // ----------------------------------------------------------------
    private static final String[] COURSES = {
            "BS Computer Science",
            "BS Information Technology",
            "BS Information Systems",
            "BS Computer Engineering",
            "BS Civil Engineering",
            "BS Mechanical Engineering",
            "BS Electrical Engineering",
            "BS Electronics Engineering",
            "BS Industrial Engineering",
            "BS Chemical Engineering",
            "BS Architecture",
            "BS Nursing",
            "BS Medical Technology",
            "BS Pharmacy",
            "BS Physical Therapy",
            "BS Radiologic Technology",
            "BS Nutrition and Dietetics",
            "BS Psychology",
            "BS Biology",
            "BS Chemistry",
            "BS Mathematics",
            "BS Statistics",
            "BS Environmental Science",
            "BS Accountancy",
            "BS Business Administration",
            "BS Entrepreneurship",
            "BS Economics",
            "BS Hospitality Management",
            "BS Tourism Management",
            "BS Office Administration",
            "BA Communication",
            "BA Journalism",
            "BA Political Science",
            "BA English Language",
            "BS Social Work",
            "BS Criminology",
            "Bachelor of Elementary Education",
            "Bachelor of Secondary Education",
            "Bachelor of Physical Education",
            "Bachelor of Early Childhood Ed.",
            "Bachelor of Fine Arts",
            "Bachelor of Multimedia Arts",
            "Bachelor of Music",
            "BS Interior Design",
            "BS Agriculture",
            "BS Forestry",
            "BS Fisheries",
            "BS Marine Biology",
            "BS Marine Transportation",
            "BS Aviation",
            "Other Course"
    };

    private static final String[] MAJORS = {
            "Software Engineering",
            "Web Development",
            "Data Science",
            "Artificial Intelligence",
            "Networking",
            "Cybersecurity",
            "Game Development",
            "Marketing Management",
            "Financial Management",
            "Human Resource Management",
            "Operations Management",
            "Entrepreneurship",
            "Banking and Finance",
            "Accounting",
            "Structural Engineering",
            "Construction Management",
            "Environmental Engineering",
            "Power Systems",
            "Mechatronics",
            "Clinical Nursing",
            "Community Health",
            "Clinical Psychology",
            "Industrial Psychology",
            "Biology",
            "Chemistry",
            "Mathematics",
            "English",
            "Filipino",
            "General Science",
            "Social Studies",
            "Special Education",
            "Journalism",
            "Broadcasting",
            "Public Relations",
            "Digital Media",
            "Hotel and Restaurant",
            "Culinary Arts",
            "Travel and Tours",
            "Animation",
            "Graphic Design",
            "Music Performance",
            "Interior Design",
            "Agribusiness",
            "Crop Science",
            "Forestry",
            "Marine Engineering",
            "General Studies",
            "Not Applicable",
            "Undeclared"
    };

    private static final String[] YEAR_LEVELS = {
            "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
    };

    private static final String[] GENDERS = {
            "Male", "Female", "Non-binary", "Prefer not to say"
    };

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-() ]{7,20}$");

    private boolean profileEditing;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private char passwordEchoChar;

    private JPanel mainContent;
    private final List<JButton> navigationButtons = new ArrayList<>();

    private Student currentStudent;

    public LoginApp() {
        // Q1 - Window title.
        setTitle("Student Login System");

        // Q2 - Required window size: 1000 x 600.
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        showLoginPage();
    }

    // ================================================================
    // STUDENT MODEL
    // ================================================================
    private static class Student {
        String username = "";
        String studentNo = "";
        String firstName = "";
        String lastName = "";
        String middleName = "";
        String email = "";
        String course = "";
        String yearLevel = "";
        String major = "";
        String contactNo = "";
        String birthdate = "";
        String gender = "";
        String address = "";
        String registered = "";
        BufferedImage photo;
    }

    // ================================================================
    // SHARED LEFT BRAND PANEL (login + registration windows)
    // ================================================================
    private JPanel createLeftPanel(String headingText, String bodyText) {
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Futuristic touch: thin concentric orbit rings.
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setStroke(new BasicStroke(1f));

                int w = getWidth();
                int h = getHeight();

                for (int i = 0; i < 4; i++) {
                    int r = 110 + i * 55;
                    g2.setColor(new Color(255, 255, 255, 24 - i * 4));
                    g2.drawOval(-r, h - r, r * 2, r * 2);

                    g2.setColor(new Color(246, 219, 233, 26 - i * 5));
                    g2.drawOval(w - r, -r, r * 2, r * 2);
                }

                g2.dispose();
            }
        };
        leftPanel.setBackground(GREEN);
        leftPanel.setPreferredSize(new Dimension(390, 600));
        leftPanel.setBorder(new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y,
                                    int width, int height) {
                g.setColor(PINK_DARK);
                g.fillRect(width - 5, 0, 5, height);
            }
        });

        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.weightx = 1;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.insets = new Insets(6, 45, 6, 45);

        JPanel logoPanel = new StudentLogo(96);
        left.gridy = 0;
        left.insets = new Insets(0, 45, 18, 45);
        leftPanel.add(logoPanel, left);

        JLabel welcome = new JLabel(headingText, SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 25));
        welcome.setForeground(WHITE);
        left.gridy = 1;
        left.insets = new Insets(0, 40, 4, 40);
        leftPanel.add(welcome, left);

        JLabel portal = new JLabel("Student Portal", SwingConstants.CENTER);
        portal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        portal.setForeground(new Color(246, 219, 233));
        left.gridy = 2;
        left.insets = new Insets(0, 40, 14, 40);
        leftPanel.add(portal, left);

        JTextArea description = new JTextArea(bodyText);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(232, 244, 235));
        description.setBackground(GREEN);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setBorder(null);

        left.gridy = 3;
        left.insets = new Insets(0, 58, 0, 58);
        leftPanel.add(description, left);

        return leftPanel;
    }

    // ================================================================
    // Q25 - FINAL LOGIN SCREEN / PROFESSIONAL STUDENT LOGIN SYSTEM
    // ================================================================
    private void showLoginPage() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        // LEFT SIDE: GREEN + PASTEL PINK ACCENTS
        JPanel leftPanel = createLeftPanel(
                "WELCOME BACK",
                "A clean academic space where students can "
                        + "access subjects, grades, schedules, attendance, "
                        + "and important announcements in one place."
        );

        // RIGHT LOGIN PANEL
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(PINK);

        GridBagConstraints form = new GridBagConstraints();
        form.gridx = 0;
        form.weightx = 1;
        form.fill = GridBagConstraints.HORIZONTAL;
        form.insets = new Insets(5, 65, 5, 65);

        // Q5 - Heading changed to Student Portal Login.
        JLabel title = new JLabel("Student Portal Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(GREEN);

        form.gridy = 0;
        form.insets = new Insets(0, 65, 3, 65);
        rightPanel.add(title, form);

        JLabel subtitle = new JLabel("Sign in to continue to your dashboard.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        form.gridy = 1;
        form.insets = new Insets(0, 65, 18, 65);
        rightPanel.add(subtitle, form);

        JLabel userLabel = label("Username");
        form.gridy = 2;
        form.insets = new Insets(3, 65, 4, 65);
        rightPanel.add(userLabel, form);

        usernameField = new RoundTextField();
        styleField(usernameField);
        // Q6 - The username field is explicitly added to the panel.
        form.gridy = 3;
        form.insets = new Insets(0, 65, 9, 65);
        rightPanel.add(usernameField, form);

        JLabel passLabel = label("Password");
        form.gridy = 4;
        form.insets = new Insets(3, 65, 4, 65);
        rightPanel.add(passLabel, form);

        // Q7 - JPasswordField masks the password from startup.
        passwordField = new RoundPasswordField();
        styleField(passwordField);
        passwordEchoChar = passwordField.getEchoChar();

        // Q16 - Eye button beside the password field.
        JButton eyeButton = createEyeButton();
        JPanel passwordRow = new JPanel(new BorderLayout(8, 0));
        passwordRow.setOpaque(false);
        passwordRow.add(passwordField, BorderLayout.CENTER);
        passwordRow.add(eyeButton, BorderLayout.EAST);

        form.gridy = 5;
        form.insets = new Insets(0, 65, 5, 65);
        rightPanel.add(passwordRow, form);

        // Q8 - Show Password checkbox works both ways.
        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setForeground(SECONDARY);
        showPasswordCheckBox.setBackground(PINK);
        showPasswordCheckBox.addActionListener(e -> togglePassword());
        form.gridy = 6;
        form.insets = new Insets(0, 65, 9, 65);
        rightPanel.add(showPasswordCheckBox, form);

        // Q17 + Q18 - Login icon, hover effect, and hand cursor.
        JButton loginButton = createGreenButton("->  SIGN IN");
        loginButton.addActionListener(e -> login());

        form.gridy = 7;
        form.insets = new Insets(3, 65, 7, 65);
        rightPanel.add(loginButton, form);

        JButton clearButton = createOutlineButton("CLEAR");
        clearButton.addActionListener(e -> clearFields());

        form.gridy = 8;
        form.insets = new Insets(0, 65, 9, 65);
        rightPanel.add(clearButton, form);

        // Q21 - Registration event listener opens registration window.
        JLabel register = new JLabel(
                "<html><u>Create Account</u></html>",
                SwingConstants.CENTER
        );
        register.setFont(new Font("Segoe UI", Font.BOLD, 13));
        register.setForeground(GREEN);
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                register();
            }
        });

        form.gridy = 9;
        form.insets = new Insets(3, 65, 0, 65);
        rightPanel.add(register, form);

        passwordField.addActionListener(e -> login());

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    // ================================================================
    // Q7, Q8, Q16 - PASSWORD MASKING / CHECKBOX / EYE BUTTON
    // ================================================================
    private void togglePassword() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar(passwordEchoChar);
        }
    }

    private JButton createEyeButton() {
        JButton eye = new JButton("◉");
        eye.setToolTipText("Show / hide password");
        eye.setFont(new Font("Segoe UI Symbol", Font.BOLD, 17));
        eye.setForeground(GREEN);
        eye.setBackground(PINK_LIGHT);
        eye.setFocusPainted(false);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.setBorder(BorderFactory.createLineBorder(PINK_DARK, 1));
        eye.setPreferredSize(new Dimension(48, 43));

        eye.addActionListener(e -> {
            boolean visible = passwordField.getEchoChar() == 0;
            passwordField.setEchoChar(visible ? passwordEchoChar : (char) 0);
            showPasswordCheckBox.setSelected(!visible);
        });

        return eye;
    }

    // ================================================================
    // Q11-Q15 - LOGIN FUNCTIONALITY DEBUGGING
    // ================================================================
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Q11 - Empty username/password validation.
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            usernameField.requestFocusInWindow();
            return;
        }

        File file = new File(USER_FILE);

        // Q24 - users.txt may not exist on first launch.
        if (!file.exists()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed. Please create an account first.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String passwordHash = hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Q23 - Correct delimiter: username|passwordHash|...
                // | is a regex operator, so \\| means a literal |.
                // The -1 limit keeps empty trailing fields.
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 2) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];

                    // Q14 - Use .equals() for String values, not ==.
                    // Q15 - Username is case-insensitive; password remains
                    // case-sensitive because the password hash is compared
                    // exactly.
                    if (storedUsername.equalsIgnoreCase(username)
                            && storedPassword.equals(passwordHash)) {

                        // Q13 - Required success message before dashboard.
                        JOptionPane.showMessageDialog(
                                this,
                                "Welcome! You have successfully logged in.",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        openDashboard(toStudent(parts));
                        return;
                    }
                }
            }

            // Q12 - Required incorrect-login message.
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed. Please check your username and password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to read user information.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================================================================
    // Q9 + Q10 - CLEAR BUTTON
    // ================================================================
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");

        // Q10 - Return cursor to username after clearing.
        usernameField.requestFocusInWindow();
    }

    // ================================================================
    // Q21 + Q22 + Q24 - REGISTRATION
    // ================================================================
    private void register() {
        Student created = showRegistrationDialog();

        // The dashboard opens as soon as the account is created.
        if (created != null) {
            openDashboard(created);
        }
    }

    private String part(String[] parts, int index) {
        return index < parts.length ? parts[index] : "";
    }

    private Student toStudent(String[] parts) {
        Student s = new Student();
        s.username = parts[0];
        s.studentNo = part(parts, 2);
        s.firstName = part(parts, 3);
        s.lastName = part(parts, 4);
        s.middleName = part(parts, 5);
        s.email = part(parts, 6);
        s.course = part(parts, 7);
        s.yearLevel = part(parts, 8);
        s.major = part(parts, 9);
        s.contactNo = part(parts, 10);
        s.birthdate = part(parts, 11);
        s.gender = part(parts, 12);
        s.address = part(parts, 13);
        s.registered = part(parts, 14);
        s.photo = loadPhoto(s.username);
        return s;
    }

    // One account per line (see the file format at the top).
    private String toLine(Student s, String passwordHash) {
        return s.username + "|" + passwordHash
                + "|" + s.studentNo
                + "|" + s.firstName
                + "|" + s.lastName
                + "|" + s.middleName
                + "|" + s.email
                + "|" + s.course
                + "|" + s.yearLevel
                + "|" + s.major
                + "|" + s.contactNo
                + "|" + s.birthdate
                + "|" + s.gender
                + "|" + s.address
                + "|" + s.registered;
    }

    // Replaces (updated != null) or removes (updated == null) the account.
    private boolean updateUserFile(String username, Student updated) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        List<String> output = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|", -1);

                if (!found && parts[0].equalsIgnoreCase(username)) {
                    found = true;
                    if (updated != null) {
                        output.add(toLine(updated, part(parts, 1)));
                    }
                } else {
                    output.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (!found) return false;

        File temp = new File(USER_FILE + ".tmp");

        try (FileWriter writer = new FileWriter(temp)) {
            for (String line : output) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            return false;
        }

        try {
            Files.move(
                    temp.toPath(), file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private String formatDate(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "";

        try {
            return LocalDate.parse(iso.trim()).format(
                    DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
            );
        } catch (DateTimeParseException e) {
            return iso;
        }
    }

    private String displayName(Student s) {
        String first = s.firstName.trim();
        String middle = s.middleName.trim();
        String last = s.lastName.trim();

        if (first.isEmpty() && last.isEmpty()) return s.username;

        StringBuilder name = new StringBuilder(first);
        if (!middle.isEmpty()) {
            name.append(' ')
                    .append(Character.toUpperCase(middle.charAt(0)))
                    .append('.');
        }
        if (!last.isEmpty()) name.append(' ').append(last);

        return name.toString().trim();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void selectOrAdd(JComboBox<String> box, String value) {
        if (value == null || value.trim().isEmpty()) {
            box.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).equals(value)) {
                box.setSelectedIndex(i);
                return;
            }
        }

        box.addItem(value);
        box.setSelectedIndex(box.getItemCount() - 1);
    }

    private Student showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Create Student Account", true);

        // Same size as the login window.
        dialog.setSize(1000, 600);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        JPanel leftPanel = createLeftPanel(
                "CREATE ACCOUNT",
                "Fill out your academic details to set up your "
                        + "student profile and go straight to your dashboard."
        );

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(PINK);

        // ---- Form fields ----
        JTextField userF = formField();
        JPasswordField passF = new RoundPasswordField();
        decorateField(passF, 100, 40, 8);
        JTextField firstF = formField();
        JTextField middleF = formField();
        JTextField lastF = formField();
        JTextField studentNoF = formField();
        JTextField emailF = formField();

        JComboBox<String> courseBox = createCombo("Select course", COURSES);
        JComboBox<String> yearBox = createCombo("Select year", YEAR_LEVELS);
        JComboBox<String> majorBox = createCombo("Select major", MAJORS);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 0;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(GREEN);
        f.gridy = 0;
        f.insets = new Insets(0, 0, 3, 0);
        form.add(title, f);

        JLabel subtitle = new JLabel("All fields are required.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);
        f.gridy = 1;
        f.insets = new Insets(0, 0, 12, 0);
        form.add(subtitle, f);

        f.gridy = 2;
        f.insets = new Insets(0, 0, 11, 0);
        form.add(formRow(
                new String[]{"Username", "Password"},
                new JComponent[]{userF, passF}
        ), f);

        f.gridy = 3;
        form.add(formRow(
                new String[]{"First Name", "Middle Name", "Last Name"},
                new JComponent[]{firstF, middleF, lastF}
        ), f);

        f.gridy = 4;
        form.add(formRow(
                new String[]{"Student No.", "Email Address"},
                new JComponent[]{studentNoF, emailF}
        ), f);

        f.gridy = 5;
        form.add(formRow(
                new String[]{"Course"},
                new JComponent[]{courseBox}
        ), f);

        f.gridy = 6;
        form.add(formRow(
                new String[]{"Year Level", "Major"},
                new JComponent[]{yearBox, majorBox},
                new double[]{1, 2}
        ), f);

        JButton createButton = createGreenButton("CREATE ACCOUNT");
        createButton.setPreferredSize(new Dimension(100, 40));
        JButton cancelButton = createOutlineButton("CANCEL");
        cancelButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setOpaque(false);
        buttons.add(createButton);
        buttons.add(cancelButton);

        f.gridy = 7;
        f.insets = new Insets(3, 0, 0, 0);
        form.add(buttons, f);

        GridBagConstraints wrap = new GridBagConstraints();
        wrap.gridx = 0;
        wrap.gridy = 0;
        wrap.weightx = 1;
        wrap.fill = GridBagConstraints.HORIZONTAL;
        wrap.insets = new Insets(0, 45, 0, 45);
        rightPanel.add(form, wrap);

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);
        dialog.setContentPane(root);

        final Student[] result = new Student[1];

        cancelButton.addActionListener(e -> dialog.dispose());

        createButton.addActionListener(e -> {
            String username = userF.getText().trim();
            String password = new String(passF.getPassword());
            String first = firstF.getText().trim();
            String middle = middleF.getText().trim();
            String last = lastF.getText().trim();
            String studentNo = studentNoF.getText().trim();
            String email = emailF.getText().trim();

            if (username.isEmpty() || password.isEmpty()
                    || first.isEmpty() || middle.isEmpty() || last.isEmpty()
                    || studentNo.isEmpty() || email.isEmpty()) {
                registrationError(dialog, "Please fill out all fields.");
                return;
            }

            if (courseBox.getSelectedIndex() <= 0
                    || yearBox.getSelectedIndex() <= 0
                    || majorBox.getSelectedIndex() <= 0) {
                registrationError(dialog,
                        "Please select your course, year level, and major.");
                return;
            }

            String[] texts = {username, password, first, middle, last,
                    studentNo, email};
            for (String t : texts) {
                if (t.contains("|")) {
                    registrationError(dialog,
                            "Fields cannot contain the | character.");
                    return;
                }
            }

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                registrationError(dialog,
                        "Please enter a valid email address.");
                return;
            }

            // Q22 - Duplicate usernames are checked case-insensitively.
            if (userExists(username)) {
                registrationError(dialog, "Username already exists.");
                return;
            }

            if (studentNoExists(studentNo, "")) {
                registrationError(dialog,
                        "Student No. is already registered.");
                return;
            }

            Student s = new Student();
            s.username = username;
            s.studentNo = studentNo;
            s.firstName = first;
            s.middleName = middle;
            s.lastName = last;
            s.email = email;
            s.course = (String) courseBox.getSelectedItem();
            s.yearLevel = (String) yearBox.getSelectedItem();
            s.major = (String) majorBox.getSelectedItem();
            s.registered = LocalDate.now().toString();

            // Q24 - FileWriter(..., true) creates users.txt automatically
            // when the file does not exist.
            try (FileWriter writer = new FileWriter(USER_FILE, true)) {
                writer.write(
                        toLine(s, hashPassword(password))
                                + System.lineSeparator()
                );
            } catch (IOException ex) {
                registrationError(dialog, "Unable to create account.");
                return;
            }

            JOptionPane.showMessageDialog(
                    dialog,
                    "Account created successfully!",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

            result[0] = s;
            dialog.dispose();
        });

        SwingUtilities.invokeLater(() -> userF.requestFocusInWindow());
        dialog.setVisible(true);

        return result[0];
    }

    private void registrationError(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private JPanel formRow(String[] labels, JComponent[] fields) {
        double[] weights = new double[labels.length];
        for (int i = 0; i < weights.length; i++) weights[i] = 1;
        return formRow(labels, fields, weights);
    }

    // Builds a row of labeled inputs. Zero preferred width makes the
    // weights split the row exactly in proportion.
    private JPanel formRow(String[] labels, JComponent[] fields,
                           double[] weights) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < labels.length; i++) {
            JPanel column = new JPanel(new BorderLayout(0, 4));
            column.setOpaque(false);
            column.setPreferredSize(new Dimension(0, 62));
            column.add(label(labels[i]), BorderLayout.NORTH);
            column.add(fields[i], BorderLayout.CENTER);

            c.gridx = i;
            c.weightx = weights[i];
            c.insets = new Insets(0, i == 0 ? 0 : 12, 0, 0);
            row.add(column, c);
        }

        return row;
    }

    private JTextField formField() {
        JTextField field = new RoundTextField();
        decorateField(field, 100, 40, 8);
        return field;
    }

    private JComboBox<String> createCombo(String placeholder, String[] items) {
        String[] all = new String[items.length + 1];
        all[0] = placeholder;
        System.arraycopy(items, 0, all, 1, items.length);

        RoundCombo combo = new RoundCombo(all);
        combo.setUI(new FlatComboUI());
        combo.setOpaque(false);
        combo.setFont(FONT);
        combo.setBackground(WHITE);
        combo.setForeground(TEXT);
        combo.setRenderer(new ComboRenderer());
        combo.setBorder(new RoundedFieldBorder(PINK_DARK, 1, 12, 4, 10));
        combo.setMaximumRowCount(8);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return combo;
    }

    private boolean userExists(String username) {
        File file = new File(USER_FILE);

        // Q24 - No file means there are no users yet.
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Q23 - Read the same | format used during registration.
                String[] parts = line.split("\\|", -1);

                if (parts.length >= 1
                        && parts[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    private boolean studentNoExists(String studentNo, String excludeUser) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|", -1);

                if (part(parts, 2).equalsIgnoreCase(studentNo)
                        && !parts[0].equalsIgnoreCase(excludeUser)) {
                    return true;
                }
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    // ================================================================
    // PROFILE PICTURE STORAGE
    // ================================================================
    private File photoFile(String username) {
        String key = hashPassword(username.toLowerCase(Locale.ROOT))
                .substring(0, 20);
        return new File(PHOTO_DIR, key + ".png");
    }

    private BufferedImage loadPhoto(String username) {
        File file = photoFile(username);
        if (!file.exists()) return null;

        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            return null;
        }
    }

    // Crops the picture to a centered square, resizes it, and saves it.
    private BufferedImage savePhoto(String username, File chosen)
            throws IOException {
        BufferedImage source = ImageIO.read(chosen);

        if (source == null) {
            throw new IOException("The selected file is not a valid image.");
        }

        int side = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - side) / 2;
        int y = (source.getHeight() - side) / 2;
        BufferedImage square = source.getSubimage(x, y, side, side);

        BufferedImage output = new BufferedImage(
                300, 300, BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.drawImage(square, 0, 0, 300, 300, null);
        g2.dispose();

        File dir = new File(PHOTO_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create the pictures folder.");
        }

        ImageIO.write(output, "png", photoFile(username));
        return output;
    }

    // ================================================================
    // DASHBOARD - Q13 + Q25
    // ================================================================
    private void openDashboard(Student student) {
        currentStudent = student;
        profileEditing = false;
        String username = student.username;

        navigationButtons.clear();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        // Elegant green sidebar.
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(GREEN);
        sidebar.setPreferredSize(new Dimension(220, 600));

        JPanel brand = new JPanel();
        brand.setBackground(GREEN);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new javax.swing.border.EmptyBorder(22, 18, 18, 18));

        JPanel smallLogo = new StudentLogo(62);
        smallLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(smallLogo);
        brand.add(Box.createVerticalStrut(10));

        JLabel brandName = new JLabel("STUDENT PORTAL");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandName.setForeground(WHITE);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(brandName);

        JLabel brandSub = new JLabel("Academic Management");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandSub.setForeground(new Color(220, 238, 225));
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(brandSub);

        JPanel nav = new JPanel();
        nav.setBackground(GREEN);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new javax.swing.border.EmptyBorder(8, 14, 8, 14));

        String[] items = {
                "Dashboard",
                "My Profile",
                "Subjects",
                "Grades",
                "Schedule",
                "Announcements"
        };

        for (String item : items) {
            JButton button = createNavButton(item);
            navigationButtons.add(button);

            nav.add(button);
            nav.add(Box.createVerticalStrut(5));

            button.addActionListener(e -> {
                if (profileEditing) {
                    int choice = JOptionPane.showOptionDialog(
                            this,
                            "You have unsaved changes in your profile.\n"
                                    + "Discard them and leave this page?",
                            "Unsaved Changes",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new Object[]{"Discard", "Keep Editing"},
                            "Keep Editing"
                    );

                    if (choice != 0) return;
                    profileEditing = false;
                }

                setActiveNavigation(button);
                showDashboardPage(item, username);
            });
        }

        JButton logout = createNavButton("Logout");
        logout.addActionListener(e -> {
            String message = profileEditing
                    ? "You are about to log out.\n"
                    + "Your unsaved profile changes will be lost."
                    : "You are about to log out of your account.\n"
                    + "Are you sure you want to continue?";

            int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{"Log Out", "Stay Signed In"},
                    "Stay Signed In"
            );

            if (choice != 0) return;

            profileEditing = false;
            navigationButtons.clear();
            currentStudent = null;
            showLoginPage();
        });

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(GREEN);
        logoutPanel.setBorder(new javax.swing.border.EmptyBorder(8, 14, 18, 14));
        logoutPanel.add(logout);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND);
        mainContent.setBorder(new javax.swing.border.EmptyBorder(22, 24, 22, 24));

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainContent, BorderLayout.CENTER);

        setContentPane(root);
        revalidate();
        repaint();

        setActiveNavigation(navigationButtons.get(0));
        showDashboardPage("Dashboard", username);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setForeground(WHITE);
        button.setBackground(GREEN);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new javax.swing.border.EmptyBorder(10, 13, 10, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground().equals(GREEN)) {
                    button.setBackground(GREEN_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(GREEN_DARK)) {
                    button.setBackground(GREEN);
                }
            }
        });

        return button;
    }

    private void setActiveNavigation(JButton selected) {
        for (JButton button : navigationButtons) {
            button.setBackground(GREEN);
            button.setForeground(WHITE);
            button.setBorder(
                    new javax.swing.border.EmptyBorder(10, 13, 10, 10)
            );
        }

        selected.setBackground(GREEN_DARK);
        selected.setForeground(WHITE);

        // Slim accent bar marks the active page (same total left inset).
        selected.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, PINK_DARK),
                new javax.swing.border.EmptyBorder(10, 10, 10, 10)
        ));
    }

    // ================================================================
    // DASHBOARD PAGES
    // ================================================================
    private void showDashboardPage(String page, String username) {
        mainContent.removeAll();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new javax.swing.border.EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel(page);
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(GREEN);

        JLabel user = new JLabel("Welcome, " + username);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        user.setForeground(SECONDARY);

        header.add(title, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.setOpaque(false);

        switch (page) {
            case "Dashboard":
                createDashboard(pagePanel, username);
                break;
            case "My Profile":
                createProfile(pagePanel);
                break;
            case "Subjects":
                createSubjects(pagePanel);
                break;
            case "Grades":
                createGrades(pagePanel);
                break;
            case "Schedule":
                createSchedule(pagePanel);
                break;
            case "Announcements":
                createAnnouncements(pagePanel);
                break;
        }

        mainContent.add(pagePanel, BorderLayout.CENTER);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private JPanel pillRow(String... texts) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String t : texts) {
            if (t == null || t.trim().isEmpty()) continue;

            if (row.getComponentCount() > 0) {
                row.add(Box.createHorizontalStrut(8));
            }
            row.add(new PillLabel(t.trim()));
        }

        return row;
    }

    private void createDashboard(JPanel panel, String username) {
        Student s = currentStudent;

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);

        CardPanel welcomeCard = new CardPanel(true);
        welcomeCard.setBorder(new javax.swing.border.EmptyBorder(16, 22, 20, 26));
        welcomeCard.setLayout(new BoxLayout(welcomeCard, BoxLayout.Y_AXIS));

        String firstName = (s != null && !s.firstName.trim().isEmpty())
                ? s.firstName.trim() : username;

        JLabel greeting = new JLabel("Good day, " + firstName + "!");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 22));
        greeting.setForeground(WHITE);
        greeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel message = new JLabel(
                "Here is your academic overview for today."
        );
        message.setFont(FONT);
        message.setForeground(new Color(246, 219, 233));
        message.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeCard.add(greeting);
        welcomeCard.add(Box.createVerticalStrut(4));
        welcomeCard.add(message);

        if (s != null) {
            JPanel pills = pillRow(s.course, s.yearLevel, s.major);
            if (pills.getComponentCount() > 0) {
                welcomeCard.add(Box.createVerticalStrut(12));
                welcomeCard.add(pills);
            }
        }

        content.add(welcomeCard, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);

        cards.add(summaryCard("book", "Subjects", "6"));
        cards.add(summaryCard("chart", "Current GWA", "1.75"));
        cards.add(summaryCard("check", "Attendance", "96%"));
        cards.add(summaryCard("clock", "Tasks Due", "3"));

        content.add(cards, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 0));
        bottom.setOpaque(false);

        JPanel schedule = card();
        schedule.setLayout(new BoxLayout(schedule, BoxLayout.Y_AXIS));
        schedule.add(cardHeader("calendar", "Today's Schedule"));
        schedule.add(Box.createVerticalStrut(9));
        schedule.add(infoRow("08:00 AM", "Computer Programming"));
        schedule.add(infoRow("10:00 AM", "Business Management"));
        schedule.add(infoRow("01:00 PM", "Entrepreneurship"));

        JPanel announcements = card();
        announcements.setLayout(new BoxLayout(announcements, BoxLayout.Y_AXIS));
        announcements.add(cardHeader("bell", "Announcements"));
        announcements.add(Box.createVerticalStrut(9));
        announcements.add(announcement(
                "Midterm examination schedule has been posted."
        ));
        announcements.add(Box.createVerticalStrut(8));
        announcements.add(announcement(
                "Please check your subjects for updated activities."
        ));

        bottom.add(schedule);
        bottom.add(announcements);

        content.add(bottom, BorderLayout.SOUTH);
        panel.add(content, BorderLayout.CENTER);
    }

    // ================================================================
    // PROFILE - cards, edit / save / delete, profile picture
    // ================================================================
    private void refreshProfile() {
        showDashboardPage("My Profile", currentStudent.username);
    }

    private void profileError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Profile Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Input components shown while the profile is being edited.
    private class ProfileInputs {
        final JTextField first;
        final JTextField middle;
        final JTextField last;
        final JTextField birthdate;
        final JTextField studentNo;
        final JTextField email;
        final JTextField contact;
        final JTextField address;
        final JComboBox<String> gender;
        final JComboBox<String> course;
        final JComboBox<String> year;
        final JComboBox<String> major;

        ProfileInputs(Student s) {
            first = editField(s.firstName);
            middle = editField(s.middleName);
            last = editField(s.lastName);
            birthdate = editField(s.birthdate);
            studentNo = editField(s.studentNo);
            email = editField(s.email);
            contact = editField(s.contactNo);
            address = editField(s.address);

            gender = createCombo("Select gender", GENDERS);
            course = createCombo("Select course", COURSES);
            year = createCombo("Select year", YEAR_LEVELS);
            major = createCombo("Select major", MAJORS);

            selectOrAdd(gender, s.gender);
            selectOrAdd(course, s.course);
            selectOrAdd(year, s.yearLevel);
            selectOrAdd(major, s.major);

            for (JComboBox<String> box : java.util.List.of(
                    gender, course, year, major)) {
                box.setPreferredSize(new Dimension(100, 36));
            }
        }
    }

    private JTextField editField(String value) {
        JTextField field = new RoundTextField();
        decorateField(field, 100, 36, 6);
        field.setText(value == null ? "" : value);
        return field;
    }

    private JLabel valueLabel(String text) {
        boolean blank = text == null || text.trim().isEmpty();

        JLabel l = new JLabel(blank ? "-" : text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(blank ? SECONDARY : TEXT);
        return l;
    }

    private JLabel addressLabel(String text) {
        if (text == null || text.trim().isEmpty()) return valueLabel("");

        JLabel l = new JLabel(
                "<html><body style='width:270px'>"
                        + escapeHtml(text.trim()) + "</body></html>"
        );
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(TEXT);
        return l;
    }

    private JPanel fieldRow(String label, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(0, 3));
        row.setOpaque(false);

        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(SECONDARY);

        row.add(l, BorderLayout.NORTH);
        row.add(component, BorderLayout.CENTER);
        return row;
    }

    private CardPanel profileCard(String icon, String title,
                                  JComponent... rows) {
        CardPanel c = card();
        c.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 2, 0);
        c.add(cardHeader(icon, title), g);

        for (int i = 0; i < rows.length; i++) {
            g.gridy = i + 1;
            g.insets = new Insets(10, 0, 0, 0);
            c.add(rows[i], g);
        }

        return c;
    }

    private JPanel stackColumn(JComponent... cards) {
        JPanel stack = new JPanel(new GridBagLayout());
        stack.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < cards.length; i++) {
            g.gridy = i;
            g.insets = new Insets(0, 0, i < cards.length - 1 ? 12 : 0, 0);
            stack.add(cards[i], g);
        }

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(stack, BorderLayout.NORTH);
        return wrap;
    }

    private void createProfile(JPanel panel) {
        final Student s = currentStudent;
        final boolean editing = profileEditing;
        final ProfileInputs inputs = editing ? new ProfileInputs(s) : null;

        // ---------------- Hero card ----------------
        CardPanel hero = new CardPanel(true);
        hero.setBorder(new javax.swing.border.EmptyBorder(18, 22, 22, 26));
        hero.setLayout(new BorderLayout(22, 0));

        ProfilePhoto photo = new ProfilePhoto(112);
        photo.setImage(s.photo);
        hero.add(photo, BorderLayout.WEST);

        JLabel name = new JLabel(displayName(s));
        name.setFont(new Font("Segoe UI", Font.BOLD, 24));
        name.setForeground(WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        StringBuilder subtitleText = new StringBuilder();
        if (!s.course.trim().isEmpty()) subtitleText.append(s.course.trim());
        if (!s.yearLevel.trim().isEmpty()) {
            if (subtitleText.length() > 0) subtitleText.append("  \u2022  ");
            subtitleText.append(s.yearLevel.trim());
        }
        if (subtitleText.length() == 0) subtitleText.append("Student");

        JLabel subtitle = new JLabel(subtitleText.toString());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(246, 219, 233));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pills = pillRow(
                s.studentNo.trim().isEmpty()
                        ? "" : "Student No. " + s.studentNo.trim(),
                s.major,
                "Active"
        );

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(name);
        stack.add(Box.createVerticalStrut(2));
        stack.add(subtitle);
        stack.add(Box.createVerticalStrut(12));
        stack.add(pills);

        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);

        GridBagConstraints ic = new GridBagConstraints();
        ic.anchor = GridBagConstraints.WEST;
        ic.weightx = 1;
        ic.weighty = 1;
        info.add(stack, ic);

        hero.add(info, BorderLayout.CENTER);

        // ---------------- Action buttons ----------------
        JButton upload = createOutlineButton("UPLOAD PHOTO");
        JButton edit = createOutlineButton("EDIT PROFILE");
        JButton save = createGreenButton("SAVE CHANGES");
        JButton cancel = createOutlineButton("CANCEL");
        JButton delete = createOutlineButton("DELETE ACCOUNT");

        JButton[] allButtons = {upload, edit, save, cancel, delete};
        for (JButton b : allButtons) {
            b.setPreferredSize(new Dimension(100, 38));
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        edit.setEnabled(!editing);
        save.setEnabled(editing);
        cancel.setEnabled(editing);

        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 0));
        actions.setOpaque(false);
        for (JButton b : allButtons) actions.add(b);

        upload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Profile Picture");
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter(
                    "Images (JPG, PNG, GIF, BMP)",
                    "jpg", "jpeg", "png", "gif", "bmp"
            ));

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                BufferedImage saved = savePhoto(
                        s.username, chooser.getSelectedFile()
                );
                s.photo = saved;
                photo.setImage(saved);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to upload the picture. Please choose a "
                                + "valid image file.",
                        "Upload Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        edit.addActionListener(e -> {
            profileEditing = true;
            refreshProfile();
        });

        cancel.addActionListener(e -> {
            profileEditing = false;
            refreshProfile();
        });

        save.addActionListener(e -> {
            if (inputs == null) return;

            String first = inputs.first.getText().trim();
            String middle = inputs.middle.getText().trim();
            String last = inputs.last.getText().trim();
            String studentNo = inputs.studentNo.getText().trim();
            String email = inputs.email.getText().trim();
            String contact = inputs.contact.getText().trim();
            String address = inputs.address.getText().trim();
            String birth = inputs.birthdate.getText().trim();

            if (first.isEmpty() || middle.isEmpty() || last.isEmpty()
                    || studentNo.isEmpty() || email.isEmpty()) {
                profileError("Student No., name, and email address "
                        + "cannot be empty.");
                return;
            }

            if (inputs.course.getSelectedIndex() <= 0
                    || inputs.year.getSelectedIndex() <= 0
                    || inputs.major.getSelectedIndex() <= 0) {
                profileError("Please select your course, year level, "
                        + "and major.");
                return;
            }

            String[] texts = {first, middle, last, studentNo, email,
                    contact, address, birth};
            for (String t : texts) {
                if (t.contains("|")) {
                    profileError("Fields cannot contain the | character.");
                    return;
                }
            }

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                profileError("Please enter a valid email address.");
                return;
            }

            if (!contact.isEmpty()
                    && !PHONE_PATTERN.matcher(contact).matches()) {
                profileError("Please enter a valid contact number.");
                return;
            }

            if (!birth.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(birth);
                    if (date.isAfter(LocalDate.now())) {
                        profileError("Birthdate cannot be in the future.");
                        return;
                    }
                    birth = date.toString();
                } catch (DateTimeParseException ex) {
                    profileError("Please enter the birthdate as "
                            + "YYYY-MM-DD.");
                    return;
                }
            }

            if (studentNoExists(studentNo, s.username)) {
                profileError("Student No. is already registered to "
                        + "another account.");
                return;
            }

            Student updated = new Student();
            updated.username = s.username;
            updated.studentNo = studentNo;
            updated.firstName = first;
            updated.middleName = middle;
            updated.lastName = last;
            updated.email = email;
            updated.course = (String) inputs.course.getSelectedItem();
            updated.yearLevel = (String) inputs.year.getSelectedItem();
            updated.major = (String) inputs.major.getSelectedItem();
            updated.contactNo = contact;
            updated.birthdate = birth;
            updated.gender = inputs.gender.getSelectedIndex() > 0
                    ? (String) inputs.gender.getSelectedItem() : "";
            updated.address = address;
            updated.registered = s.registered;
            updated.photo = s.photo;

            if (!updateUserFile(s.username, updated)) {
                profileError("Unable to save your changes.");
                return;
            }

            currentStudent = updated;
            profileEditing = false;

            JOptionPane.showMessageDialog(
                    this,
                    "Your profile has been updated successfully!",
                    "Profile Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshProfile();
        });

        delete.addActionListener(e -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Delete your account permanently?\n"
                            + "Your profile information and picture will "
                            + "be removed.\nThis action cannot be undone.",
                    "Delete Account",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{"Delete Account", "Cancel"},
                    "Cancel"
            );

            if (choice != 0) return;

            if (!updateUserFile(s.username, null)) {
                profileError("Unable to delete your account.");
                return;
            }

            File pictureFile = photoFile(s.username);
            if (pictureFile.exists() && !pictureFile.delete()) {
                pictureFile.deleteOnExit();
            }

            profileEditing = false;
            currentStudent = null;
            navigationButtons.clear();

            JOptionPane.showMessageDialog(
                    this,
                    "Your account has been deleted.",
                    "Account Deleted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            showLoginPage();
        });

        // ---------------- Information cards ----------------
        JComponent firstC = editing ? inputs.first : valueLabel(s.firstName);
        JComponent middleC = editing ? inputs.middle : valueLabel(s.middleName);
        JComponent lastC = editing ? inputs.last : valueLabel(s.lastName);
        JComponent birthC = editing
                ? inputs.birthdate : valueLabel(formatDate(s.birthdate));
        JComponent genderC = editing ? inputs.gender : valueLabel(s.gender);

        JComponent emailC = editing ? inputs.email : valueLabel(s.email);
        JComponent contactC = editing
                ? inputs.contact : valueLabel(s.contactNo);
        JComponent addressC = editing
                ? inputs.address : addressLabel(s.address);

        JComponent studentNoC = editing
                ? inputs.studentNo : valueLabel(s.studentNo);
        JComponent courseC = editing ? inputs.course : valueLabel(s.course);
        JComponent yearC = editing ? inputs.year : valueLabel(s.yearLevel);
        JComponent majorC = editing ? inputs.major : valueLabel(s.major);

        JPanel personal = profileCard(
                "person", "Personal Information",
                fieldRow("First Name", firstC),
                fieldRow("Middle Name", middleC),
                fieldRow("Last Name", lastC),
                fieldRow(editing ? "Birthdate (YYYY-MM-DD)" : "Birthdate",
                        birthC),
                fieldRow("Gender", genderC)
        );

        JPanel contact = profileCard(
                "mail", "Contact Details",
                fieldRow("Email Address", emailC),
                fieldRow("Contact No.", contactC),
                fieldRow("Address", addressC)
        );

        JPanel academic = profileCard(
                "cap", "Academic Information",
                fieldRow("Student No.", studentNoC),
                fieldRow("Course", courseC),
                fieldRow("Year Level", yearC),
                fieldRow("Major", majorC)
        );

        JPanel account = profileCard(
                "lock", "Account Details",
                fieldRow("Username", valueLabel(s.username)),
                fieldRow("Student Status", valueLabel("Active")),
                fieldRow("Portal Account", valueLabel("Verified")),
                fieldRow("Date Registered",
                        valueLabel(formatDate(s.registered)))
        );

        JPanel columns = new JPanel(new GridLayout(1, 2, 14, 0));
        columns.setOpaque(false);
        columns.add(stackColumn(personal, contact));
        columns.add(stackColumn(academic, account));

        ScrollablePanel content = new ScrollablePanel(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 12, 0);
        content.add(hero, g);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 14, 4);
        content.add(actions, g);

        g.gridy = 2;
        g.insets = new Insets(0, 0, 0, 0);
        content.add(columns, g);

        JScrollPane scroll = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setOpaque(false);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());

        panel.add(scroll, BorderLayout.CENTER);
    }

    private void createSubjects(JPanel panel) {
        JPanel c = card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(cardHeader("book", "My Subjects"));
        c.add(Box.createVerticalStrut(15));

        String[] subjects = {
                "Computer Programming",
                "Business Management",
                "Entrepreneurship",
                "Marketing Management",
                "Communication Skills",
                "Physical Education"
        };

        for (String subject : subjects) {
            JLabel label = new JLabel("• " + subject);
            label.setFont(FONT);
            label.setForeground(TEXT);
            c.add(label);
            c.add(Box.createVerticalStrut(7));
        }

        panel.add(c, BorderLayout.NORTH);
    }

    private void createGrades(JPanel panel) {
        JPanel c = card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(cardHeader("chart", "Academic Grades"));
        c.add(Box.createVerticalStrut(15));

        c.add(detailRow("Computer Programming", "1.50"));
        c.add(detailRow("Business Management", "1.75"));
        c.add(detailRow("Entrepreneurship", "1.75"));
        c.add(detailRow("Marketing Management", "2.00"));
        c.add(detailRow("Communication Skills", "1.50"));

        JLabel gwa = new JLabel("Current GWA: 1.75");
        gwa.setFont(new Font("Segoe UI", Font.BOLD, 17));
        gwa.setForeground(GREEN);
        c.add(Box.createVerticalStrut(8));
        c.add(gwa);

        panel.add(c, BorderLayout.NORTH);
    }

    private void createSchedule(JPanel panel) {
        JPanel c = card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(cardHeader("calendar", "Class Schedule"));
        c.add(Box.createVerticalStrut(15));

        c.add(infoRow("08:00 AM", "Computer Programming"));
        c.add(infoRow("10:00 AM", "Business Management"));
        c.add(infoRow("01:00 PM", "Entrepreneurship"));
        c.add(infoRow("03:00 PM", "Marketing Management"));

        panel.add(c, BorderLayout.NORTH);
    }

    private void createAnnouncements(JPanel panel) {
        JPanel c = card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(cardHeader("bell", "Announcements"));
        c.add(Box.createVerticalStrut(15));

        c.add(announcement(
                "Midterm examination schedule has been posted."
        ));
        c.add(Box.createVerticalStrut(12));
        c.add(announcement(
                "Students are reminded to submit pending activities."
        ));
        c.add(Box.createVerticalStrut(12));
        c.add(announcement(
                "Please check the student portal regularly for updates."
        ));

        panel.add(c, BorderLayout.NORTH);
    }

    // ================================================================
    // UI HELPERS
    // ================================================================
    private CardPanel card() {
        CardPanel p = new CardPanel(false);
        p.setBorder(new javax.swing.border.EmptyBorder(16, 18, 20, 22));
        return p;
    }

    private JPanel summaryCard(String icon, String name, String value) {
        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel n = new JLabel(name);
        n.setFont(new Font("Segoe UI", Font.BOLD, 12));
        n.setForeground(SECONDARY);

        top.add(new IconBadge(icon, 30), BorderLayout.WEST);
        top.add(n, BorderLayout.CENTER);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 26));
        v.setForeground(GREEN);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(top);
        p.add(Box.createVerticalStrut(10));
        p.add(v);
        p.add(Box.createVerticalGlue());

        return p;
    }

    // Icon badge + title + thin divider used at the top of every card.
    private JPanel cardHeader(String icon, String title) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(GREEN);

        row.add(new IconBadge(icon, 30), BorderLayout.WEST);
        row.add(t, BorderLayout.CENTER);

        JPanel line = new JPanel();
        line.setBackground(BORDER);
        line.setPreferredSize(new Dimension(1, 1));

        JPanel header = new JPanel(new BorderLayout(0, 9));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(row, BorderLayout.NORTH);
        header.add(line, BorderLayout.SOUTH);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        return header;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(GREEN);
        return label;
    }

    private JPanel infoRow(String time, String subject) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new javax.swing.border.EmptyBorder(4, 0, 4, 0));

        JLabel t = new JLabel(time);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(GREEN);

        JLabel s = new JLabel(subject);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(TEXT);

        row.add(t, BorderLayout.WEST);
        row.add(s, BorderLayout.CENTER);

        return row;
    }

    private JLabel announcement(String text) {
        JLabel label = new JLabel(
                "<html><body style='width:320px'>• " + text
                        + "</body></html>"
        );
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel detailRow(String name, String value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setBorder(new javax.swing.border.EmptyBorder(5, 0, 5, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel n = new JLabel(name);
        n.setFont(new Font("Segoe UI", Font.BOLD, 12));
        n.setForeground(SECONDARY);

        String shown = (value == null || value.trim().isEmpty())
                ? "-" : value;

        JLabel v = new JLabel(shown);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(TEXT);

        row.add(n, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);

        return row;
    }

    // Q20 - Modern text fields: padding, visible border, rounded appearance,
    // consistent height and width.
    private void styleField(JTextField field) {
        decorateField(field, 315, 43, 12);
    }

    private void decorateField(JTextField field, int width, int height,
                               int vPad) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(GREEN);
        field.setPreferredSize(new Dimension(width, height));

        Border idle = new RoundedFieldBorder(PINK_DARK, 1, 12, vPad, 12);
        Border focus = new RoundedFieldBorder(GREEN, 2, 12, vPad, 12);
        field.setBorder(idle);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focus);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(idle);
            }
        });
    }

    // Q18 - Green button hover becomes noticeably darker and uses hand cursor.
    private JButton createGreenButton(String text) {
        JButton b = new RoundButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(WHITE);
        b.setBackground(GREEN);
        b.setFocusPainted(false);
        b.setBorder(new RoundedButtonBorder(GREEN, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(315, 43));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) b.setBackground(GREEN_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(GREEN);
            }
        });

        return b;
    }

    private JButton createOutlineButton(String text) {
        JButton b = new RoundButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(GREEN);
        b.setBackground(PINK_LIGHT);
        b.setFocusPainted(false);
        b.setBorder(new RoundedButtonBorder(GREEN, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(315, 40));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) b.setBackground(new Color(247, 227, 238));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(PINK_LIGHT);
            }
        });

        return b;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT);
        return l;
    }

    // ================================================================
    // PASSWORD HASHING
    // ================================================================
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) result.append('0');
                result.append(hex);
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ================================================================
    // Q25 - ELEGANT MINIMAL STUDENT LOGO (scales with its size)
    // ================================================================
    private static class StudentLogo extends JPanel {

        StudentLogo(int size) {
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();
            double s = Math.min(w, h) / 96.0;

            // Soft circular badge with a fine double ring.
            g2.setColor(new Color(255, 249, 252));
            g2.fillOval(3, 3, w - 6, h - 6);

            g2.setColor(PINK_DARK);
            g2.setStroke(new BasicStroke((float) (2.2 * s)));
            g2.drawOval(5, 5, w - 10, h - 10);

            g2.setStroke(new BasicStroke((float) (0.9 * s)));
            int inset = (int) Math.round(11 * s);
            g2.drawOval(inset, inset, w - inset * 2, h - inset * 2);

            double cx = w / 2.0;
            double cy = h / 2.0 + 1 * s;

            // Cap base (drawn first so the board sits on top).
            Path2D base = new Path2D.Double();
            base.moveTo(cx - 16 * s, cy - 2 * s);
            base.lineTo(cx - 16 * s, cy + 11 * s);
            base.quadTo(cx, cy + 21 * s, cx + 16 * s, cy + 11 * s);
            base.lineTo(cx + 16 * s, cy - 2 * s);
            base.lineTo(cx, cy + 5 * s);
            base.closePath();
            g2.setColor(GREEN_DARK);
            g2.fill(base);

            // Cap board (diamond).
            Path2D board = new Path2D.Double();
            board.moveTo(cx, cy - 23 * s);
            board.lineTo(cx + 30 * s, cy - 9 * s);
            board.lineTo(cx, cy + 5 * s);
            board.lineTo(cx - 30 * s, cy - 9 * s);
            board.closePath();
            g2.setColor(GREEN);
            g2.fill(board);

            // Center button.
            g2.setColor(PINK_DARK);
            double r = 2.6 * s;
            g2.fill(new Ellipse2D.Double(cx - r, cy - 9 * s - r, r * 2, r * 2));

            // Tassel.
            g2.setStroke(new BasicStroke(
                    (float) (1.8 * s), BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));
            g2.drawLine(
                    (int) Math.round(cx), (int) Math.round(cy - 9 * s),
                    (int) Math.round(cx + 24 * s),
                    (int) Math.round(cy - 3 * s)
            );
            g2.drawLine(
                    (int) Math.round(cx + 24 * s),
                    (int) Math.round(cy - 3 * s),
                    (int) Math.round(cx + 24 * s),
                    (int) Math.round(cy + 9 * s)
            );
            double t = 2.4 * s;
            g2.fill(new Ellipse2D.Double(
                    cx + 24 * s - t, cy + 9 * s - t * 0.4, t * 2, t * 2
            ));

            g2.dispose();
        }
    }

    // ================================================================
    // PROFILE PICTURE FRAME (default: bald cute anonymous silhouette)
    // ================================================================
    private static class ProfilePhoto extends JPanel {

        private BufferedImage image;

        ProfilePhoto(int size) {
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setOpaque(false);
        }

        void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            int d = Math.min(getWidth(), getHeight());
            int pad = 4;
            int inner = d - pad * 2;

            Shape circle = new Ellipse2D.Double(pad, pad, inner, inner);
            Shape oldClip = g2.getClip();
            g2.clip(circle);

            if (image != null) {
                g2.drawImage(image, pad, pad, inner, inner, null);
            } else {
                // Soft backdrop.
                g2.setColor(PINK);
                g2.fill(circle);

                g2.setColor(PINK_DARK);

                double cx = d / 2.0;

                // Shoulders / body.
                g2.fill(new Ellipse2D.Double(
                        cx - d * 0.31, d * 0.68, d * 0.62, d * 0.60
                ));

                // Ears.
                double ear = d * 0.075;
                g2.fill(new Ellipse2D.Double(
                        cx - d * 0.205 - ear / 2, d * 0.40, ear, ear * 1.3
                ));
                g2.fill(new Ellipse2D.Double(
                        cx + d * 0.205 - ear / 2, d * 0.40, ear, ear * 1.3
                ));

                // Bald round head.
                g2.fill(new Ellipse2D.Double(
                        cx - d * 0.19, d * 0.23, d * 0.38, d * 0.38
                ));

                // Neck.
                g2.fillRect(
                        (int) (cx - d * 0.06), (int) (d * 0.56),
                        (int) (d * 0.12), (int) (d * 0.16)
                );
            }

            g2.setClip(oldClip);

            // Elegant ring.
            g2.setColor(PINK_DARK);
            g2.setStroke(new BasicStroke(3f));
            g2.draw(circle);

            g2.setColor(GREEN);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new Ellipse2D.Double(
                    pad - 3, pad - 3, inner + 6, inner + 6
            ));

            g2.dispose();
        }
    }

    // ================================================================
    // CUSTOM COMPONENTS
    // ================================================================

    // Text field that paints its own rounded white background so the
    // corners blend into the pink panel.
    private static class RoundTextField extends JTextField {
        RoundTextField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(getBackground());
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundPasswordField extends JPasswordField {
        RoundPasswordField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(getBackground());
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundCombo extends JComboBox<String> {
        RoundCombo(String[] items) {
            super(items);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(getBackground());
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Flat combo box: green drop-down arrow and no blue focus fill.
    private static class FlatComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g2.setColor(GREEN);
                    g2.fillPolygon(
                            new int[]{cx - 5, cx + 5, cx},
                            new int[]{cy - 2, cy - 2, cy + 3},
                            3
                    );
                    g2.dispose();
                }
            };
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
                                                boolean hasFocus) {
            super.paintCurrentValueBackground(g, bounds, false);
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds,
                                      boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false);
        }
    }

    private static class ComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel l = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus
            );

            l.setFont(FONT);
            l.setBorder(new javax.swing.border.EmptyBorder(5, 10, 5, 10));

            boolean placeholder = value != null
                    && value.toString().startsWith("Select ");

            if (index < 0) {
                // Selected value shown inside the closed box.
                l.setOpaque(false);
                l.setForeground(placeholder ? SECONDARY : TEXT);
                l.setBorder(new javax.swing.border.EmptyBorder(0, 4, 0, 4));
            } else {
                l.setOpaque(true);
                l.setBackground(isSelected ? PINK : WHITE);
                l.setForeground(isSelected ? GREEN
                        : (placeholder ? SECONDARY : TEXT));
            }

            return l;
        }
    }

    // ================================================================
    // CARD GRAPHICS
    // ================================================================

    // Rounded card with a soft layered shadow. gradient = hero style.
    private static class CardPanel extends JPanel {
        private static final int ARC = 18;
        private final boolean gradient;

        CardPanel(boolean gradient) {
            this.gradient = gradient;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth() - 4;
            int h = getHeight() - 4;

            // Soft shadow.
            for (int i = 4; i >= 1; i--) {
                g2.setColor(new Color(22, 101, 52, 9));
                g2.fillRoundRect(i, i, w, h, ARC, ARC);
            }

            RoundRectangle2D shape = new RoundRectangle2D.Double(
                    0, 0, w, h, ARC, ARC
            );
            Shape oldClip = g2.getClip();

            if (gradient) {
                g2.setPaint(new GradientPaint(0, 0, GREEN, w, h, GREEN_DARK));
                g2.fill(shape);

                g2.clip(shape);

                // Thin orbit rings.
                g2.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 4; i++) {
                    int r = 70 + i * 38;
                    g2.setColor(new Color(255, 255, 255, 26 - i * 5));
                    g2.drawOval(w - r, -r, r * 2, r * 2);

                    g2.setColor(new Color(246, 219, 233, 20 - i * 4));
                    g2.drawOval(-r / 2, h - r / 2, r, r);
                }

                // Accent line along the bottom edge.
                g2.setColor(PINK_DARK);
                g2.fillRect(0, h - 3, w, 3);

                g2.setClip(oldClip);
            } else {
                g2.setColor(PINK_LIGHT);
                g2.fill(shape);

                // Green accent line along the top edge.
                g2.clip(shape);
                g2.setColor(GREEN);
                g2.fillRect(0, 0, w, 3);
                g2.setClip(oldClip);

                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(
                        0.5, 0.5, w - 1, h - 1, ARC, ARC
                ));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Round green badge with a simple line icon.
    private static class IconBadge extends JComponent {
        private final String type;

        IconBadge(String type, int size) {
            this.type = type;
            Dimension d = new Dimension(size, size);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setRenderingHint(
                    RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE
            );

            double s = Math.min(getWidth(), getHeight()) / 30.0;
            int d = (int) Math.round(30 * s);

            g2.setColor(GREEN);
            g2.fillOval(0, 0, d, d);
            g2.setColor(PINK_DARK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(0, 0, d - 1, d - 1);

            g2.scale(s, s);
            g2.setColor(PINK_LIGHT);
            g2.setStroke(new BasicStroke(
                    1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
            ));

            switch (type) {
                case "person":
                    g2.draw(new Ellipse2D.Double(11, 7, 8, 8));
                    g2.draw(new Arc2D.Double(8, 17, 14, 12, 0, 180,
                            Arc2D.OPEN));
                    break;

                case "mail": {
                    g2.draw(new RoundRectangle2D.Double(7, 9, 16, 12, 3, 3));
                    Path2D flap = new Path2D.Double();
                    flap.moveTo(7.5, 10);
                    flap.lineTo(15, 16);
                    flap.lineTo(22.5, 10);
                    g2.draw(flap);
                    break;
                }

                case "cap": {
                    Path2D board = new Path2D.Double();
                    board.moveTo(15, 8);
                    board.lineTo(25, 12.5);
                    board.lineTo(15, 17);
                    board.lineTo(5, 12.5);
                    board.closePath();
                    g2.draw(board);

                    Path2D base = new Path2D.Double();
                    base.moveTo(10, 15.2);
                    base.lineTo(10, 19);
                    base.quadTo(15, 22.5, 20, 19);
                    base.lineTo(20, 15.2);
                    g2.draw(base);
                    break;
                }

                case "lock":
                    g2.fill(new RoundRectangle2D.Double(9, 14, 12, 9, 3, 3));
                    g2.draw(new Arc2D.Double(11, 7, 8, 12, 0, 180,
                            Arc2D.OPEN));
                    break;

                case "book":
                    g2.draw(new RoundRectangle2D.Double(7, 9, 16, 13, 3, 3));
                    g2.draw(new Line2D.Double(15, 9, 15, 22));
                    break;

                case "chart":
                    g2.fill(new RoundRectangle2D.Double(9, 16, 3, 6, 1, 1));
                    g2.fill(new RoundRectangle2D.Double(14, 12, 3, 10, 1, 1));
                    g2.fill(new RoundRectangle2D.Double(19, 8, 3, 14, 1, 1));
                    break;

                case "clock":
                    g2.draw(new Ellipse2D.Double(8, 8, 14, 14));
                    g2.draw(new Line2D.Double(15, 15, 15, 10.5));
                    g2.draw(new Line2D.Double(15, 15, 18.5, 17));
                    break;

                case "check": {
                    Path2D tick = new Path2D.Double();
                    tick.moveTo(9, 15.5);
                    tick.lineTo(13.5, 20);
                    tick.lineTo(21.5, 10.5);
                    g2.draw(tick);
                    break;
                }

                case "calendar":
                    g2.draw(new RoundRectangle2D.Double(8, 9, 14, 13, 3, 3));
                    g2.draw(new Line2D.Double(8, 14, 22, 14));
                    g2.draw(new Line2D.Double(12, 7, 12, 10.5));
                    g2.draw(new Line2D.Double(18, 7, 18, 10.5));
                    break;

                case "bell":
                    g2.draw(new Arc2D.Double(9, 8, 12, 14, 0, 180,
                            Arc2D.OPEN));
                    g2.draw(new Line2D.Double(9, 15, 9, 20));
                    g2.draw(new Line2D.Double(21, 15, 21, 20));
                    g2.draw(new Line2D.Double(8, 20, 22, 20));
                    g2.fill(new Ellipse2D.Double(13.5, 22, 3, 3));
                    break;

                default:
                    break;
            }

            g2.dispose();
        }
    }

    // Translucent pill used for chips on the green hero cards.
    private static class PillLabel extends JLabel {
        PillLabel(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(WHITE);
            setOpaque(false);
            setBorder(new javax.swing.border.EmptyBorder(4, 12, 4, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            int w = getWidth() - 1;
            int h = getHeight() - 1;

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, w, h, h, h);
            g2.setColor(new Color(255, 255, 255, 95));
            g2.drawRoundRect(0, 0, w, h, h, h);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // Panel that follows the width of its scroll pane viewport.
    private static class ScrollablePanel extends JPanel implements Scrollable {
        ScrollablePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                                              int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                                               int orientation, int direction) {
            return Math.max(40, visibleRect.height - 30);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // Slim, flat scroll bar in the portal colors.
    private static class SlimScrollBarUI extends BasicScrollBarUI {
        private JButton emptyButton() {
            JButton b = new JButton();
            Dimension zero = new Dimension(0, 0);
            b.setPreferredSize(zero);
            b.setMinimumSize(zero);
            b.setMaximumSize(zero);
            return b;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return emptyButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return emptyButton();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            // Transparent track.
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(PINK_DARK);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 6, 6);
            g2.dispose();
        }
    }

    // Button that fills a rounded shape so corners match the border.
    private static class RoundButton extends JButton {
        RoundButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            if (!isEnabled()) {
                // Faded look for disabled buttons.
                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );
                g2.setComposite(AlphaComposite.SrcOver.derive(0.42f));
                g2.setColor(getBackground());
                g2.fillRoundRect(
                        0, 0, getWidth() - 1, getHeight() - 1, 10, 10
                );
                g2.setFont(getFont());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
                return;
            }

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ================================================================
    // Q20 - ROUNDED FIELD BORDER
    // ================================================================
    private static class RoundedFieldBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int arc;
        private final int vPad;
        private final int hPad;

        RoundedFieldBorder(Color color, int thickness, int arc, int padding) {
            this(color, thickness, arc, padding, padding);
        }

        RoundedFieldBorder(Color color, int thickness, int arc,
                           int vPad, int hPad) {
            this.color = color;
            this.thickness = thickness;
            this.arc = arc;
            this.vPad = vPad;
            this.hPad = hPad;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(
                    x + 1,
                    y + 1,
                    width - 3,
                    height - 3,
                    arc,
                    arc
            );
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(vPad, hPad, vPad, hPad);
        }
    }

    private static class RoundedButtonBorder extends AbstractBorder {
        private final Color color;
        private final int arc;

        RoundedButtonBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(c.isEnabled() ? color : new Color(
                    color.getRed(), color.getGreen(), color.getBlue(), 100
            ));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    arc,
                    arc
            );
            g2.dispose();
        }
    }

    // ================================================================
    // MAIN
    // ================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginApp app = new LoginApp();
            app.setVisible(true);
        });
    }
}