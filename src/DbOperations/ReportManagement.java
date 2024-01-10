package DbOperations;

import static DbOperations.DbConnection.connection;
import static DbOperations.DbConnection.prepare;
import static DbOperations.DbConnection.result;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReportManagement extends DbConnection{
    private final Component component;
    
    public ReportManagement(Component component){
        this.component = component;
    }
    
    public void LoadSalesData(JTable table) {
        String[] columnsToDisplay = DbColumns.PURCHASEDCOLUMNS.getValues();

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
    
    public void LoadSalesData(JTable table,Object fromDate, Object toDate) {
        String[] columnsToDisplay = DbColumns.PURCHASEDCOLUMNS.getValues();

         String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.PURCHASEDTABLE.getValue() +
                   " WHERE "+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+" BETWEEN '" + fromDate + "' AND '" + toDate + "'";
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
    
    public void LoadOutOfStocks(JTable table){
        String[] columnsToDisplay = DbColumns.IVENTORYCOLUMNS.getValues();

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.INVENTORYTABLE.getValue() +
          " WHERE " + DbColumns.IVENTORYCOLUMNS.getValues()[4] + " = 0";
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
    
    public void LoadOutOfStocks(JTable table,Object fromDate, Object toDate){
        String[] columnsToDisplay = DbColumns.IVENTORYCOLUMNS.getValues();

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.INVENTORYTABLE.getValue() +
                " WHERE " + DbColumns.IVENTORYCOLUMNS.getValues()[4] + " = 0 AND "+DbColumns.IVENTORYCOLUMNS.getValues()[6]+" BETWEEN '" + fromDate + "' AND '" + toDate + "'";

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
    
    public void LoadTopSales(JTable table){
        String[] columnsToDisplay = {DbColumns.PURCHASEDCOLUMNS.getValues()[1],DbColumns.PURCHASEDCOLUMNS.getValues()[3],
        DbColumns.PURCHASEDCOLUMNS.getValues()[5],DbColumns.PURCHASEDCOLUMNS.getValues()[6]};
        
        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.PURCHASEDTABLE.getValue() +
                " ORDER BY " + DbColumns.PURCHASEDCOLUMNS.getValues()[3] + " DESC";
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
    
    public void LoadTopSales(JTable table,Object fromDate, Object toDate){
        String[] columnsToDisplay = {DbColumns.PURCHASEDCOLUMNS.getValues()[1],DbColumns.PURCHASEDCOLUMNS.getValues()[3],
        DbColumns.PURCHASEDCOLUMNS.getValues()[5],DbColumns.PURCHASEDCOLUMNS.getValues()[6]};
        
        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.PURCHASEDTABLE.getValue() +
                " WHERE " + DbColumns.PURCHASEDCOLUMNS.getValues()[6] + " BETWEEN '" + fromDate + "' AND '" + toDate + "'" +
                " ORDER BY " + DbColumns.PURCHASEDCOLUMNS.getValues()[3] + " DESC";

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
    
    public void LoadTopSellers(JTable table) {
        String query = "SELECT "+DbColumns.PURCHASEDCOLUMNS.getValues()[7]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[8]+", CONCAT(YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), '-', LPAD(MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), 2, '0')) AS month_year, SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[3]+") as total_quantity " +
                       "FROM "+DbTables.PURCHASEDTABLE.getValue()+" " +
                       "GROUP BY "+DbColumns.PURCHASEDCOLUMNS.getValues()[7]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[8]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+", YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") " +
                       "ORDER BY YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") DESC, MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") DESC, total_quantity DESC";

        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (result.next()) {
                Object[] row = {
                    result.getString(DbColumns.PURCHASEDCOLUMNS.getValues()[7]),
                    result.getString(DbColumns.PURCHASEDCOLUMNS.getValues()[8]),
                    result.getInt("total_quantity"),
                    result.getString("month_year")
                };
                model.addRow(row);
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void LoadTopSellers(JTable table, Object fromDate, Object toDate) {
        String query = "SELECT "+DbColumns.PURCHASEDCOLUMNS.getValues()[7]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[8]+", CONCAT(YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), '-', LPAD(MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), 2, '0')) AS month_year, SUM("+DbColumns.PURCHASEDCOLUMNS.getValues()[3]+") as total_quantity " +
                       "FROM "+DbTables.PURCHASEDTABLE.getValue()+" " +
                       "WHERE "+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+" BETWEEN ? AND ? " +
                       "GROUP BY "+DbColumns.PURCHASEDCOLUMNS.getValues()[7]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[8]+", "+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+", YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+"), MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") " +
                       "ORDER BY YEAR("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") DESC, MONTH("+DbColumns.PURCHASEDCOLUMNS.getValues()[6]+") DESC, total_quantity DESC";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, fromDate);
            prepare.setObject(2, toDate); 
            result = prepare.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (result.next()) {
                Object[] row = {
                    result.getString(DbColumns.PURCHASEDCOLUMNS.getValues()[7]),
                    result.getString(DbColumns.PURCHASEDCOLUMNS.getValues()[8]),
                    result.getInt("total_quantity"),
                    result.getString("month_year"),
                };
                model.addRow(row);
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
