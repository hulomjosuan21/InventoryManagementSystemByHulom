package DbOperations;

public enum DbColumns {
    IVENTORYCOLUMNS(new String[]{"productID","Category","ProductName",
        "Description","Quantity","RetailPrice","DateOfPurchase"}),
    CATEGORYCOLUMNS(new String[]{"categoryID","categoryName","dateCreated"}),
    APPCOLUMNS(new String[]{"appID","currentUser"}), 
    USERSCOLUMNS(new String[]{"userId","firstname","lastname","username","password",
        "birthdate","gender","profileImgPath","userType"}), RECORDSCOLUMNS(new String[]{"recordDate","sold"}),
        PURCHASEDCOLUMNS(new String[]{"invoiceNumber","product","discountPercent","quantity","subtotal","total","purchasedDate"}),
        REPORTOPTIONS(new String[]{"Sales Report", "Inventory Report", "Out of Stocks", "Top Sales", "Top Sellers"});
    
    private final String[] values;
    
    DbColumns(String[] values){
        this.values = values;
    }
    
    public String[] getValues(){
        return values;
    }
}
