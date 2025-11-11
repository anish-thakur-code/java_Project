package stickynotes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Vector;

class ExpenseTracker extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtDate, txtCategory, txtAmount, txtDescription;
    private final String FILE_PATH = "expenses.txt";

    public ExpenseTracker() {
        setTitle("💰 Expense Tracker");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color sidebarColor = new Color(31, 41, 55);
        Color background = new Color(245, 247, 250);
        Color redColor = new Color(20, 213, 210);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JLabel title = new JLabel("Expense Tracker", JLabel.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Poppins", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        sidebar.add(title);

        JButton addBtn = createButton("➕ Add", redColor);
        JButton deleteBtn = createButton("🗑 Delete", redColor);
        JButton saveBtn = createButton("💾 Save", redColor);
        JButton loadBtn = createButton("📂 Load", redColor);

        sidebar.add(addBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(deleteBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(saveBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(loadBtn);
        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Date", "Category", "Amount", "Description"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(231, 246, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setBackground(new Color(230, 230, 250));
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBackground(background);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        txtDate = new JTextField(LocalDate.now().toString());
        txtCategory = new JTextField();
        txtAmount = new JTextField();
        txtDescription = new JTextField();

        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(txtDate);
        inputPanel.add(txtCategory);
        inputPanel.add(txtAmount);
        inputPanel.add(txtDescription);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addExpense());
        deleteBtn.addActionListener(e -> deleteExpense());
        saveBtn.addActionListener(e -> saveExpenses());
        loadBtn.addActionListener(e -> loadExpenses());

        loadExpenses();
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 45));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private void addExpense() {
        String date = txtDate.getText().trim();
        String category = txtCategory.getText().trim();
        String amount = txtAmount.getText().trim();
        String desc = txtDescription.getText().trim();

        if (date.isEmpty() || category.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.addRow(new Object[]{date, category, amount, desc});
        clearFields();
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        model.removeRow(row);
    }

    private void saveExpenses() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("expenses.txt"))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                Vector<?> row = (Vector<?>) model.getDataVector().elementAt(i);
                bw.write(String.join("|", row.get(0).toString(), row.get(1).toString(), row.get(2).toString(), row.get(3).toString()));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "💾 Expenses saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        model.setRowCount(0);
        File file = new File("expenses.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\|", -1);
                if (parts.length == 4) model.addRow(parts);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtCategory.setText("");
        txtAmount.setText("");
        txtDescription.setText("");
        txtDate.setText(LocalDate.now().toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new ExpenseTracker().setVisible(true);
        });
    }
}
