package DbOperations;

import static DbOperations.DbConnection.connection;
import static DbOperations.DbConnection.prepare;
import static DbOperations.DbConnection.result;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RecordManagement extends DbConnection{
    private final Component component;
    
    public RecordManagement(Component component){
        this.component = component;
    }
    
    public int countProducts(){
        String query = "SELECT COUNT(*) FROM " + DbTables.INVENTORYTABLE.getValue();
        int count = 0;
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
               count = result.getInt(1);
            }
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return count;
    }
    
    public int getOutOfStocks(){
        String query = "SELECT COUNT(*) FROM "+DbTables.INVENTORYTABLE.getValue()+" WHERE "+DbColumns.IVENTORYCOLUMNS.getValues()[4]+" = 0";
        int outOfStocks = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();

            if (result.next()) {
                outOfStocks = result.getInt(1); 
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return outOfStocks;          
    }
    
    public double getTotalSalesToday() {
        String query = "SELECT SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[5]+") AS totalSales FROM "+DbTables.PURCHASEDTABLE+" WHERE DATE("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") = CURDATE()";
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
    }
    
    public double getTotalSales() {
        String query = "SELECT SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[5]+") AS totalSales FROM "+DbTables.PURCHASEDTABLE;
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
    }
    
    public int getProductSoldToday() {
        String query = "SELECT SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[3]+") AS totalquantity FROM "+DbTables.PURCHASEDTABLE+" WHERE DATE("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") = CURDATE()";
        int totalProduct = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                totalProduct = result.getInt("totalquantity");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return totalProduct;
    }
    
    public int getProductSold() {
        String query = "SELECT SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[3]+") AS totalquantity FROM "+DbTables.PURCHASEDTABLE+" WHERE DATE("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") != CURDATE()";
        int totalProduct = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                totalProduct = result.getInt("totalquantity");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return totalProduct;
    }

    public void recordPurchase(Object invoiceNum, Object item, Object discount, Object quantity, Object subtotal, Object total, Object date){
        String query = "INSERT INTO "+DbTables.PURCHASEDTABLE.getValue()+" ("+DbColumns.PURCHASEDCOLUMNS.getValues()[0]+
                ", "+DbColumns.PURCHASEDCOLUMNS.getValues()[1]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[2]+
                ", "+DbColumns.PURCHASEDCOLUMNS.getValues()[3]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[4]+
                ", "+DbColumns.PURCHASEDCOLUMNS.getValues()[5]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") VALUES (?,?,?,?,?,?,?)";
        
        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, invoiceNum);
            prepare.setObject(2, item);
            prepare.setObject(3, discount);
            prepare.setObject(4, quantity);
            prepare.setObject(5, subtotal);
            prepare.setObject(6, total);
            prepare.setObject(7, date);
            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }      
    }
    
    public void loadPurchasedData(JTable table) {
        String[] columnsToDisplay = {"invoiceNumber", "product", "discountPercent","quantity","subtotal", "total","purchasedDate"};

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.PURCHASEDTABLE.getValue();
        try {
            prepare = connection.prepareStatement(query);

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
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void searchPurchasedData(JTable table, String search){
        String query = "SELECT * FROM "+DbTables.PURCHASEDTABLE.getValue()+" WHERE "
                    + String.join(" LIKE ? OR ", DbColumns.PURCHASEDCOLUMNS.getValues()) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= DbColumns.PURCHASEDCOLUMNS.getValues().length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[DbColumns.PURCHASEDCOLUMNS.getValues().length];
                for (int i = 0; i < DbColumns.PURCHASEDCOLUMNS.getValues().length; i++) {
                    row[i] = result.getObject(DbColumns.PURCHASEDCOLUMNS.getValues()[i]);
                }
                model.addRow(row);
            }
            
            result.close();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }        
    }
}
