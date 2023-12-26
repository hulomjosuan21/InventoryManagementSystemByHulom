package DbOperations;

import static DbOperations.DbConnection.connection;
import static DbOperations.DbConnection.prepare;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;
    }
    
    
    public void recordSold(int val) {
        String query = "SELECT COUNT(*) FROM " + DbTables.RECORDSTABLE.getValue() + " WHERE " + DbColumns.RECORDSCOLUMNS.getValues()[0] + " = CURDATE()";
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            result.next();
            int count = result.getInt(1);
            result.close();
            prepare.close();

            if (count > 0) {
                // current day - increment the value
                prepare = connection.prepareStatement("UPDATE " + DbTables.RECORDSTABLE.getValue() + " SET " + DbColumns.RECORDSCOLUMNS.getValues()[1] + " = " + DbColumns.RECORDSCOLUMNS.getValues()[1] + " + ? WHERE " + DbColumns.RECORDSCOLUMNS.getValues()[0] + " = CURDATE()");
                prepare.setInt(1, val);
                prepare.executeUpdate();
                prepare.close();
            } else {
                // new day - insert the value
                prepare = connection.prepareStatement("INSERT INTO " + DbTables.RECORDSTABLE.getValue() + " (" + DbColumns.RECORDSCOLUMNS.getValues()[0] + ", " + DbColumns.RECORDSCOLUMNS.getValues()[1] + ") VALUES (CURDATE(), ?)");
                prepare.setInt(1, val);
                prepare.executeUpdate();
                prepare.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public int getSoldToday() {
        String query = "SELECT "+DbColumns.RECORDSCOLUMNS.getValues()[1]+" FROM "+DbTables.RECORDSTABLE.getValue()+" WHERE "+DbColumns.RECORDSCOLUMNS.getValues()[0]+" = CURDATE()";
        int soldValue = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery(query);
            
            if (result.next()) {
                soldValue = result.getInt(DbColumns.RECORDSCOLUMNS.getValues()[1]);
            }
            
            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return soldValue;
    }
    
    public int getOldSold() {
        String query = "SELECT SUM(" + DbColumns.RECORDSCOLUMNS.getValues()[1] + ") FROM " + DbTables.RECORDSTABLE.getValue() + " WHERE DATE(" + DbColumns.RECORDSCOLUMNS.getValues()[0] + ") != CURDATE()";
        int soldValue = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery(query);

            if (result.next()) {
                soldValue = result.getInt(1); 
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return soldValue;
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
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return outOfStocks;          
    }
    
    public double getTotalSalesToday() {
        String query = "SELECT SUM(total) AS totalSales FROM purchasedtable WHERE DATE(purchasedDate) = CURDATE()";
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
    }
    
    public double getTotalSales() {
        String query = "SELECT SUM(total) AS totalSales FROM purchasedtable";
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
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
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }      
    }
}
