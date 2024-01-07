
import DbOperations.AppManagement;
import DbOperations.DbConnection;
import DbOperations.UserManagement;
import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.JOptionPane;
import org.opencv.core.Core;

public class MainClass {
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        IntelliJTheme.setup(Application.class.getResourceAsStream("/theme_eclipse.theme.json"));
        DbConnection dbConnection = new DbConnection();

        if (dbConnection.isDatabaseConnected()) {
            String value = AppManagement.getCurrentUser(new Application());
            boolean checkUser = new UserManagement(new Application()).checkCurrentUser(value);
            
            java.awt.EventQueue.invokeLater(() -> {
                if(!checkUser){
                    new LoginFrame().setVisible(true);
                }else{
                    new Application().setVisible(true);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Error: No database connected!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
}
