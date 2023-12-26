package DbOperations;

public enum DbTables {
    INVENTORYTABLE("inventorytable"), 
    CATEGORYTABLE("categorytable"), 
    APPTABLE("apptable"), 
    USERTABLE("userstable"),
    RECORDSTABLE("recordstable"),PURCHASEDTABLE("purchasedtable");
    
    private final String value;
    
    DbTables(String value){
        this.value =  value;
    }
    
    public String getValue(){
        return value;
    }
}