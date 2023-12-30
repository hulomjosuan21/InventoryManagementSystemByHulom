package DbOperations;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class InventoryManagement extends DbConnection{
    private final Component component;
    
    public InventoryManagement(Component component){
        this.component = component;
    }
    
    public void DisplayInventoryData(JTable table) {
        String[] columnsToDisplay = DbColumns.IVENTORYCOLUMNS.getValues();

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.INVENTORYTABLE.getValue();
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
    
    public void addInventoryValue(String[] values){
        String query = "INSERT INTO "+DbTables.INVENTORYTABLE.getValue()+
                " ("+DbColumns.IVENTORYCOLUMNS.getValues()[1]+","+DbColumns.IVENTORYCOLUMNS.getValues()[2]+","+DbColumns.IVENTORYCOLUMNS.getValues()[3]+
                ","+DbColumns.IVENTORYCOLUMNS.getValues()[4]+","+DbColumns.IVENTORYCOLUMNS.getValues()[5]+","+DbColumns.IVENTORYCOLUMNS.getValues()[6]+") VALUES (?,?,?,?,?,?)";
        
        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            prepare.setObject(3, values[2]); 
            prepare.setObject(4, values[3]); 
            prepare.setObject(5, values[4]); 
            prepare.setObject(6, values[5]);

            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditInventoryValue(Object newVal, int colIdx, Object p_n){
        String query = "UPDATE " + DbTables.INVENTORYTABLE.getValue() + " SET " + DbColumns.IVENTORYCOLUMNS.getValues()[colIdx] + " = ? WHERE " + DbColumns.IVENTORYCOLUMNS.getValues()[0] + " = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, newVal);
            prepare.setObject(2, p_n);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteInventoryRecord(Object ID) {
        String query = "DELETE FROM "+DbTables.INVENTORYTABLE.getValue()+" WHERE "+DbColumns.IVENTORYCOLUMNS.getValues()[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadInventoryData(JTable table, String search){
        String query = "SELECT * FROM "+DbTables.INVENTORYTABLE.getValue()+" WHERE "
                    + String.join(" LIKE ? OR ", DbColumns.IVENTORYCOLUMNS.getValues()) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= DbColumns.IVENTORYCOLUMNS.getValues().length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[DbColumns.IVENTORYCOLUMNS.getValues().length];
                for (int i = 0; i < DbColumns.IVENTORYCOLUMNS.getValues().length; i++) {
                    row[i] = result.getObject(DbColumns.IVENTORYCOLUMNS.getValues()[i]);
                }
                model.addRow(row);
            }
            
            result.close();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public void reduceProductQuantity(Object minusVal, Object p_n){
        String query = "UPDATE "+DbTables.INVENTORYTABLE.getValue()+" SET "+DbColumns.IVENTORYCOLUMNS.getValues()[4]+
                " = "+DbColumns.IVENTORYCOLUMNS.getValues()[4]+" - ? WHERE "+DbColumns.IVENTORYCOLUMNS.getValues()[2]+" = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, minusVal);
            prepare.setObject(2, p_n);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
}
