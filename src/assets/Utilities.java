package assets;

import DbOperations.DbColumns;
import DbOperations.DbTables;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatter;

public class Utilities extends DbOperations.DbConnection{
    
    public static String capitalizeEachWord(String fullName) {
        String[] words = fullName.split("\\s+"); 

        StringBuilder capitalizedFullName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                capitalizedFullName.append(capitalizedWord).append(" ");
            }
        }
        return capitalizedFullName.toString().trim(); 
    }
    
    public static String getCurrentDate(JDateChooser g_date) {
        if (g_date == null || g_date.getDate() == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            return dateFormat.format(new Date());
        } else {
            Date selectedDate = g_date.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            return dateFormat.format(selectedDate);
        }
    }

    public static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return now.format(formatter);
    }
    
    public static Object get_AddedDate(JDateChooser g_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(Helper.dateFormat);
        if(g_date.getDate() != null){
            return dateFormat.format(g_date.getDate());
        }
        return java.sql.Date.valueOf(LocalDate.now());
    }
    
    public static Object get_RecordID(javax.swing.JTable tb){
        try{
            return tb.getValueAt(tb.getSelectedRow(), 0);
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }    
    
    public static Object[] get_RecordIDs(javax.swing.JTable tb){
        Object[] recordIDs = new Object[tb.getSelectedRowCount()];
        int[] selectedRows = tb.getSelectedRows();

        for (int i = 0; i < selectedRows.length; i++) {
            try {
                recordIDs[i] = tb.getValueAt(selectedRows[i], 0);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                recordIDs[i] = 0; 
            }
        }
        return recordIDs;
    }   
    
    public static boolean has_NoZeroVal(Object[] array){
        for(Object i : array){
            if(i == "0"){
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValueExists(String valueToCheck, String column,String table){
        boolean exist = false;
        String query = "SELECT * FROM "+table+" WHERE "+column+" = LOWER(?)";
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, valueToCheck.toLowerCase());
            
            result = prepare.executeQuery();
            
            if(result.next()){
                exist = true;
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return exist;
    }
    

    public static String first_LetterUpperCase(String text){
        if (text.isEmpty()) {
            return null; 
        } else {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }  
    
    public static String[] getAllValue(JTextField addProduct, JTextField addDescription, JSpinner addQuantity, JComboBox addCategory, JSpinner addPrice, JDateChooser addDate,Component parentComponent) {
        try {
            String getProduct = first_LetterUpperCase(addProduct.getText().trim());
            String getDescription = first_LetterUpperCase(addDescription.getText().trim());
            String getQuantity = addQuantity.getValue().toString();
            String getCategory = addCategory.getSelectedItem().toString();
            String getPrice = addPrice.getValue().toString();

            if (getProduct.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Product name cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            } else if (isValueExists(getProduct, DbColumns.IVENTORYCOLUMNS.getValues()[2], DbTables.INVENTORYTABLE.getValue())) {
                JOptionPane.showMessageDialog(parentComponent, "Product with this name already exists.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getDescription.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Description cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getQuantity.isEmpty() || Integer.parseInt(getQuantity) < 1) {
                JOptionPane.showMessageDialog(parentComponent, "Quantity must be greater than 0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getPrice.isEmpty() || Double.parseDouble(getPrice) < 1.0) {
                JOptionPane.showMessageDialog(parentComponent, "Price must be greater than 0.0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new String[]{getCategory, getProduct, getDescription, getQuantity, getPrice, get_AddedDate(addDate).toString()};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid number format. Please enter valid numeric values.","Message", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid input. Please check your entries.","Message", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public static String getSpinnerFromTable(javax.swing.JTable table){
        int editedRow = table.getEditingRow();
        int editedColumn = table.getEditingColumn();
        
        table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        String editedValue = null;
        
        if (editedRow != -1 && editedColumn != -1) {
            TableCellEditor editor = table.getCellEditor(editedRow, editedColumn);
            if (editor != null) {
                editor.stopCellEditing();
            }
            editedValue = model.getValueAt(editedRow, editedColumn).toString();
        }
        
        return editedValue;
    }
    
    public static String getComboxFromTable(JTable table) {
        int row = table.getSelectedRow();
        TableCellEditor editor = table.getCellEditor(row, 1);    
        if (editor instanceof DefaultCellEditor) {
            Component editorComponent = ((DefaultCellEditor) editor).getComponent();
            if (editorComponent instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) editorComponent;
                Object selectedItem = comboBox.getSelectedItem();
                if (selectedItem != null) {
                    return selectedItem.toString();
                }
            }
        }
        return ""; 
    }
    
    public static class IntCustomJSpinner extends DefaultCellEditor {

        private JSpinner input;

        public IntCustomJSpinner() {
            super(new JCheckBox());
            input = new JSpinner();
            SpinnerNumberModel numberModel = (SpinnerNumberModel) input.getModel();
            numberModel.setMinimum(0);
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
            DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
            formatter.setCommitsOnValidEdit(true);
            editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

            editor.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            super.getTableCellEditorComponent(table, value, isSelected, row, column);
            int qty = Integer.parseInt(value.toString());
            input.setValue(qty);
            return input;
        }

        @Override
        public Object getCellEditorValue() {
            return input.getValue();
        }
    }
    
    public static class DoubleCustomJSpinner extends DefaultCellEditor {

        private JSpinner input;

        public DoubleCustomJSpinner() {
            super(new JCheckBox());
            input = new JSpinner();
            SpinnerNumberModel numberModel = new SpinnerNumberModel(1.0, 0.0, Double.MAX_VALUE, 0.1); // Allow float values
            input.setModel(numberModel);
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
            DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
            formatter.setCommitsOnValidEdit(true);
            editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

            editor.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != '.' && c != '-') {
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            super.getTableCellEditorComponent(table, value, isSelected, row, column);
            float qty = Float.parseFloat(value.toString());
            input.setValue(qty);
            return input;
        }

        @Override
        public Object getCellEditorValue() {
            return input.getValue();
        }
    } 
    
    public static class DateJTextField extends JTextField {
        public DateJTextField() {
            super();
        }

        @Override
        public void processKeyEvent(KeyEvent e) {
            char c = e.getKeyChar();
            if (Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                super.processKeyEvent(e);
            } else {
                e.consume();
            }
        }

        @Override
        public void setText(String text) {
            if (isValidDateFormat(text)) {
                super.setText(text);
            } else {
            }
        }

        private boolean isValidDateFormat(String text) {
            return text.matches("\\d{4}-\\d{2}-\\d{2}");
        }
    }
    
    public static class NumberTextField extends JTextField {

        private boolean allowDecimal;

        public NumberTextField(boolean allowDecimal) {
            this.allowDecimal = allowDecimal;
        }

        @Override
        public void processKeyEvent(KeyEvent evt) {
            char c = evt.getKeyChar();
            if (isValidInput(c)) {
                super.processKeyEvent(evt);
            } else {
                evt.consume();
            }
        }

        private boolean isValidInput(char c) {
            if (Character.isDigit(c)) {
                return true;
            } else if (allowDecimal && c == '.' && !getText().contains(".")) {
                // Allows a single decimal point if the field allows decimal values
                return true;
            } else if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                return true;
            }
            return false;
        }
    }
    
    public static boolean validateEmailValue(String _email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(_email);

        return matcher.matches();        
    }
 
    public static boolean validateUsernameValue(String _username){
        String pattern = "^[a-zA-Z]+@[0-9]+$";
        
        Pattern regex = Pattern.compile(pattern);
        
        Matcher matcher = regex.matcher(_username);
        
        return matcher.matches();
    }   
    
    public static boolean validatePassword(String _password, Component parentComponent) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(_password);

        boolean isPhoneNumber = _password.matches("\\d{10}"); // Assuming a 10-digit phone number
        
        if (isPhoneNumber) {
            JOptionPane.showMessageDialog(parentComponent, "You've used a phone number as a password. Please choose a stronger password.");
            return false;
        }

        if (!matcher.matches()) {
            if (!_password.matches(".*[0-9].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one digit.");
                return false;
            }

            if (!_password.matches(".*[a-z].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one lowercase letter.");
                return false;
            }

            if (!_password.matches(".*[A-Z].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one uppercase letter.");
                return false;
            }

            if (!_password.matches(".*[@#$%^&+=].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one special character (@#$%^&+=).");
                return false;
            }

            if (_password.length() < 8) {
                JOptionPane.showMessageDialog(parentComponent, "Password should be at least 8 characters long.");
                return false;
            }

            if (_password.contains(" ")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should not contain spaces.");
                return false;
            }

            return false;
        }

        return true;
    }
    
    public static Object[] getAllUserInput(String userId,String firstName,String lastName,String userName,
            String password,String confirmPassword,String gender, 
            com.toedter.calendar.JDateChooser birthDate, String imgName,int firstUserPos, Component parentComponent) {

        if (firstName == null || lastName == null || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gender == null || birthDate == null) {
            JOptionPane.showMessageDialog(parentComponent, "Please fill in all fields.");
            return null;
        }

        if (!validateUsernameValue(userName)) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid username format.");
            return null;
        }

        if (!validatePassword(password, parentComponent)) {
            return null;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(parentComponent, "Passwords do not match.");
            return null;
        }
        return new Object[]{userId, first_LetterUpperCase(firstName), first_LetterUpperCase(lastName), userName, confirmPassword, get_AddedDate(birthDate), gender, imgName, firstUserPos};
    }
    
    public static String userIdGenerator(int numberOfUsers) {
        String formattedNumber = String.format("%03d", numberOfUsers);
        return "user" + formattedNumber + ".wan";
    }
   
    public static double convertPercentageToNumber(String percentString) {
        if(!percentString.equals("No discount")){
            String numericString = percentString.replaceAll("%", "");
        
            double numericValue = Double.parseDouble(numericString);
            double actualNumber = numericValue / 100.0;

            return actualNumber;
        }else{
            return 0.0;
        }
    }
}
