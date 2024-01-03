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
    
    public void LoadSalesData(JTable table,Object fromDate, Object toDate) {
        String[] columnsToDisplay = DbColumns.PURCHASEDCOLUMNS.getValues();

    String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + DbTables.PURCHASEDTABLE.getValue() +
                   " WHERE purchasedDate BETWEEN '" + fromDate + "' AND '" + toDate + "'";
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
}
