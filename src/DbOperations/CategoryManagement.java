package DbOperations;

import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CategoryManagement extends DbConnection{
    private final Component component;
    
    public CategoryManagement(Component component){
        this.component = component;
    }
    
    public DefaultTableModel DisplayCategoryData(){
        String query = "SELECT * FROM " + DbTables.CATEGORYTABLE.getValue();

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Category ID","Category Name","Date Added"});
        try {
            result = statement.executeQuery(query);
            metaData = result.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (result.next()) {
                Object[] rowData = new Object[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    rowData[i - 1] = result.getObject(i);
                }
                model.addRow(rowData);
            }

            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }
    
    public void addCategoryValue(String[] values){
        String query = "INSERT INTO "+DbTables.CATEGORYTABLE.getValue()+" ("+DbColumns.CATEGORYCOLUMNS.getValues()[1]+","+DbColumns.CATEGORYCOLUMNS.getValues()[2]+") VALUES (?,?)";
       
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditCategoryValue(Object newVal, int colIdx, Object ID){
        String query = "UPDATE "+DbTables.CATEGORYTABLE.getValue()+" SET "+DbColumns.CATEGORYCOLUMNS.getValues()[colIdx]+" = ? WHERE ("+DbColumns.CATEGORYCOLUMNS.getValues()[0]+" = ?)";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, newVal);
            prepare.setObject(2, ID);
            
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteCategoryRecord(Object ID){
        String query = "DELETE FROM "+DbTables.CATEGORYTABLE.getValue()+" WHERE "+DbColumns.CATEGORYCOLUMNS.getValues()[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String[] AddElementToComboBox(){
        String query = "SELECT " + DbColumns.CATEGORYCOLUMNS.getValues()[1] + " FROM " + DbTables.CATEGORYTABLE;
        
        List<String> getVal = new ArrayList<>();
        
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
                getVal.add(result.getString(DbColumns.CATEGORYCOLUMNS.getValues()[1]));
            }
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return getVal.toArray(new String[0]);
    } 
    
    public void loadCategoryData(JTable table, String search){
        String query = "SELECT * FROM "+DbTables.CATEGORYTABLE.getValue()+" WHERE "
                    + String.join(" LIKE ? OR ", DbColumns.CATEGORYCOLUMNS.getValues()) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= DbColumns.CATEGORYCOLUMNS.getValues().length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[DbColumns.CATEGORYCOLUMNS.getValues().length];
                for (int i = 0; i < DbColumns.CATEGORYCOLUMNS.getValues().length; i++) {
                    row[i] = result.getObject(DbColumns.CATEGORYCOLUMNS.getValues()[i]);
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
