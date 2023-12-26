package DbOperations;

import assets.*;
import static DbOperations.DbConnection.connection;
import static DbOperations.DbConnection.prepare;
import static DbOperations.DbConnection.result;
import java.awt.Component;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SalesManagement extends DbConnection{
    private final Component component;
    public final static double TAX_RATE = 0.12;
    
    public SalesManagement(Component component){
        this.component = component;
    }
    
    public void loadInventoryData(JTable table, String search) {
        String[] columnsToDisplay = {"Category", "ProductName", "Description", "Quantity","RetailPrice"};

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.INVENTORYTABLE.getValue()
                + " WHERE " + String.join(" LIKE ? OR ", columnsToDisplay) + " LIKE ?";
        try {
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= columnsToDisplay.length; i++) {
                prepare.setString(i, "%" + search + "%");
            }

            result = prepare.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);

            while (result.next()) {
                Object[] row = new Object[columnsToDisplay.length];
                for (int i = 0; i < columnsToDisplay.length; i++) {
                    row[i] = result.getObject(columnsToDisplay[i]);
                }
                model.addRow(row);
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getSQLState(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String generateReceipt(DefaultTableModel model, String getDate) {
        StringBuilder receipt = new StringBuilder();

        receipt.append("                                    Josuan\n");
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("                                    Receipt\n");
        receipt.append("----------------------------------------------------------------\n");

        String currentDate = getDate;
        String currentTime = Utilities.getCurrentTime();
        receipt.append("Date: ").append(currentDate).append("\n");
        receipt.append("Time: ").append(currentTime).append("\n\n");

        receipt.append("Item                                 Quantity   Price\n");
        receipt.append("----------------------------------------------------------------\n");

        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < model.getRowCount(); i++) {
            String itemName = (String) model.getValueAt(i, 0);
            double price = (double) model.getValueAt(i, 1);
            String formattedDiscount = (String) model.getValueAt(i, 2);
            int quantity = ((Number) model.getValueAt(i, 3)).intValue(); // Quantity at index 3
            double subtotal = ((Number) model.getValueAt(i, 4)).doubleValue(); // Subtotal at index 4
            double total = ((Number) model.getValueAt(i, 5)).doubleValue(); // Total at index 5

            receipt.append(String.format("%d. %-30s x%-9d "+Helper.currency+"%.2f\n", i + 1, itemName, quantity, price));
        }
        receipt.append("----------------------------------------------------------------\n");
        double subtotalSum = 0.0;
        double totalSum = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            subtotalSum += ((Number) model.getValueAt(i, 4)).doubleValue(); // Summing up subtotals
            totalSum += ((Number) model.getValueAt(i, 5)).doubleValue(); // Summing up totals
        }

        receipt.append(String.format("Subtotal:                 "+Helper.currency+"%.2f\n", subtotalSum));
        receipt.append(String.format("Total:                    "+Helper.currency+"%.2f\n", totalSum));
        receipt.append("----------------------------------------------------------------\n\n");
        receipt.append("Thank you for your purchase!");

        return receipt.toString();
    }
    
    public String generateInvoiceNumber() {
        String uniqueID = UUID.randomUUID().toString();
        String invoiceNumber = uniqueID.substring(0, 8) + ".wan";
        return invoiceNumber;
    }
  
}
