package DbOperations;

public enum DbColumns {
    IVENTORYCOLUMNS(new String[]{"productID","Category","ProductName",
        "Description","Quantity","RetailPrice","DateOfPurchase"}),
    CATEGORYCOLUMNS(new String[]{"categoryID","categoryName","dateCreated"}),
    APPCOLUMNS(new String[]{"appID","countUsers","currentUser"}), 
    USERSCOLUMNS(new String[]{"userId","firstname","lastname","username","password",
        "birthdate","gender","profileImgPath","userType"}), RECORDSCOLUMNS(new String[]{"recordDate","sold"});
    
    private final String[] values;
    
    DbColumns(String[] values){
        this.values = values;
    }
    
    public String[] getValues(){
        return values;
    }
}
