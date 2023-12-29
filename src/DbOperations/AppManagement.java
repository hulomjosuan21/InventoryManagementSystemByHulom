package DbOperations;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class AppManagement extends DbConnection{
    private final Component component;
    
    public AppManagement(Component component){
        this.component = component;
    }  
    
    public static String getCurrentUser(Component p_c){
        String query = "SELECT "+DbColumns.APPCOLUMNS.getValues()[1]+" FROM "+DbTables.APPTABLE.getValue()+" WHERE "
                + DbColumns.APPCOLUMNS.getValues()[0] + " = 1";
        
        try{
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(DbColumns.APPCOLUMNS.getValues()[1]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(p_c, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;           
    }    
    
    public static void setCurrentUser(String _id,Component p_c){
        String query = "UPDATE "+DbTables.APPTABLE.getValue()+" SET "+DbColumns.APPCOLUMNS.getValues()[1]+" = ? WHERE "+DbColumns.APPCOLUMNS.getValues()[0]+" = 1";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            
            prepare.executeUpdate();
            
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(p_c, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
        
}
