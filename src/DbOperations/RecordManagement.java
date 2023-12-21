package DbOperations;

import java.sql.SQLException;

public class RecordManagement extends DbConnection{
    public int countProducts(){
        String query = "SELECT COUNT(*) FROM " + DbTables.INVENTORYTABLE.getValue();
        int count = 0;
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
               count = result.getInt(1);
            }
            result.close();
        } catch (SQLException e) {}
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
        } catch (SQLException e) {}
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
        } catch (SQLException e) {}
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
        } catch (SQLException e) {}
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
        } catch (SQLException e) {}
        return outOfStocks;          
    }
}
