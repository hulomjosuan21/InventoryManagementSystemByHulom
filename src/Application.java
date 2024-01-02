import DbOperations.*;
import assets.*;
import customComponents.*;
import com.formdev.flatlaf.IntelliJTheme;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.JTable;
import org.opencv.core.Core;

//@author Josuan
public final class Application extends javax.swing.JFrame {
    private final DbConnection DBC = new DbConnection();
    private final InventoryManagement IMT = new InventoryManagement(this);
    private final CategoryManagement CMT = new CategoryManagement(this);
    private final RecordManagement RMT = new RecordManagement(this);
    private final AppManagement AMT = new AppManagement(this);
    private final UserManagement UMT = new UserManagement(this);
    private final SalesManagement SMT = new SalesManagement(this);
    
    private static File get_imgFile1;
    
    public Application() {
        initComponents();
        
        Image appIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/appLogo.png"));
        this.setIconImage(appIcon);

        sideBarMenu();
        close_min_max_Screen(closeBtn,minimizeBtn,maximizeBtn);
        close_min_max_Screen(closeBtn1,minimizeBtn1,maximizeBtn1);
        inventoryInit();
        categoryInit();
        userInit();
        salesInit();
        adminInit();
        recordInit();
        returnItemInit();
        if (this.getExtendedState() == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.NORMAL);
        } else {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        }
        
        ImageManagement.setupFileDragAndDrop(uploadPanel1, new Color(204,204,204),imageAvatar2);
        
    }
    
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        IntelliJTheme.setup(Application.class.getResourceAsStream("/theme_eclipse.theme.json"));
        DbConnection dbConnection = new DbConnection();

        if (dbConnection.isDatabaseConnected()) {
            String value = AppManagement.getCurrentUser(new Application());
            boolean checkUser = new UserManagement(new Application()).checkCurrentUser(value);
            
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    if(!checkUser){
                        new LoginFrame().setVisible(true);
                    }else{
                        new Application().setVisible(true);
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Error: No database connected!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void inventoryInit(){
        IMT.DisplayInventoryData(inventoryTable);
        im4.setModel(new DefaultComboBoxModel(CMT.AddElementToComboBox()));
        inventoryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(CMT.AddElementToComboBox())));
        inventoryTable.getColumnModel().getColumn(4).setCellEditor(new Utilities.IntCustomJSpinner());
        inventoryTable.getColumnModel().getColumn(5).setCellEditor(new Utilities.DoubleCustomJSpinner());            
    }
    
    public void categoryInit(){
        CMT.DisplayCategoryData(categoryTable);      
    }
    
    public void recordInit(){
        totalProductsLabel.setText(RMT.countProducts()+"");
        soldOldLabel.setText(RMT.getProductSold()+"");
        soldTotayLabel.setText(RMT.getProductSoldToday()+"");
        outStockLabel.setText(RMT.getOutOfStocks()+"");
        String formatted1 = String.format("%.1f", RMT.getTotalSalesToday());
        String formatted2 = String.format("%.1f", RMT.getTotalSales());
        todaysalesLabel.setText(Helper.currency+" "+formatted1);
        totalSalesLabel.setText(Helper.currency+" "+formatted2);      
        inventoryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(CMT.AddElementToComboBox())));
        salesCategoryComboBox.setModel(new DefaultComboBoxModel(CMT.AddElementToComboBox()));
    }
    
    public void userInit(){
        String get_id = AppManagement.getCurrentUser(this);
        String get_image = UMT.getImagePath(get_id);
        String get_username = UMT.getUName(get_id);
        String get_fname = UMT.getFName(get_id);
        String get_lname = UMT.getLName(get_id);
        String get_fullName = get_fname+" "+get_lname;
        short get_usertype = UMT.getUserType(get_id);
        
        ImageManagement.setImageToAvatar(imageAvatar1, get_image);
        usernameLabel.setText(get_username);
        fullnameLabel.setText(Utilities.capitalizeEachWord(get_fullName));
        String get_gender = UMT.getGender(get_id);
        
        switch (get_usertype) {
            case 0:
                switchPanel(menuLayeredPane,menuPanel1);
                adminSettingsLabel.setEnabled(true);
                adminSettingsLabel.setVisible(true);
                ivnLabel.setVisible(true);
                invoiceTextField.setVisible(true);
                break;
            case 1:
                switchPanel(menuLayeredPane,menuPanel2);
                adminSettingsLabel.setEnabled(false);
                adminSettingsLabel.setVisible(false);
                panelRound19.setVisible(false);
                ivnLabel.setVisible(false);
                invoiceTextField.setVisible(false);
                break;
            default:
                switchPanel(menuLayeredPane,menuPanel2);
                adminSettingsLabel.setEnabled(false);
                adminSettingsLabel.setVisible(false);   
                panelRound19.setVisible(false);
                ivnLabel.setVisible(false);
                invoiceTextField.setVisible(false);
                break;
        }
        try{          
            if(get_gender.equals(Helper.listOfGender[0])){
                genderLabel.setIcon(new ImageIcon("src/icons/male.png"));
            }else if(get_gender.equals(Helper.listOfGender[1])){
                genderLabel.setIcon(new ImageIcon("src/icons/female.png"));
            }else{
                genderLabel.setIcon(new ImageIcon("src/icons/nogender.png"));
            }
        }catch(NullPointerException e){
        }
    }
    
    public void adminInit(){
        UMT.DisplayUserData(usersTable);
        usersTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox(Helper.listOfGender)));
    }
    
    public void salesInit(){
        salesCategoryComboBox.setModel(new DefaultComboBoxModel(CMT.AddElementToComboBox()));
        
        SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 100, 10);
        discountSpinner.setModel(spinnerModel1);
    }
    
    public void returnItemInit(){
        RMT.loadPurchasedData(returnitemTable);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainLayeredPane = new javax.swing.JLayeredPane();
        mainPanel = new javax.swing.JPanel();
        contentLayeredPane = new javax.swing.JLayeredPane();
        dashboardPanel = new customComponents.PanelRound();
        d1 = new customComponents.PanelRound();
        panelRound2 = new customComponents.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        totalProductsLabel = new javax.swing.JLabel();
        d2 = new customComponents.PanelRound();
        panelRound4 = new customComponents.PanelRound();
        jLabel2 = new javax.swing.JLabel();
        soldOldLabel = new javax.swing.JLabel();
        d3 = new customComponents.PanelRound();
        panelRound6 = new customComponents.PanelRound();
        jLabel4 = new javax.swing.JLabel();
        totalSalesLabel = new javax.swing.JLabel();
        d4 = new customComponents.PanelRound();
        panelRound7 = new customComponents.PanelRound();
        jLabel5 = new javax.swing.JLabel();
        outStockLabel = new javax.swing.JLabel();
        d5 = new customComponents.PanelRound();
        panelRound11 = new customComponents.PanelRound();
        jLabel6 = new javax.swing.JLabel();
        soldTotayLabel = new javax.swing.JLabel();
        d6 = new customComponents.PanelRound();
        panelRound12 = new customComponents.PanelRound();
        jLabel7 = new javax.swing.JLabel();
        todaysalesLabel = new javax.swing.JLabel();
        usersPanel = new customComponents.PanelRound();
        panelRound5 = new customComponents.PanelRound();
        imageAvatar1 = new customComponents.ImageAvatar();
        usernameLabel = new javax.swing.JLabel();
        panelRound8 = new customComponents.PanelRound();
        panelRound1 = new customComponents.PanelRound();
        fullnameLabel = new javax.swing.JLabel();
        genderLabel = new javax.swing.JLabel();
        panelRound23 = new customComponents.PanelRound();
        panelRound9 = new customComponents.PanelRound();
        panelRound19 = new customComponents.PanelRound();
        adminSettingsLabel = new javax.swing.JLabel();
        inventoryPanel = new customComponents.PanelRound();
        jScrollPane1 = new javax.swing.JScrollPane();
        inventoryTable = new javax.swing.JTable();
        panelRound13 = new customComponents.PanelRound();
        im1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        im2 = new javax.swing.JTextField();
        im3 = new javax.swing.JSpinner();
        im4 = new javax.swing.JComboBox<>();
        im6 = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        im5 = new javax.swing.JSpinner();
        inventoryAddBtn = new javax.swing.JButton();
        inventoryEditBtn = new javax.swing.JButton();
        inventoryDeleteBtn = new javax.swing.JButton();
        inventorySearchBar = new customComponents.TextFieldWithIcon();
        categoryPanel = new customComponents.PanelRound();
        jScrollPane2 = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        panelRound3 = new customComponents.PanelRound();
        jLabel20 = new javax.swing.JLabel();
        categoryInput = new javax.swing.JTextField();
        categoryAddBtn = new javax.swing.JButton();
        categoryEditBtn = new javax.swing.JButton();
        categoryDeleteBtn = new javax.swing.JButton();
        categorySearchBar = new customComponents.TextFieldWithIcon();
        salesPanel = new customComponents.PanelRound();
        panelRound10 = new customComponents.PanelRound();
        jScrollPane3 = new javax.swing.JScrollPane();
        recieptTextArea = new javax.swing.JTextArea();
        payBtn = new javax.swing.JButton();
        printBtn = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        getTotalTextField = new javax.swing.JTextField();
        receivedTextField = new javax.swing.JTextField();
        getBalanceTextField = new javax.swing.JTextField();
        panelRound14 = new customComponents.PanelRound();
        jLabel24 = new javax.swing.JLabel();
        salesCategoryComboBox = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        itemNameTextField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        dateCHooser = new com.toedter.calendar.JDateChooser();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        priceSpinner = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        quantitySpinner = new javax.swing.JSpinner();
        jLabel29 = new javax.swing.JLabel();
        discountSpinner = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        totalSpinner = new javax.swing.JSpinner();
        jScrollPane5 = new javax.swing.JScrollPane();
        salesTable1 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        salesTable2 = new javax.swing.JTable();
        addCartBtn = new javax.swing.JButton();
        removeCartBtn = new javax.swing.JButton();
        invoiceTextField = new javax.swing.JTextField();
        ivnLabel = new javax.swing.JLabel();
        returnItemPanel = new customComponents.PanelRound();
        panelRound21 = new customComponents.PanelRound();
        jLabel40 = new javax.swing.JLabel();
        invoicenumtextField = new javax.swing.JTextField();
        returnItemBtn = new javax.swing.JButton();
        returnItemSearchBar = new customComponents.TextFieldWithIcon();
        jScrollPane7 = new javax.swing.JScrollPane();
        returnitemTable = new javax.swing.JTable();
        priceListPanel = new customComponents.PanelRound();
        reportPanel = new customComponents.PanelRound();
        settingsPanel = new customComponents.PanelRound();
        settingsScrillPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        panelRound22 = new customComponents.PanelRound();
        jLabel10 = new javax.swing.JLabel();
        getPasswordPasswordField1 = new javax.swing.JPasswordField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        getConfirmPasswordPasswordField1 = new javax.swing.JPasswordField();
        addCartBtn1 = new javax.swing.JButton();
        headerPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        close_min_max_Panel = new javax.swing.JPanel();
        closeBtn = new customComponents.CircleButton();
        minimizeBtn = new customComponents.CircleButton();
        maximizeBtn = new customComponents.CircleButton();
        menuLayeredPane = new javax.swing.JLayeredPane();
        menuPanel1 = new customComponents.PanelRound();
        logoutBack = new customComponents.PanelRound();
        logoutLabel = new javax.swing.JLabel();
        settingsBack = new customComponents.PanelRound();
        settingsLabel = new javax.swing.JLabel();
        reportBack = new customComponents.PanelRound();
        reportLabel = new javax.swing.JLabel();
        priceListBack = new customComponents.PanelRound();
        priceListLabel = new javax.swing.JLabel();
        returnItemBack = new customComponents.PanelRound();
        returnItemLabel = new javax.swing.JLabel();
        salesBack = new customComponents.PanelRound();
        salesLabel = new javax.swing.JLabel();
        categoryBack = new customComponents.PanelRound();
        categoryLabel = new javax.swing.JLabel();
        inventoryBack = new customComponents.PanelRound();
        inventoryLabel = new javax.swing.JLabel();
        usersBack = new customComponents.PanelRound();
        usersLabel = new javax.swing.JLabel();
        dashboardBack = new customComponents.PanelRound();
        dashboardLabel = new javax.swing.JLabel();
        menuPanel2 = new customComponents.PanelRound();
        dashboardBack1 = new customComponents.PanelRound();
        dashboardLabel1 = new javax.swing.JLabel();
        salesBack1 = new customComponents.PanelRound();
        salesLabel1 = new javax.swing.JLabel();
        priceListBack1 = new customComponents.PanelRound();
        priceListLabel1 = new javax.swing.JLabel();
        returnItemBack1 = new customComponents.PanelRound();
        returnItemLabel1 = new javax.swing.JLabel();
        logoutBack1 = new customComponents.PanelRound();
        logoutLabel1 = new javax.swing.JLabel();
        usersBack2 = new customComponents.PanelRound();
        usersLabel2 = new javax.swing.JLabel();
        settingsBack2 = new customComponents.PanelRound();
        settingsLabel2 = new javax.swing.JLabel();
        adminPanel = new customComponents.PanelRound();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelRound15 = new customComponents.PanelRound();
        jScrollPane4 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        adminDeleteBtn = new javax.swing.JButton();
        promoteBtn = new javax.swing.JButton();
        demoteBtn = new javax.swing.JButton();
        adminEditBtn = new javax.swing.JButton();
        panelRound16 = new customComponents.PanelRound();
        panelRound17 = new customComponents.PanelRound();
        imageAvatar2 = new customComponents.ImageAvatar();
        uploadPanel1 = new customComponents.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        uploadImageLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        panelRound18 = new customComponents.PanelRound();
        jLabel21 = new javax.swing.JLabel();
        getFNameTextField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        getLNameTextField = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        getUNameTextField = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        getPasswordPasswordField = new javax.swing.JPasswordField();
        jLabel37 = new javax.swing.JLabel();
        getConfirmPasswordPasswordField = new javax.swing.JPasswordField();
        jLabel38 = new javax.swing.JLabel();
        getGenderComboBox = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        getBirthDateDateChooser = new com.toedter.calendar.JDateChooser();
        addUserBtn = new javax.swing.JButton();
        termServiceCheckBox = new javax.swing.JCheckBox();
        showPasswordCheckBox = new javax.swing.JCheckBox();
        panelRound20 = new customComponents.PanelRound();
        testBtn1 = new javax.swing.JButton();
        close_min_max_Panel1 = new javax.swing.JPanel();
        closeBtn1 = new customComponents.CircleButton();
        minimizeBtn1 = new customComponents.CircleButton();
        maximizeBtn1 = new customComponents.CircleButton();
        backLabel = new javax.swing.JLabel();
        titleLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainLayeredPane.setLayout(new java.awt.CardLayout());

        contentLayeredPane.setLayout(new java.awt.CardLayout());

        dashboardPanel.setBackground(new java.awt.Color(224, 231, 255));
        dashboardPanel.setRoundTopLeft(50);

        d1.setBackground(new java.awt.Color(165, 180, 252));
        d1.setRoundBottomLeft(25);
        d1.setRoundBottomRight(25);
        d1.setRoundTopLeft(25);
        d1.setRoundTopRight(25);

        panelRound2.setBackground(new java.awt.Color(99, 102, 241));
        panelRound2.setRoundBottomLeft(25);
        panelRound2.setRoundBottomRight(25);
        panelRound2.setRoundTopLeft(25);
        panelRound2.setRoundTopRight(25);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Total Products");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        totalProductsLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        totalProductsLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalProductsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalProductsLabel.setText("0");

        javax.swing.GroupLayout d1Layout = new javax.swing.GroupLayout(d1);
        d1.setLayout(d1Layout);
        d1Layout.setHorizontalGroup(
            d1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(d1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalProductsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        d1Layout.setVerticalGroup(
            d1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalProductsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        d2.setBackground(new java.awt.Color(165, 180, 252));
        d2.setRoundBottomLeft(25);
        d2.setRoundBottomRight(25);
        d2.setRoundTopLeft(25);
        d2.setRoundTopRight(25);

        panelRound4.setBackground(new java.awt.Color(99, 102, 241));
        panelRound4.setRoundBottomLeft(25);
        panelRound4.setRoundBottomRight(25);
        panelRound4.setRoundTopLeft(25);
        panelRound4.setRoundTopRight(25);

        jLabel2.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Products Sold (old)");

        javax.swing.GroupLayout panelRound4Layout = new javax.swing.GroupLayout(panelRound4);
        panelRound4.setLayout(panelRound4Layout);
        panelRound4Layout.setHorizontalGroup(
            panelRound4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound4Layout.setVerticalGroup(
            panelRound4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        soldOldLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        soldOldLabel.setForeground(new java.awt.Color(255, 255, 255));
        soldOldLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        soldOldLabel.setText("0");

        javax.swing.GroupLayout d2Layout = new javax.swing.GroupLayout(d2);
        d2.setLayout(d2Layout);
        d2Layout.setHorizontalGroup(
            d2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(d2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(soldOldLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        d2Layout.setVerticalGroup(
            d2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(179, Short.MAX_VALUE))
            .addGroup(d2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d2Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(soldOldLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        d3.setBackground(new java.awt.Color(165, 180, 252));
        d3.setRoundBottomLeft(25);
        d3.setRoundBottomRight(25);
        d3.setRoundTopLeft(25);
        d3.setRoundTopRight(25);

        panelRound6.setBackground(new java.awt.Color(99, 102, 241));
        panelRound6.setRoundBottomLeft(25);
        panelRound6.setRoundBottomRight(25);
        panelRound6.setRoundTopLeft(25);
        panelRound6.setRoundTopRight(25);

        jLabel4.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Total Sales");

        javax.swing.GroupLayout panelRound6Layout = new javax.swing.GroupLayout(panelRound6);
        panelRound6.setLayout(panelRound6Layout);
        panelRound6Layout.setHorizontalGroup(
            panelRound6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound6Layout.setVerticalGroup(
            panelRound6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        totalSalesLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        totalSalesLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalSalesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalSalesLabel.setText("0");

        javax.swing.GroupLayout d3Layout = new javax.swing.GroupLayout(d3);
        d3.setLayout(d3Layout);
        d3Layout.setHorizontalGroup(
            d3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(d3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(totalSalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        d3Layout.setVerticalGroup(
            d3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(179, Short.MAX_VALUE))
            .addGroup(d3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d3Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(totalSalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        d4.setBackground(new java.awt.Color(165, 180, 252));
        d4.setRoundBottomLeft(25);
        d4.setRoundBottomRight(25);
        d4.setRoundTopLeft(25);
        d4.setRoundTopRight(25);

        panelRound7.setBackground(new java.awt.Color(99, 102, 241));
        panelRound7.setRoundBottomLeft(25);
        panelRound7.setRoundBottomRight(25);
        panelRound7.setRoundTopLeft(25);
        panelRound7.setRoundTopRight(25);

        jLabel5.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Out of Stocks");

        javax.swing.GroupLayout panelRound7Layout = new javax.swing.GroupLayout(panelRound7);
        panelRound7.setLayout(panelRound7Layout);
        panelRound7Layout.setHorizontalGroup(
            panelRound7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound7Layout.setVerticalGroup(
            panelRound7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        outStockLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        outStockLabel.setForeground(new java.awt.Color(255, 255, 255));
        outStockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outStockLabel.setText("0");

        javax.swing.GroupLayout d4Layout = new javax.swing.GroupLayout(d4);
        d4.setLayout(d4Layout);
        d4Layout.setHorizontalGroup(
            d4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(d4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(outStockLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        d4Layout.setVerticalGroup(
            d4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
            .addGroup(d4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d4Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(outStockLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        d5.setBackground(new java.awt.Color(165, 180, 252));
        d5.setRoundBottomLeft(25);
        d5.setRoundBottomRight(25);
        d5.setRoundTopLeft(25);
        d5.setRoundTopRight(25);

        panelRound11.setBackground(new java.awt.Color(99, 102, 241));
        panelRound11.setRoundBottomLeft(25);
        panelRound11.setRoundBottomRight(25);
        panelRound11.setRoundTopLeft(25);
        panelRound11.setRoundTopRight(25);

        jLabel6.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Products Sold (today)");

        javax.swing.GroupLayout panelRound11Layout = new javax.swing.GroupLayout(panelRound11);
        panelRound11.setLayout(panelRound11Layout);
        panelRound11Layout.setHorizontalGroup(
            panelRound11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound11Layout.setVerticalGroup(
            panelRound11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        soldTotayLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        soldTotayLabel.setForeground(new java.awt.Color(255, 255, 255));
        soldTotayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        soldTotayLabel.setText("0");

        javax.swing.GroupLayout d5Layout = new javax.swing.GroupLayout(d5);
        d5.setLayout(d5Layout);
        d5Layout.setHorizontalGroup(
            d5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(d5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(soldTotayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        d5Layout.setVerticalGroup(
            d5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
            .addGroup(d5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d5Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(soldTotayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        d6.setBackground(new java.awt.Color(165, 180, 252));
        d6.setRoundBottomLeft(25);
        d6.setRoundBottomRight(25);
        d6.setRoundTopLeft(25);
        d6.setRoundTopRight(25);

        panelRound12.setBackground(new java.awt.Color(99, 102, 241));
        panelRound12.setRoundBottomLeft(25);
        panelRound12.setRoundBottomRight(25);
        panelRound12.setRoundTopLeft(25);
        panelRound12.setRoundTopRight(25);

        jLabel7.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Today's Sales");

        javax.swing.GroupLayout panelRound12Layout = new javax.swing.GroupLayout(panelRound12);
        panelRound12.setLayout(panelRound12Layout);
        panelRound12Layout.setHorizontalGroup(
            panelRound12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound12Layout.setVerticalGroup(
            panelRound12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        todaysalesLabel.setFont(new java.awt.Font("Calibri", 1, 60)); // NOI18N
        todaysalesLabel.setForeground(new java.awt.Color(255, 255, 255));
        todaysalesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        todaysalesLabel.setText("0");

        javax.swing.GroupLayout d6Layout = new javax.swing.GroupLayout(d6);
        d6.setLayout(d6Layout);
        d6Layout.setHorizontalGroup(
            d6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(d6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(todaysalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        d6Layout.setVerticalGroup(
            d6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(d6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
            .addGroup(d6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(d6Layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(todaysalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout dashboardPanelLayout = new javax.swing.GroupLayout(dashboardPanel);
        dashboardPanel.setLayout(dashboardPanelLayout);
        dashboardPanelLayout.setHorizontalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(d1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(53, 53, 53)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(d5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(53, 53, 53)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(d3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(53, 53, 53))
        );
        dashboardPanelLayout.setVerticalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(d3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(54, 54, 54)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(d4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(d6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );

        contentLayeredPane.add(dashboardPanel, "card2");

        usersPanel.setBackground(new java.awt.Color(224, 231, 255));
        usersPanel.setRoundTopLeft(50);

        panelRound5.setBackground(new java.awt.Color(165, 180, 252));
        panelRound5.setRoundBottomLeft(25);
        panelRound5.setRoundBottomRight(25);
        panelRound5.setRoundTopLeft(50);
        panelRound5.setRoundTopRight(25);

        imageAvatar1.setForeground(new java.awt.Color(67, 56, 202));
        imageAvatar1.setBorderSize(2);
        imageAvatar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nullProfile.jpg"))); // NOI18N
        imageAvatar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageAvatar1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                imageAvatar1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                imageAvatar1MouseExited(evt);
            }
        });

        usernameLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setText("Admin");
        usernameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usernameLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelRound5Layout = new javax.swing.GroupLayout(panelRound5);
        panelRound5.setLayout(panelRound5Layout);
        panelRound5Layout.setHorizontalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound5Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(imageAvatar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(28, 28, 28))
            .addGroup(panelRound5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound5Layout.setVerticalGroup(
            panelRound5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(imageAvatar1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLabel)
                .addContainerGap(343, Short.MAX_VALUE))
        );

        panelRound8.setBackground(new java.awt.Color(165, 180, 252));
        panelRound8.setRoundBottomLeft(25);
        panelRound8.setRoundBottomRight(25);
        panelRound8.setRoundTopLeft(25);
        panelRound8.setRoundTopRight(25);

        panelRound1.setBackground(new java.awt.Color(99, 102, 241));
        panelRound1.setRoundBottomLeft(25);
        panelRound1.setRoundBottomRight(25);
        panelRound1.setRoundTopLeft(25);
        panelRound1.setRoundTopRight(25);

        fullnameLabel.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        fullnameLabel.setForeground(new java.awt.Color(255, 255, 255));
        fullnameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fullnameLabel.setText("Example Name");

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fullnameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fullnameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
        );

        genderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        genderLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nogender.png"))); // NOI18N
        genderLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genderLabelMouseClicked(evt);
            }
        });

        panelRound23.setBackground(new java.awt.Color(99, 102, 241));
        panelRound23.setRoundBottomLeft(25);
        panelRound23.setRoundBottomRight(25);
        panelRound23.setRoundTopLeft(25);
        panelRound23.setRoundTopRight(25);

        javax.swing.GroupLayout panelRound23Layout = new javax.swing.GroupLayout(panelRound23);
        panelRound23.setLayout(panelRound23Layout);
        panelRound23Layout.setHorizontalGroup(
            panelRound23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 688, Short.MAX_VALUE)
        );
        panelRound23Layout.setVerticalGroup(
            panelRound23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRound8Layout = new javax.swing.GroupLayout(panelRound8);
        panelRound8.setLayout(panelRound8Layout);
        panelRound8Layout.setHorizontalGroup(
            panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound8Layout.createSequentialGroup()
                        .addComponent(genderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelRound23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelRound8Layout.setVerticalGroup(
            panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound8Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelRound8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelRound23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelRound9.setBackground(new java.awt.Color(165, 180, 252));
        panelRound9.setRoundBottomLeft(25);
        panelRound9.setRoundBottomRight(25);
        panelRound9.setRoundTopLeft(25);
        panelRound9.setRoundTopRight(25);

        panelRound19.setBackground(new java.awt.Color(99, 102, 241));
        panelRound19.setRoundBottomLeft(25);
        panelRound19.setRoundBottomRight(25);
        panelRound19.setRoundTopLeft(25);
        panelRound19.setRoundTopRight(25);

        adminSettingsLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        adminSettingsLabel.setForeground(new java.awt.Color(255, 255, 255));
        adminSettingsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adminSettingsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminSetting.png"))); // NOI18N
        adminSettingsLabel.setText("Admin Settings");
        adminSettingsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adminSettingsLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelRound19Layout = new javax.swing.GroupLayout(panelRound19);
        panelRound19.setLayout(panelRound19Layout);
        panelRound19Layout.setHorizontalGroup(
            panelRound19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound19Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(adminSettingsLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRound19Layout.setVerticalGroup(
            panelRound19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(adminSettingsLabel)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelRound9Layout = new javax.swing.GroupLayout(panelRound9);
        panelRound9.setLayout(panelRound9Layout);
        panelRound9Layout.setHorizontalGroup(
            panelRound9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound9Layout.setVerticalGroup(
            panelRound9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound9Layout.createSequentialGroup()
                .addContainerGap(153, Short.MAX_VALUE)
                .addComponent(panelRound19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout usersPanelLayout = new javax.swing.GroupLayout(usersPanel);
        usersPanel.setLayout(usersPanelLayout);
        usersPanelLayout.setHorizontalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRound9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        usersPanelLayout.setVerticalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(usersPanelLayout.createSequentialGroup()
                        .addComponent(panelRound8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelRound9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        contentLayeredPane.add(usersPanel, "card2");

        inventoryPanel.setBackground(new java.awt.Color(224, 231, 255));
        inventoryPanel.setRoundTopLeft(50);

        inventoryTable.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        inventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Category", "Product Name", "Description", "Quantity", "Retail Price", "Date of Purchase"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        inventoryTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        inventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryTableMouseClicked(evt);
            }
        });
        inventoryTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                inventoryTablePropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(inventoryTable);

        panelRound13.setBackground(new java.awt.Color(165, 180, 252));
        panelRound13.setRoundBottomLeft(25);
        panelRound13.setRoundBottomRight(25);
        panelRound13.setRoundTopLeft(50);
        panelRound13.setRoundTopRight(25);

        im1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        im1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Product");

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Description");

        im2.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        im2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        im3.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        im3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        im3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                im3KeyTyped(evt);
            }
        });

        im4.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        im4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        im4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        im6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        im6.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Quantity");

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Category");

        jLabel18.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Retail Price");

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Date of Purchase");

        im5.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        im5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        im5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                im5KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelRound13Layout = new javax.swing.GroupLayout(panelRound13);
        panelRound13.setLayout(panelRound13Layout);
        panelRound13Layout.setHorizontalGroup(
            panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound13Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im3)
                    .addComponent(im4, 0, 205, Short.MAX_VALUE))
                .addGap(75, 75, 75)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(im6, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im5, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(117, 117, 117))
        );
        panelRound13Layout.setVerticalGroup(
            panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound13Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16)
                    .addComponent(jLabel18))
                .addGap(7, 7, 7)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(im1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(im5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17)
                    .addComponent(jLabel19))
                .addGap(6, 6, 6)
                .addGroup(panelRound13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(im6, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(im4)
                    .addComponent(im2))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        inventoryAddBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        inventoryAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        inventoryAddBtn.setText("ADD");
        inventoryAddBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        inventoryAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryAddBtnActionPerformed(evt);
            }
        });

        inventoryEditBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        inventoryEditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        inventoryEditBtn.setText("EDIT");
        inventoryEditBtn.setToolTipText("Click to edit and save a cell at a time. Each press saves changes made to a single cell.");
        inventoryEditBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        inventoryEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryEditBtnActionPerformed(evt);
            }
        });

        inventoryDeleteBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        inventoryDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        inventoryDeleteBtn.setText("DELETE");
        inventoryDeleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        inventoryDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryDeleteBtnActionPerformed(evt);
            }
        });

        inventorySearchBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        inventorySearchBar.setPrefixIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        inventorySearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inventorySearchBarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout inventoryPanelLayout = new javax.swing.GroupLayout(inventoryPanel);
        inventoryPanel.setLayout(inventoryPanelLayout);
        inventoryPanelLayout.setHorizontalGroup(
            inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(inventoryPanelLayout.createSequentialGroup()
                        .addComponent(panelRound13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(inventoryPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(inventoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(inventoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(inventoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                        .addComponent(inventorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))))
        );
        inventoryPanelLayout.setVerticalGroup(
            inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inventoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentLayeredPane.add(inventoryPanel, "card2");

        categoryPanel.setBackground(new java.awt.Color(224, 231, 255));
        categoryPanel.setRoundTopLeft(50);

        categoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category ID", "Category", "Date Created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(categoryTable);

        panelRound3.setBackground(new java.awt.Color(165, 180, 252));
        panelRound3.setRoundBottomLeft(25);
        panelRound3.setRoundBottomRight(25);
        panelRound3.setRoundTopLeft(50);
        panelRound3.setRoundTopRight(25);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Category");

        categoryInput.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        categoryInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        javax.swing.GroupLayout panelRound3Layout = new javax.swing.GroupLayout(panelRound3);
        panelRound3.setLayout(panelRound3Layout);
        panelRound3Layout.setHorizontalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryInput, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRound3Layout.setVerticalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addGap(6, 6, 6)
                .addComponent(categoryInput, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        categoryAddBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        categoryAddBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        categoryAddBtn.setText("ADD");
        categoryAddBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        categoryAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryAddBtnActionPerformed(evt);
            }
        });

        categoryEditBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        categoryEditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        categoryEditBtn.setText("EDIT");
        categoryEditBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        categoryEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryEditBtnActionPerformed(evt);
            }
        });

        categoryDeleteBtn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        categoryDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        categoryDeleteBtn.setText("DELETE");
        categoryDeleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        categoryDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryDeleteBtnActionPerformed(evt);
            }
        });

        categorySearchBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        categorySearchBar.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        categorySearchBar.setPrefixIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        categorySearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categorySearchBarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout categoryPanelLayout = new javax.swing.GroupLayout(categoryPanel);
        categoryPanel.setLayout(categoryPanelLayout);
        categoryPanelLayout.setHorizontalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(categoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(panelRound3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, categoryPanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(categoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(categoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(categoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                        .addComponent(categorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)))
                .addContainerGap())
        );
        categoryPanelLayout.setVerticalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, categoryPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentLayeredPane.add(categoryPanel, "card2");

        salesPanel.setBackground(new java.awt.Color(224, 231, 255));
        salesPanel.setRoundTopLeft(50);

        panelRound10.setBackground(new java.awt.Color(165, 180, 252));
        panelRound10.setRoundBottomLeft(25);
        panelRound10.setRoundBottomRight(25);
        panelRound10.setRoundTopLeft(5);
        panelRound10.setRoundTopRight(5);

        recieptTextArea.setColumns(20);
        recieptTextArea.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        recieptTextArea.setRows(5);
        recieptTextArea.setEnabled(false);
        jScrollPane3.setViewportView(recieptTextArea);

        payBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pay.png"))); // NOI18N
        payBtn.setText("PAY");
        payBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        payBtn.setEnabled(false);
        payBtn.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                payBtnStateChanged(evt);
            }
        });
        payBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payBtnActionPerformed(evt);
            }
        });

        printBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/print.png"))); // NOI18N
        printBtn.setText("PRINT");
        printBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        printBtn.setEnabled(false);
        printBtn.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                printBtnStateChanged(evt);
            }
        });
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBtnActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Total");

        jLabel34.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Received");

        jLabel35.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Balance");

        receivedTextField.setEnabled(false);
        receivedTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                receivedTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                receivedTextFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panelRound10Layout = new javax.swing.GroupLayout(panelRound10);
        panelRound10.setLayout(panelRound10Layout);
        panelRound10Layout.setHorizontalGroup(
            panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(panelRound10Layout.createSequentialGroup()
                        .addComponent(payBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(panelRound10Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34)
                    .addComponent(jLabel33)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(getBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(receivedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getTotalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRound10Layout.setVerticalGroup(
            panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getTotalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(receivedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        panelRound14.setBackground(new java.awt.Color(165, 180, 252));
        panelRound14.setRoundBottomLeft(25);
        panelRound14.setRoundBottomRight(25);
        panelRound14.setRoundTopLeft(50);
        panelRound14.setRoundTopRight(25);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Category");

        salesCategoryComboBox.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        salesCategoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        salesCategoryComboBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        salesCategoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesCategoryComboBoxActionPerformed(evt);
            }
        });
        salesCategoryComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                salesCategoryComboBoxKeyReleased(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Item Name");

        itemNameTextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        itemNameTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Description");

        descriptionTextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        descriptionTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        dateCHooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        dateCHooser.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Date");

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Price");

        priceSpinner.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        priceSpinner.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel28.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Quantity");

        quantitySpinner.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        quantitySpinner.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        quantitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                quantitySpinnerStateChanged(evt);
            }
        });
        quantitySpinner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                quantitySpinnerMouseClicked(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Discount");

        discountSpinner.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        discountSpinner.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        discountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                discountSpinnerStateChanged(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Total");

        totalSpinner.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        totalSpinner.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        totalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                totalSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelRound14Layout = new javax.swing.GroupLayout(panelRound14);
        panelRound14.setLayout(panelRound14Layout);
        panelRound14Layout.setHorizontalGroup(
            panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound14Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(66, 66, 66))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(79, 79, 79))
                    .addComponent(salesCategoryComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(itemNameTextField))
                .addGap(18, 18, 18)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(66, 66, 66))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(102, 102, 102))
                    .addComponent(descriptionTextField)
                    .addComponent(dateCHooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(81, 81, 81))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(102, 102, 102))
                    .addComponent(priceSpinner)
                    .addComponent(quantitySpinner))
                .addGap(18, 18, 18)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(81, 81, 81))
                    .addComponent(discountSpinner)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(101, 101, 101))
                    .addComponent(totalSpinner))
                .addGap(16, 16, 16))
        );
        panelRound14Layout.setVerticalGroup(
            panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound14Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(6, 6, 6)
                        .addComponent(salesCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(7, 7, 7)
                        .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(7, 7, 7)
                        .addComponent(priceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(7, 7, 7)
                        .addComponent(discountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(7, 7, 7)
                        .addComponent(itemNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound14Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(6, 6, 6)
                        .addComponent(dateCHooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound14Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelRound14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound14Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addGap(7, 7, 7)
                                .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound14Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(7, 7, 7)
                                .addComponent(totalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        salesTable1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        salesTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category", "Product Name", "Description", "Quantity", "Retail Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        salesTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesTable1MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(salesTable1);

        salesTable2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        salesTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Retail Price", "Discount", "Quantity", "Sub Total", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(salesTable2);

        addCartBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/addCart.png"))); // NOI18N
        addCartBtn.setText("ADD");
        addCartBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        addCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCartBtnActionPerformed(evt);
            }
        });

        removeCartBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/removeCart.png"))); // NOI18N
        removeCartBtn.setText("REMOVE");
        removeCartBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        removeCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCartBtnActionPerformed(evt);
            }
        });

        invoiceTextField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        invoiceTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        ivnLabel.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        ivnLabel.setForeground(new java.awt.Color(102, 102, 102));
        ivnLabel.setText("Invoice Number");

        javax.swing.GroupLayout salesPanelLayout = new javax.swing.GroupLayout(salesPanel);
        salesPanel.setLayout(salesPanelLayout);
        salesPanelLayout.setHorizontalGroup(
            salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salesPanelLayout.createSequentialGroup()
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelRound14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane5)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18))
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(removeCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63)))
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelRound10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addComponent(ivnLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invoiceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );
        salesPanelLayout.setVerticalGroup(
            salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(invoiceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ivnLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelRound10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(panelRound14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(removeCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)))
                .addContainerGap())
        );

        contentLayeredPane.add(salesPanel, "card2");

        returnItemPanel.setBackground(new java.awt.Color(224, 231, 255));
        returnItemPanel.setRoundTopLeft(50);

        panelRound21.setBackground(new java.awt.Color(165, 180, 252));
        panelRound21.setRoundBottomLeft(25);
        panelRound21.setRoundBottomRight(25);
        panelRound21.setRoundTopLeft(50);
        panelRound21.setRoundTopRight(25);

        jLabel40.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Invoice Number");

        invoicenumtextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        invoicenumtextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        returnItemBtn.setText("RETURN");
        returnItemBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        returnItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnItemBtnActionPerformed(evt);
            }
        });

        returnItemSearchBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        returnItemSearchBar.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        returnItemSearchBar.setPrefixIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        returnItemSearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                returnItemSearchBarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelRound21Layout = new javax.swing.GroupLayout(panelRound21);
        panelRound21.setLayout(panelRound21Layout);
        panelRound21Layout.setHorizontalGroup(
            panelRound21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound21Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelRound21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound21Layout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelRound21Layout.createSequentialGroup()
                        .addComponent(invoicenumtextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(returnItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                        .addComponent(returnItemSearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86))))
        );
        panelRound21Layout.setVerticalGroup(
            panelRound21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound21Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel40)
                .addGap(7, 7, 7)
                .addGroup(panelRound21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(invoicenumtextField, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(returnItemBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnItemSearchBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        returnitemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice Number", "Product", "Discount Percent", "Quantity", "Subtotal", "Total", "Purchased Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        returnitemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnitemTableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(returnitemTable);

        javax.swing.GroupLayout returnItemPanelLayout = new javax.swing.GroupLayout(returnItemPanel);
        returnItemPanel.setLayout(returnItemPanelLayout);
        returnItemPanelLayout.setHorizontalGroup(
            returnItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnItemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(returnItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
        );
        returnItemPanelLayout.setVerticalGroup(
            returnItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnItemPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        contentLayeredPane.add(returnItemPanel, "card2");

        priceListPanel.setBackground(new java.awt.Color(224, 231, 255));
        priceListPanel.setRoundTopLeft(50);

        javax.swing.GroupLayout priceListPanelLayout = new javax.swing.GroupLayout(priceListPanel);
        priceListPanel.setLayout(priceListPanelLayout);
        priceListPanelLayout.setHorizontalGroup(
            priceListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 920, Short.MAX_VALUE)
        );
        priceListPanelLayout.setVerticalGroup(
            priceListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 555, Short.MAX_VALUE)
        );

        contentLayeredPane.add(priceListPanel, "card2");

        reportPanel.setBackground(new java.awt.Color(224, 231, 255));
        reportPanel.setRoundTopLeft(50);

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 920, Short.MAX_VALUE)
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 555, Short.MAX_VALUE)
        );

        contentLayeredPane.add(reportPanel, "card2");

        settingsPanel.setBackground(new java.awt.Color(224, 231, 255));
        settingsPanel.setRoundTopLeft(50);

        settingsScrillPane.setBorder(null);
        settingsScrillPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(224, 231, 255));

        panelRound22.setBackground(new java.awt.Color(165, 180, 252));
        panelRound22.setRoundBottomLeft(25);
        panelRound22.setRoundBottomRight(25);
        panelRound22.setRoundTopLeft(50);
        panelRound22.setRoundTopRight(25);

        jLabel10.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Change Password");

        getPasswordPasswordField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel41.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel41.setText("New Password");

        jLabel42.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel42.setText("Confirm New Password");

        getConfirmPasswordPasswordField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        addCartBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        addCartBtn1.setText("CHANGE PASSWORD");
        addCartBtn1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        addCartBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCartBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound22Layout = new javax.swing.GroupLayout(panelRound22);
        panelRound22.setLayout(panelRound22Layout);
        panelRound22Layout.setHorizontalGroup(
            panelRound22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound22Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelRound22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42)
                    .addComponent(getConfirmPasswordPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jLabel41)
                    .addComponent(getPasswordPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addComponent(jLabel10)
                    .addComponent(addCartBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(684, Short.MAX_VALUE))
        );
        panelRound22Layout.setVerticalGroup(
            panelRound22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound22Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getPasswordPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getConfirmPasswordPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addCartBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(602, Short.MAX_VALUE))
        );

        settingsScrillPane.setViewportView(jPanel1);

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsScrillPane)
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(settingsScrillPane, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
        );

        contentLayeredPane.add(settingsPanel, "card2");

        titleLabel.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("POS AND INVENTORY MANAGEMENT SYSTEM");
        titleLabel.setToolTipText("Credit for this application goes to Josuan.");
        titleLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titleLabelMouseClicked(evt);
            }
        });

        closeBtn.setBackground(new java.awt.Color(255, 95, 90));
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });

        minimizeBtn.setBackground(new java.awt.Color(255, 189, 68));
        minimizeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeBtnActionPerformed(evt);
            }
        });

        maximizeBtn.setBackground(new java.awt.Color(0, 202, 78));
        maximizeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maximizeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout close_min_max_PanelLayout = new javax.swing.GroupLayout(close_min_max_Panel);
        close_min_max_Panel.setLayout(close_min_max_PanelLayout);
        close_min_max_PanelLayout.setHorizontalGroup(
            close_min_max_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(close_min_max_PanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minimizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maximizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        close_min_max_PanelLayout.setVerticalGroup(
            close_min_max_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(closeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
            .addComponent(minimizeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(maximizeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addComponent(close_min_max_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(171, 171, 171)
                .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(277, 277, 277))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(close_min_max_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        menuLayeredPane.setLayout(new java.awt.CardLayout());

        menuPanel1.setBackground(new java.awt.Color(79, 70, 229));
        menuPanel1.setRoundTopRight(50);

        logoutBack.setBackground(new java.awt.Color(79, 70, 229));
        logoutBack.setRoundBottomLeft(25);
        logoutBack.setRoundBottomRight(25);
        logoutBack.setRoundTopLeft(25);
        logoutBack.setRoundTopRight(25);

        logoutLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        logoutLabel.setForeground(new java.awt.Color(255, 255, 255));
        logoutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logout.png"))); // NOI18N
        logoutLabel.setText("Log Out");
        logoutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout logoutBackLayout = new javax.swing.GroupLayout(logoutBack);
        logoutBack.setLayout(logoutBackLayout);
        logoutBackLayout.setHorizontalGroup(
            logoutBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutBackLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        logoutBackLayout.setVerticalGroup(
            logoutBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutLabel)
                .addContainerGap())
        );

        settingsBack.setBackground(new java.awt.Color(79, 70, 229));
        settingsBack.setRoundBottomLeft(25);
        settingsBack.setRoundBottomRight(25);
        settingsBack.setRoundTopLeft(25);
        settingsBack.setRoundTopRight(25);

        settingsLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        settingsLabel.setForeground(new java.awt.Color(255, 255, 255));
        settingsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        settingsLabel.setText("Settings");

        javax.swing.GroupLayout settingsBackLayout = new javax.swing.GroupLayout(settingsBack);
        settingsBack.setLayout(settingsBackLayout);
        settingsBackLayout.setHorizontalGroup(
            settingsBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(settingsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        settingsBackLayout.setVerticalGroup(
            settingsBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(settingsLabel)
                .addContainerGap())
        );

        reportBack.setBackground(new java.awt.Color(79, 70, 229));
        reportBack.setRoundBottomLeft(25);
        reportBack.setRoundBottomRight(25);
        reportBack.setRoundTopLeft(25);
        reportBack.setRoundTopRight(25);

        reportLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        reportLabel.setForeground(new java.awt.Color(255, 255, 255));
        reportLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/report.png"))); // NOI18N
        reportLabel.setText("Report");

        javax.swing.GroupLayout reportBackLayout = new javax.swing.GroupLayout(reportBack);
        reportBack.setLayout(reportBackLayout);
        reportBackLayout.setHorizontalGroup(
            reportBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        reportBackLayout.setVerticalGroup(
            reportBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reportLabel)
                .addContainerGap())
        );

        priceListBack.setBackground(new java.awt.Color(79, 70, 229));
        priceListBack.setRoundBottomLeft(25);
        priceListBack.setRoundBottomRight(25);
        priceListBack.setRoundTopLeft(25);
        priceListBack.setRoundTopRight(25);

        priceListLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        priceListLabel.setForeground(new java.awt.Color(255, 255, 255));
        priceListLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pricelist.png"))); // NOI18N
        priceListLabel.setText("Price List");

        javax.swing.GroupLayout priceListBackLayout = new javax.swing.GroupLayout(priceListBack);
        priceListBack.setLayout(priceListBackLayout);
        priceListBackLayout.setHorizontalGroup(
            priceListBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, priceListBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(priceListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        priceListBackLayout.setVerticalGroup(
            priceListBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, priceListBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(priceListLabel)
                .addContainerGap())
        );

        returnItemBack.setBackground(new java.awt.Color(79, 70, 229));
        returnItemBack.setRoundBottomLeft(25);
        returnItemBack.setRoundBottomRight(25);
        returnItemBack.setRoundTopLeft(25);
        returnItemBack.setRoundTopRight(25);

        returnItemLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        returnItemLabel.setForeground(new java.awt.Color(255, 255, 255));
        returnItemLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/returnitem.png"))); // NOI18N
        returnItemLabel.setText("Return Item");

        javax.swing.GroupLayout returnItemBackLayout = new javax.swing.GroupLayout(returnItemBack);
        returnItemBack.setLayout(returnItemBackLayout);
        returnItemBackLayout.setHorizontalGroup(
            returnItemBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnItemBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnItemLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        returnItemBackLayout.setVerticalGroup(
            returnItemBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnItemBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnItemLabel)
                .addContainerGap())
        );

        salesBack.setBackground(new java.awt.Color(79, 70, 229));
        salesBack.setRoundBottomLeft(25);
        salesBack.setRoundBottomRight(25);
        salesBack.setRoundTopLeft(25);
        salesBack.setRoundTopRight(25);

        salesLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        salesLabel.setForeground(new java.awt.Color(255, 255, 255));
        salesLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sales.png"))); // NOI18N
        salesLabel.setText("Sales");

        javax.swing.GroupLayout salesBackLayout = new javax.swing.GroupLayout(salesBack);
        salesBack.setLayout(salesBackLayout);
        salesBackLayout.setHorizontalGroup(
            salesBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(salesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        salesBackLayout.setVerticalGroup(
            salesBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesBackLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(salesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        categoryBack.setBackground(new java.awt.Color(79, 70, 229));
        categoryBack.setRoundBottomLeft(25);
        categoryBack.setRoundBottomRight(25);
        categoryBack.setRoundTopLeft(25);
        categoryBack.setRoundTopRight(25);

        categoryLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        categoryLabel.setForeground(new java.awt.Color(255, 255, 255));
        categoryLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/category.png"))); // NOI18N
        categoryLabel.setText("Category");

        javax.swing.GroupLayout categoryBackLayout = new javax.swing.GroupLayout(categoryBack);
        categoryBack.setLayout(categoryBackLayout);
        categoryBackLayout.setHorizontalGroup(
            categoryBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, categoryBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(categoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        categoryBackLayout.setVerticalGroup(
            categoryBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, categoryBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(categoryLabel)
                .addContainerGap())
        );

        inventoryBack.setBackground(new java.awt.Color(79, 70, 229));
        inventoryBack.setRoundBottomLeft(25);
        inventoryBack.setRoundBottomRight(25);
        inventoryBack.setRoundTopLeft(25);
        inventoryBack.setRoundTopRight(25);

        inventoryLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        inventoryLabel.setForeground(new java.awt.Color(255, 255, 255));
        inventoryLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/inventory.png"))); // NOI18N
        inventoryLabel.setText("Inventory");

        javax.swing.GroupLayout inventoryBackLayout = new javax.swing.GroupLayout(inventoryBack);
        inventoryBack.setLayout(inventoryBackLayout);
        inventoryBackLayout.setHorizontalGroup(
            inventoryBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inventoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        inventoryBackLayout.setVerticalGroup(
            inventoryBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inventoryLabel)
                .addContainerGap())
        );

        usersBack.setBackground(new java.awt.Color(79, 70, 229));
        usersBack.setRoundBottomLeft(25);
        usersBack.setRoundBottomRight(25);
        usersBack.setRoundTopLeft(25);
        usersBack.setRoundTopRight(25);

        usersLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        usersLabel.setForeground(new java.awt.Color(255, 255, 255));
        usersLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user.png"))); // NOI18N
        usersLabel.setText("Users");

        javax.swing.GroupLayout usersBackLayout = new javax.swing.GroupLayout(usersBack);
        usersBack.setLayout(usersBackLayout);
        usersBackLayout.setHorizontalGroup(
            usersBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBackLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(usersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        usersBackLayout.setVerticalGroup(
            usersBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBackLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usersLabel)
                .addContainerGap())
        );

        dashboardBack.setBackground(new java.awt.Color(79, 70, 229));
        dashboardBack.setRoundBottomLeft(25);
        dashboardBack.setRoundBottomRight(25);
        dashboardBack.setRoundTopLeft(25);
        dashboardBack.setRoundTopRight(25);

        dashboardLabel.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        dashboardLabel.setForeground(new java.awt.Color(255, 255, 255));
        dashboardLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard.png"))); // NOI18N
        dashboardLabel.setText("Dashboard");

        javax.swing.GroupLayout dashboardBackLayout = new javax.swing.GroupLayout(dashboardBack);
        dashboardBack.setLayout(dashboardBackLayout);
        dashboardBackLayout.setHorizontalGroup(
            dashboardBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardBackLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        dashboardBackLayout.setVerticalGroup(
            dashboardBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardBackLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout menuPanel1Layout = new javax.swing.GroupLayout(menuPanel1);
        menuPanel1.setLayout(menuPanel1Layout);
        menuPanel1Layout.setHorizontalGroup(
            menuPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
            .addGroup(menuPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(menuPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(menuPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dashboardBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(usersBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(inventoryBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(categoryBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(salesBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(returnItemBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(priceListBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(reportBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(settingsBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(logoutBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        menuPanel1Layout.setVerticalGroup(
            menuPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 555, Short.MAX_VALUE)
            .addGroup(menuPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(menuPanel1Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(dashboardBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(usersBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(inventoryBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(categoryBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(salesBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(returnItemBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(priceListBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(reportBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(settingsBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(logoutBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(53, Short.MAX_VALUE)))
        );

        menuLayeredPane.add(menuPanel1, "card2");

        menuPanel2.setBackground(new java.awt.Color(79, 70, 229));
        menuPanel2.setRoundTopRight(50);

        dashboardBack1.setBackground(new java.awt.Color(79, 70, 229));
        dashboardBack1.setRoundBottomLeft(25);
        dashboardBack1.setRoundBottomRight(25);
        dashboardBack1.setRoundTopLeft(25);
        dashboardBack1.setRoundTopRight(25);

        dashboardLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        dashboardLabel1.setForeground(new java.awt.Color(255, 255, 255));
        dashboardLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dashboard.png"))); // NOI18N
        dashboardLabel1.setText("Dashboard");

        javax.swing.GroupLayout dashboardBack1Layout = new javax.swing.GroupLayout(dashboardBack1);
        dashboardBack1.setLayout(dashboardBack1Layout);
        dashboardBack1Layout.setHorizontalGroup(
            dashboardBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dashboardLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dashboardBack1Layout.setVerticalGroup(
            dashboardBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardBack1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        salesBack1.setBackground(new java.awt.Color(79, 70, 229));
        salesBack1.setRoundBottomLeft(25);
        salesBack1.setRoundBottomRight(25);
        salesBack1.setRoundTopLeft(25);
        salesBack1.setRoundTopRight(25);

        salesLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        salesLabel1.setForeground(new java.awt.Color(255, 255, 255));
        salesLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sales.png"))); // NOI18N
        salesLabel1.setText("Sales");

        javax.swing.GroupLayout salesBack1Layout = new javax.swing.GroupLayout(salesBack1);
        salesBack1.setLayout(salesBack1Layout);
        salesBack1Layout.setHorizontalGroup(
            salesBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(salesLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        salesBack1Layout.setVerticalGroup(
            salesBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(salesLabel1)
                .addContainerGap())
        );

        priceListBack1.setBackground(new java.awt.Color(79, 70, 229));
        priceListBack1.setRoundBottomLeft(25);
        priceListBack1.setRoundBottomRight(25);
        priceListBack1.setRoundTopLeft(25);
        priceListBack1.setRoundTopRight(25);

        priceListLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        priceListLabel1.setForeground(new java.awt.Color(255, 255, 255));
        priceListLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pricelist.png"))); // NOI18N
        priceListLabel1.setText("Price List");

        javax.swing.GroupLayout priceListBack1Layout = new javax.swing.GroupLayout(priceListBack1);
        priceListBack1.setLayout(priceListBack1Layout);
        priceListBack1Layout.setHorizontalGroup(
            priceListBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, priceListBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(priceListLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        priceListBack1Layout.setVerticalGroup(
            priceListBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, priceListBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(priceListLabel1)
                .addContainerGap())
        );

        returnItemBack1.setBackground(new java.awt.Color(79, 70, 229));
        returnItemBack1.setRoundBottomLeft(25);
        returnItemBack1.setRoundBottomRight(25);
        returnItemBack1.setRoundTopLeft(25);
        returnItemBack1.setRoundTopRight(25);

        returnItemLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        returnItemLabel1.setForeground(new java.awt.Color(255, 255, 255));
        returnItemLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/returnitem.png"))); // NOI18N
        returnItemLabel1.setText("Return Item");

        javax.swing.GroupLayout returnItemBack1Layout = new javax.swing.GroupLayout(returnItemBack1);
        returnItemBack1.setLayout(returnItemBack1Layout);
        returnItemBack1Layout.setHorizontalGroup(
            returnItemBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnItemBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnItemLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        returnItemBack1Layout.setVerticalGroup(
            returnItemBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnItemBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnItemLabel1)
                .addContainerGap())
        );

        logoutBack1.setBackground(new java.awt.Color(79, 70, 229));
        logoutBack1.setRoundBottomLeft(25);
        logoutBack1.setRoundBottomRight(25);
        logoutBack1.setRoundTopLeft(25);
        logoutBack1.setRoundTopRight(25);

        logoutLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        logoutLabel1.setForeground(new java.awt.Color(255, 255, 255));
        logoutLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logout.png"))); // NOI18N
        logoutLabel1.setText("Log Out");
        logoutLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout logoutBack1Layout = new javax.swing.GroupLayout(logoutBack1);
        logoutBack1.setLayout(logoutBack1Layout);
        logoutBack1Layout.setHorizontalGroup(
            logoutBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        logoutBack1Layout.setVerticalGroup(
            logoutBack1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutBack1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        usersBack2.setBackground(new java.awt.Color(79, 70, 229));
        usersBack2.setRoundBottomLeft(25);
        usersBack2.setRoundBottomRight(25);
        usersBack2.setRoundTopLeft(25);
        usersBack2.setRoundTopRight(25);

        usersLabel2.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        usersLabel2.setForeground(new java.awt.Color(255, 255, 255));
        usersLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user.png"))); // NOI18N
        usersLabel2.setText("Users");
        usersLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersLabel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout usersBack2Layout = new javax.swing.GroupLayout(usersBack2);
        usersBack2.setLayout(usersBack2Layout);
        usersBack2Layout.setHorizontalGroup(
            usersBack2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBack2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usersLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        usersBack2Layout.setVerticalGroup(
            usersBack2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBack2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usersLabel2)
                .addContainerGap())
        );

        settingsBack2.setBackground(new java.awt.Color(79, 70, 229));
        settingsBack2.setRoundBottomLeft(25);
        settingsBack2.setRoundBottomRight(25);
        settingsBack2.setRoundTopLeft(25);
        settingsBack2.setRoundTopRight(25);

        settingsLabel2.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        settingsLabel2.setForeground(new java.awt.Color(255, 255, 255));
        settingsLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        settingsLabel2.setText("Settings");

        javax.swing.GroupLayout settingsBack2Layout = new javax.swing.GroupLayout(settingsBack2);
        settingsBack2.setLayout(settingsBack2Layout);
        settingsBack2Layout.setHorizontalGroup(
            settingsBack2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsBack2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(settingsLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        settingsBack2Layout.setVerticalGroup(
            settingsBack2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsBack2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(settingsLabel2)
                .addContainerGap())
        );

        javax.swing.GroupLayout menuPanel2Layout = new javax.swing.GroupLayout(menuPanel2);
        menuPanel2.setLayout(menuPanel2Layout);
        menuPanel2Layout.setHorizontalGroup(
            menuPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(menuPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(salesBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(returnItemBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(priceListBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logoutBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(menuPanel2Layout.createSequentialGroup()
                        .addGroup(menuPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dashboardBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usersBack2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(settingsBack2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        menuPanel2Layout.setVerticalGroup(
            menuPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(dashboardBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usersBack2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnItemBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(priceListBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settingsBack2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logoutBack1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(209, Short.MAX_VALUE))
        );

        menuLayeredPane.add(menuPanel2, "card2");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(menuLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentLayeredPane))
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentLayeredPane)
                    .addComponent(menuLayeredPane)))
        );

        mainLayeredPane.add(mainPanel, "card2");

        adminPanel.setBackground(new java.awt.Color(224, 231, 255));

        jTabbedPane1.setBackground(new java.awt.Color(224, 231, 255));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        panelRound15.setBackground(new java.awt.Color(165, 180, 252));
        panelRound15.setRoundTopLeft(50);
        panelRound15.setRoundTopRight(50);

        usersTable.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Firstname", "Lastname", "Username", "Password", "Birth Date", "Gender", "Image", "User Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        usersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(usersTable);

        adminDeleteBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        adminDeleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        adminDeleteBtn.setText("DELETE");
        adminDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminDeleteBtnActionPerformed(evt);
            }
        });

        promoteBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        promoteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/promote.png"))); // NOI18N
        promoteBtn.setText("PROMOTE");
        promoteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promoteBtnActionPerformed(evt);
            }
        });

        demoteBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        demoteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/demote.png"))); // NOI18N
        demoteBtn.setText("DEMOTE");
        demoteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demoteBtnActionPerformed(evt);
            }
        });

        adminEditBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        adminEditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        adminEditBtn.setText("EDIT");
        adminEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminEditBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound15Layout = new javax.swing.GroupLayout(panelRound15);
        panelRound15.setLayout(panelRound15Layout);
        panelRound15Layout.setHorizontalGroup(
            panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(promoteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(demoteBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(adminDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adminEditBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelRound15Layout.setVerticalGroup(
            panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound15Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panelRound15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound15Layout.createSequentialGroup()
                        .addComponent(promoteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(demoteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(adminEditBtn)
                        .addGap(18, 18, 18)
                        .addComponent(adminDeleteBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("All Users", panelRound15);

        panelRound16.setBackground(new java.awt.Color(165, 180, 252));
        panelRound16.setRoundTopLeft(50);
        panelRound16.setRoundTopRight(50);

        panelRound17.setBackground(new java.awt.Color(224, 231, 255));
        panelRound17.setRoundBottomLeft(25);
        panelRound17.setRoundBottomRight(25);
        panelRound17.setRoundTopLeft(50);
        panelRound17.setRoundTopRight(25);

        imageAvatar2.setForeground(new java.awt.Color(165, 180, 252));
        imageAvatar2.setBorderSize(3);
        imageAvatar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nullProfile.jpg"))); // NOI18N
        imageAvatar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                imageAvatar2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                imageAvatar2MouseExited(evt);
            }
        });

        uploadPanel1.setRoundBottomLeft(25);
        uploadPanel1.setRoundBottomRight(25);
        uploadPanel1.setRoundTopLeft(25);
        uploadPanel1.setRoundTopRight(25);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel3.setText("Upload Image:");

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Drag & Drop");

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("or");

        uploadImageLabel.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        uploadImageLabel.setForeground(new java.awt.Color(55, 48, 163));
        uploadImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uploadImageLabel.setText("browse");
        uploadImageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                uploadImageLabelMouseClicked(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/uploadImage.png"))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 10)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Supports JPEG, JPG");

        javax.swing.GroupLayout uploadPanel1Layout = new javax.swing.GroupLayout(uploadPanel1);
        uploadPanel1.setLayout(uploadPanel1Layout);
        uploadPanel1Layout.setHorizontalGroup(
            uploadPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanel1Layout.createSequentialGroup()
                .addGroup(uploadPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(uploadPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uploadImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(uploadPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel3)
                        .addGap(0, 154, Short.MAX_VALUE))
                    .addGroup(uploadPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(uploadPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        uploadPanel1Layout.setVerticalGroup(
            uploadPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addGap(58, 58, 58)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGroup(uploadPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(uploadImageLabel))
                .addGap(0, 0, 0)
                .addComponent(jLabel11)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelRound17Layout = new javax.swing.GroupLayout(panelRound17);
        panelRound17.setLayout(panelRound17Layout);
        panelRound17Layout.setHorizontalGroup(
            panelRound17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound17Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(imageAvatar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(67, 67, 67))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound17Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(uploadPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );
        panelRound17Layout.setVerticalGroup(
            panelRound17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageAvatar2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uploadPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        panelRound18.setBackground(new java.awt.Color(224, 231, 255));
        panelRound18.setRoundBottomLeft(25);
        panelRound18.setRoundBottomRight(25);
        panelRound18.setRoundTopLeft(25);
        panelRound18.setRoundTopRight(25);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel21.setText("First Name");

        getFNameTextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getFNameTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel22.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel22.setText("Last Name");

        getLNameTextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getLNameTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel32.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel32.setText("Username");

        getUNameTextField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getUNameTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel36.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel36.setText("Password");

        getPasswordPasswordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel37.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel37.setText("Confirm Password");

        getConfirmPasswordPasswordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel38.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel38.setText("Gender");

        getGenderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        getGenderComboBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        jLabel39.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel39.setText("Birth Date");

        getBirthDateDateChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));

        addUserBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addUserBtn.setText("ADD");
        addUserBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 48, 163)));
        addUserBtn.setEnabled(false);
        addUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserBtnActionPerformed(evt);
            }
        });

        termServiceCheckBox.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        termServiceCheckBox.setText("I agree to the Terms and Services");
        termServiceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                termServiceCheckBoxActionPerformed(evt);
            }
        });

        showPasswordCheckBox.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        showPasswordCheckBox.setText("Show Password");
        showPasswordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPasswordCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound18Layout = new javax.swing.GroupLayout(panelRound18);
        panelRound18.setLayout(panelRound18Layout);
        panelRound18Layout.setHorizontalGroup(
            panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound18Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showPasswordCheckBox)
                    .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel37)
                        .addComponent(getPasswordPasswordField)
                        .addComponent(jLabel36)
                        .addComponent(jLabel32)
                        .addComponent(getLNameTextField)
                        .addComponent(jLabel22)
                        .addComponent(getFNameTextField)
                        .addComponent(jLabel21)
                        .addComponent(getUNameTextField)
                        .addComponent(getConfirmPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound18Layout.createSequentialGroup()
                        .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel38)
                            .addComponent(jLabel39)
                            .addComponent(getBirthDateDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(addUserBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(getGenderComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound18Layout.createSequentialGroup()
                        .addComponent(termServiceCheckBox)
                        .addGap(72, 72, 72))))
        );
        panelRound18Layout.setVerticalGroup(
            panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound18Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel39))
                .addGap(7, 7, 7)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(getFNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getBirthDateDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(getLNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getGenderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getUNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(termServiceCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getConfirmPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUserBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPasswordCheckBox)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelRound16Layout = new javax.swing.GroupLayout(panelRound16);
        panelRound16.setLayout(panelRound16Layout);
        panelRound16Layout.setHorizontalGroup(
            panelRound16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound16Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(panelRound17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelRound18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );
        panelRound16Layout.setVerticalGroup(
            panelRound16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound16Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panelRound16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelRound16Layout.createSequentialGroup()
                        .addComponent(panelRound18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Add User", panelRound16);

        panelRound20.setBackground(new java.awt.Color(165, 180, 252));
        panelRound20.setRoundTopLeft(50);
        panelRound20.setRoundTopRight(50);

        testBtn1.setText("Test Button #1");
        testBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound20Layout = new javax.swing.GroupLayout(panelRound20);
        panelRound20.setLayout(panelRound20Layout);
        panelRound20Layout.setHorizontalGroup(
            panelRound20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound20Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(testBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(935, Short.MAX_VALUE))
        );
        panelRound20Layout.setVerticalGroup(
            panelRound20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound20Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(testBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(431, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Test Tab", panelRound20);

        close_min_max_Panel1.setOpaque(false);

        closeBtn1.setBackground(new java.awt.Color(255, 95, 90));
        closeBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtn1ActionPerformed(evt);
            }
        });

        minimizeBtn1.setBackground(new java.awt.Color(255, 189, 68));
        minimizeBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeBtn1ActionPerformed(evt);
            }
        });

        maximizeBtn1.setBackground(new java.awt.Color(0, 202, 78));
        maximizeBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maximizeBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout close_min_max_Panel1Layout = new javax.swing.GroupLayout(close_min_max_Panel1);
        close_min_max_Panel1.setLayout(close_min_max_Panel1Layout);
        close_min_max_Panel1Layout.setHorizontalGroup(
            close_min_max_Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(close_min_max_Panel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(closeBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minimizeBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maximizeBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        close_min_max_Panel1Layout.setVerticalGroup(
            close_min_max_Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(closeBtn1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
            .addComponent(minimizeBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(maximizeBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        backLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/back2.png"))); // NOI18N
        backLabel.setToolTipText("Go back to Users Panel");
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backLabelMouseClicked(evt);
            }
        });

        titleLabel1.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        titleLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel1.setText("ADMIN SETTINGS");
        titleLabel1.setToolTipText("Credit for this application goes to Josuan.");
        titleLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout adminPanelLayout = new javax.swing.GroupLayout(adminPanel);
        adminPanel.setLayout(adminPanelLayout);
        adminPanelLayout.setHorizontalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(adminPanelLayout.createSequentialGroup()
                .addComponent(close_min_max_Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(164, 164, 164)
                .addComponent(titleLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(202, 202, 202)
                .addComponent(backLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        adminPanelLayout.setVerticalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, adminPanelLayout.createSequentialGroup()
                .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(close_min_max_Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        mainLayeredPane.add(adminPanel, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLayeredPane)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
        //this.dispose();
        System.exit(0);
    }//GEN-LAST:event_closeBtnActionPerformed

    private void minimizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeBtnActionPerformed
        this.setExtendedState(this.ICONIFIED);
    }//GEN-LAST:event_minimizeBtnActionPerformed

    private void maximizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maximizeBtnActionPerformed
        if (this.getExtendedState() == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.NORMAL);
        } else {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        }
    }//GEN-LAST:event_maximizeBtnActionPerformed

    private void inventoryAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryAddBtnActionPerformed
        String[] value = Utilities.getAllValue(im1, im2, im3, im4, im5, im6,this);

        if(value != null){
            IMT.addInventoryValue(value);
            inventoryInit();
                
            im1.setText("");
            im2.setText("");
            im3.setValue(0);
            im5.setValue(0);
        }
    }//GEN-LAST:event_inventoryAddBtnActionPerformed

    private void inventoryEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryEditBtnActionPerformed
        int getSelectedColumn = inventoryTable.getSelectedColumn();
        int getSelectedRow = inventoryTable.getSelectedRow();
        
        try{
            if(getSelectedRow != -1 && getSelectedColumn != -1){
                Object getNewVal = null;
                Object oldVal = inventoryTable.getValueAt(getSelectedRow, getSelectedColumn);
                switch(getSelectedColumn){
                    case 1:
                        getNewVal = Utilities.getComboxFromTable(inventoryTable,1);
                        break;
                    case 2:
                        getNewVal = Utilities.getSpinnerFromTable(inventoryTable);//
                        break;
                    case 3:
                        getNewVal = Utilities.getSpinnerFromTable(inventoryTable);//
                        break;      
                    case 4:
                        getNewVal = Utilities.getSpinnerFromTable(inventoryTable);
                        break;
                    case 5:
                        getNewVal = Utilities.getSpinnerFromTable(inventoryTable);
                        break;                        
                    default:
                        break;
                }
                
                if(getSelectedColumn == 2 || getSelectedColumn == 3){
                    try{
                        getNewVal = Utilities.first_LetterUpperCase(getNewVal.toString());
                    }catch(NullPointerException e){}
                }
                
                if(getNewVal != null){
                    IMT.EditInventoryValue(getNewVal, getSelectedColumn, Utilities.get_RecordID(inventoryTable));
                    inventoryInit();
                }
            }else{
                JOptionPane.showMessageDialog(this, "No cell is being selected.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.", "Inventory", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_inventoryEditBtnActionPerformed

//        int getSelectedRow = inventoryTable.getSelectedRow();
//        int getSelectedColumn = inventoryTable.getSelectedColumn();
//        try{
//            if(getSelectedRow != -1 && getSelectedColumn != -1){
//                Object oldValue = inventoryTable.getValueAt(getSelectedRow, getSelectedColumn);
//                int oldVal = 0;
//                int newVal = 0;
//                if(getSelectedColumn == 4){
//                    oldVal = Integer.parseInt(oldValue.toString());
//                }
//
//                Object newValue = null;
//
//                if (getSelectedColumn != 1 || newValue != null) {
//                    if(getSelectedColumn == 2 || getSelectedColumn == 3){
//                        newValue = Utilities.first_LetterUpperCase(Utilities.getSpinnerFromTable(inventoryTable));
//                    } else {
//                        newValue = Utilities.getSpinnerFromTable(inventoryTable);
//                    }
//                } else {
//                    newValue = Utilities.getComboxFromTable(inventoryTable);
//                }
//
//                if(getSelectedColumn == 4){
//                    newVal = Integer.parseInt(newValue.toString());
//                }
//
//                if(newValue != null && !oldValue.equals(newVal)) {
//                    IMT.EditInventoryValue(newValue, getSelectedColumn, Utilities.get_RecordID(inventoryTable));
//                } else {
//                    if(newValue == null) {
//                        JOptionPane.showMessageDialog(inventoryPanel, "New value cannot be null!", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//            }else{
//                JOptionPane.showMessageDialog(this, "No cell is being selected.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
//            }
//            inventoryInit();
//            String get_val = (String) salesCategoryComboBox.getSelectedItem(); 
//            SMT.loadInventoryData(salesTable1, get_val);
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again.", "Inventory", JOptionPane.ERROR_MESSAGE);
//        }
    
    private void inventoryDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryDeleteBtnActionPerformed
        if(Utilities.has_NoZeroVal(Utilities.get_RecordIDs(inventoryTable)) && inventoryTable.getSelectedRow() != -1){
            for(Object i : Utilities.get_RecordIDs(inventoryTable)){
                IMT.DeleteInventoryRecord(i);
            }
            inventoryInit();
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_inventoryDeleteBtnActionPerformed

    private void categoryAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryAddBtnActionPerformed
        try{
            String value = Utilities.first_LetterUpperCase(categoryInput.getText().trim());  
            if(!value.isEmpty() && !Utilities.isValueExists(value, DbColumns.CATEGORYCOLUMNS.getValues()[1], DbTables.CATEGORYTABLE.getValue())){
                CMT.addCategoryValue(new String[]{value, java.sql.Date.valueOf(LocalDate.now()).toString()});
                categoryInput.setText("");
                categoryInit();
            }else if(Utilities.isValueExists(value, DbColumns.CATEGORYCOLUMNS.getValues()[1],DbTables.CATEGORYTABLE.getValue())){
                JOptionPane.showMessageDialog(this, "Category with this name already exists.","Category",JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Category cannot be empty.","Category",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_categoryAddBtnActionPerformed

    private void categoryEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryEditBtnActionPerformed
        try{
            String newVal = Utilities.first_LetterUpperCase(Utilities.getSpinnerFromTable(categoryTable));
            int getSelectedColumn = categoryTable.getSelectedColumn();
            if(!Utilities.isValueExists(newVal, DbColumns.CATEGORYCOLUMNS.getValues()[1],DbTables.CATEGORYTABLE.getValue())){
                CMT.EditCategoryValue(newVal, getSelectedColumn, Utilities.get_RecordID(categoryTable));
                categoryInit();
            }else{
                JOptionPane.showMessageDialog(categoryPanel, "Category with this name already exists.","Category",JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(categoryPanel, "No cell is being edited.","Category",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_categoryEditBtnActionPerformed

    private void categoryDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryDeleteBtnActionPerformed
        if(Utilities.has_NoZeroVal(Utilities.get_RecordIDs(categoryTable)) && categoryTable.getSelectedRow() != -1){
            for(Object i : Utilities.get_RecordIDs(categoryTable)){
                CMT.DeleteCategoryRecord(i);
            }
            categoryInit();
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.","Category",JOptionPane.ERROR_MESSAGE);
        }  
    }//GEN-LAST:event_categoryDeleteBtnActionPerformed

    private void inventorySearchBarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inventorySearchBarKeyReleased
        IMT.loadInventoryData(inventoryTable, inventorySearchBar.getText());
    }//GEN-LAST:event_inventorySearchBarKeyReleased

    private void categorySearchBarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categorySearchBarKeyReleased
        CMT.loadCategoryData(categoryTable, categorySearchBar.getText());
    }//GEN-LAST:event_categorySearchBarKeyReleased

    private void im3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_im3KeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != KeyEvent.VK_PERIOD && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
            evt.consume(); 
        }
    }//GEN-LAST:event_im3KeyTyped

    private void im5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_im5KeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != KeyEvent.VK_PERIOD && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
            evt.consume(); 
        }
    }//GEN-LAST:event_im5KeyTyped

    private void logoutLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutLabelMouseClicked
        AppManagement.setCurrentUser("nullUser", this);
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutLabelMouseClicked

    private void logoutLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutLabel1MouseClicked
        AppManagement.setCurrentUser("nullUser", this);
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutLabel1MouseClicked

    private void usersLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersLabel2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_usersLabel2MouseClicked

    private void salesCategoryComboBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salesCategoryComboBoxKeyReleased
        
    }//GEN-LAST:event_salesCategoryComboBoxKeyReleased

    private void salesCategoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesCategoryComboBoxActionPerformed
        String get_val = (String) salesCategoryComboBox.getSelectedItem(); 
        
        SMT.loadInventoryData(salesTable1, get_val);
    }//GEN-LAST:event_salesCategoryComboBoxActionPerformed

    private void adminSettingsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminSettingsLabelMouseClicked
        switchPanel(mainLayeredPane, adminPanel);
    }//GEN-LAST:event_adminSettingsLabelMouseClicked

    private void closeBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtn1ActionPerformed
        this.dispose();        
    }//GEN-LAST:event_closeBtn1ActionPerformed

    private void minimizeBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeBtn1ActionPerformed
        this.setExtendedState(this.ICONIFIED);
    }//GEN-LAST:event_minimizeBtn1ActionPerformed

    private void maximizeBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maximizeBtn1ActionPerformed
        if (this.getExtendedState() == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.NORMAL);
        } else {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        }
    }//GEN-LAST:event_maximizeBtn1ActionPerformed

    private void backLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backLabelMouseClicked
        switchPanel(mainLayeredPane, mainPanel);
    }//GEN-LAST:event_backLabelMouseClicked

    private void addCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCartBtnActionPerformed
        try {
            String itemName = itemNameTextField.getText();
            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an item name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double retailPrice = Double.parseDouble(priceSpinner.getValue().toString());
            double discount = Double.parseDouble(discountSpinner.getValue().toString());
            int quantity = (int) quantitySpinner.getValue();

            if (retailPrice <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Retail price and quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotalWithoutDiscount = retailPrice * quantity;
            double subtotal = subtotalWithoutDiscount;
            double total = (discount > 0) ? (retailPrice - (retailPrice * (discount / 100))) * quantity : subtotalWithoutDiscount;

            DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
            boolean itemExists = false;
            int rowIndex = -1;

            for (int i = 0; i < model.getRowCount(); i++) {
                if (itemName.equals(model.getValueAt(i, 0))) {
                    itemExists = true;
                    rowIndex = i;
                    break;
                }
            }

            if (itemExists) {
                int currentQuantity = (int) model.getValueAt(rowIndex, 3);
                double currentSubtotal = (double) model.getValueAt(rowIndex, 4);
                double currentTotal = (double) model.getValueAt(rowIndex, 5);

                double updatedSubtotal = currentSubtotal + subtotalWithoutDiscount;
                double updatedTotal = currentTotal + total;
                int updatedQuantity = currentQuantity + quantity;

                model.setValueAt(updatedQuantity, rowIndex, 3);
                model.setValueAt(updatedSubtotal, rowIndex, 4);
                model.setValueAt(updatedTotal, rowIndex, 5);
            } else {
                String formattedDiscount = (discount > 0) ? String.format("%.0f%%", discount) : "No discount";
                Object[] rowData = {itemName, retailPrice, formattedDiscount, quantity, subtotal, total};
                model.addRow(rowData);
            }
//            recieptTextArea.setText(SMT.generateReceipt(model));
            totalSpinner.setValue(0);
            itemNameTextField.setText("");
            descriptionTextField.setText("");
            priceSpinner.setValue(0);
            quantitySpinner.setValue(0);
            discountSpinner.setValue(0);
            
            double getTotal = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                getTotal += Double.parseDouble((model.getValueAt(i, 5)).toString());
            }
            String formattedTotal = String.format("%.2f", getTotal);
            getTotalTextField.setText(formattedTotal);
            
//            double get_total = Double.parseDouble(getTotalTextField.getText());
            if (Double.parseDouble(getTotalTextField.getText()) < 1) {
                receivedTextField.setEnabled(false);
            } else {
                receivedTextField.setEnabled(true);
            }
            
            try{
                String receivedTextVal = receivedTextField.getText();
            
                double receivedVal = Double.parseDouble(receivedTextVal);
                if (!receivedTextVal.isEmpty()) {
                    if (receivedVal < getTotal) {
                        payBtn.setEnabled(false);
                        printBtn.setEnabled(false);
                    } else {
                        payBtn.setEnabled(true);
                        printBtn.setEnabled(true); 
                    }
                } else {
                    payBtn.setEnabled(false);
                    printBtn.setEnabled(false);
                }
            }catch(NumberFormatException e){}
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_addCartBtnActionPerformed
   
    private void removeCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCartBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
        int selectedRow = salesTable2.getSelectedRow();
        String oldTotalVal = getTotalTextField.getText();
        String receivedTextVal = receivedTextField.getText();
        if (selectedRow >= 0) {
            double receivedVal = Double.parseDouble(receivedTextVal);
            double curTotalVal = Double.parseDouble(oldTotalVal);
            double minusVal = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());

            model.removeRow(selectedRow);

            curTotalVal -= minusVal;
            String formattedTotal = String.format("%.2f", curTotalVal);

            getTotalTextField.setText(formattedTotal);

            if (model.getRowCount() == 0) {
                recieptTextArea.setText("");
                invoiceTextField.setText("");
                receivedTextField.setText("");
                getBalanceTextField.setText("");
                payBtn.setEnabled(false);
                printBtn.setEnabled(false);
            }
            
            if (!receivedTextVal.isEmpty()) {
                if (receivedVal < curTotalVal || curTotalVal == 0) {
                    payBtn.setEnabled(false);
                    printBtn.setEnabled(false);
                } else {
                    payBtn.setEnabled(true);
                    printBtn.setEnabled(true);
                }
            } else {
                payBtn.setEnabled(false);
                printBtn.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_removeCartBtnActionPerformed

    private void promoteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promoteBtnActionPerformed
        if(usersTable.getSelectedRow() != -1){
            String get_id = Utilities.get_RecordID(usersTable).toString();
            short checkError = UMT.promoteUser(get_id);      
            switch (checkError) {
                case 1:
                    JOptionPane.showMessageDialog(this, "User promoted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 3:
                    JOptionPane.showMessageDialog(this, "Can't promote User! Position Exceed to limit", "Unable to promote", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Can't promote User!", "Unable to promote", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
            UMT.DisplayUserData(usersTable);
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.","Sales",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_promoteBtnActionPerformed

    private void demoteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demoteBtnActionPerformed
        if(usersTable.getSelectedRow() != -1){
            String get_id = Utilities.get_RecordID(usersTable).toString();
            short checkError = UMT.demoteUser(get_id); 
            switch (checkError) {
                case 1:
                    JOptionPane.showMessageDialog(this, "User demote successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 3:
                    JOptionPane.showMessageDialog(this, "Can't demote User! Position Exceed to limit", "Unable to promote", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Can't demote User!", "Unable to promote", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
            UMT.DisplayUserData(usersTable);
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.","Sales",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_demoteBtnActionPerformed

    private void adminDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminDeleteBtnActionPerformed
        if(Utilities.has_NoZeroVal(Utilities.get_RecordIDs(usersTable)) && usersTable.getSelectedRow() != -1){
            
            for(Object i : Utilities.get_RecordIDs(usersTable)){
                UMT.DeleteUser(i);
            }
            for(int i : usersTable.getSelectedRows()){
                String j = usersTable.getValueAt(i, 7).toString();
                if(!j.equals("nullProfile.jpg")){
                    ImageManagement.deleteImage(j);
                }
            }
            UMT.DisplayUserData(usersTable);
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Admin", JOptionPane.INFORMATION_MESSAGE);
        }        
    }//GEN-LAST:event_adminDeleteBtnActionPerformed
    
    
    
    private void uploadImageLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uploadImageLabelMouseClicked
        get_imgFile1 = ImageManagement.getImage(this);     
        if(get_imgFile1 != null){
            imageAvatar2.setIcon(new ImageIcon(get_imgFile1.getAbsolutePath()));
            imageAvatar2.repaint();
        }
    }//GEN-LAST:event_uploadImageLabelMouseClicked

    private void addUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserBtnActionPerformed
        String userId = Utilities.userIdGenerator(UMT.getHowManyUsers());
        String firstName = getFNameTextField.getText();
        String lastName = getLNameTextField.getText();
        String userName = getUNameTextField.getText();
        String password = String.valueOf(getPasswordPasswordField.getPassword());
        String confirmPassword = String.valueOf(getConfirmPasswordPasswordField.getPassword());
        String gender = (String) getGenderComboBox.getSelectedItem();
        
        File get_imgFile2 = ImageManagement.getDroppedFile();
        System.out.println("File 1: "+get_imgFile1);
        System.out.println("File 2: "+get_imgFile2);
        
        SimpleEntry<String, Path> imageEntry = new SimpleEntry<>("nullProfile.jpg", null);
        
        if (get_imgFile1 != null && get_imgFile2 == null) {
            imageEntry = ImageManagement.generateImageName(firstName, get_imgFile1);
        } else if (get_imgFile2 != null && get_imgFile1 == null) {
            imageEntry = ImageManagement.generateImageName(firstName, get_imgFile2);
        } else if (get_imgFile1 == null && get_imgFile2 == null) {
            
        } else {
            imageEntry = ImageManagement.generateImageName(firstName, get_imgFile1);
        }
        
        Object[] allValue = Utilities.getAllUserInput(userId, firstName, lastName, userName, password, confirmPassword, gender, getBirthDateDateChooser, imageEntry.getKey(),4,this);
        
        if(allValue != null){
            if(UMT.addUser(allValue)){
                if(imageEntry.getValue() != null){
                    if (get_imgFile1 != null && get_imgFile2 == null) {
                        ImageManagement.addTheImage(get_imgFile1, imageEntry.getValue());
                    } else if (get_imgFile2 != null && get_imgFile1 == null) {
                         ImageManagement.addTheImage(get_imgFile2, imageEntry.getValue());
                    } else if (get_imgFile1 == null && get_imgFile2 == null) {

                    } else {
                         ImageManagement.addTheImage(get_imgFile1, imageEntry.getValue());
                    }
                }
            }
            UMT.DisplayUserData(usersTable);
            getFNameTextField.setText("");
            getLNameTextField.setText("");
            getUNameTextField.setText("");
            getPasswordPasswordField.setText("");
            getConfirmPasswordPasswordField.setText("");
            addUserBtn.setEnabled(false);
            getBirthDateDateChooser.setDate(null);
            termServiceCheckBox.setSelected(false);
        }
    }//GEN-LAST:event_addUserBtnActionPerformed

    private void usernameLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usernameLabelMouseClicked
        String userInput = JOptionPane.showInputDialog(this, "Enter new username:", "Change username", JOptionPane.PLAIN_MESSAGE);

        String g_id = AppManagement.getCurrentUser(this);

        if (userInput != null) {
            if (Utilities.validateUsernameValue(userInput)) {
                UMT.updateUsername(userInput, g_id);
                usernameLabel.setText(userInput);
                UMT.DisplayUserData(usersTable);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username format. Please enter a valid username.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_usernameLabelMouseClicked

    private void genderLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genderLabelMouseClicked
        JComboBox<String> comboBox = new JComboBox<>(Helper.listOfGender);
        int optionChosen = JOptionPane.showOptionDialog(usersPanel,comboBox,"Change Gender",JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,new ImageIcon("src/icons/nogender_dark.png"),null,null);
        
        String get_currentUserId = AppManagement.getCurrentUser(this);
        
        if (optionChosen == JOptionPane.OK_OPTION) {
            String selectedOption = (String) comboBox.getSelectedItem();
            UMT.updateGender(selectedOption, get_currentUserId);
            
            ImageIcon get_gender_icon;
            if(UMT.getGender(get_currentUserId).equals(Helper.listOfGender[0])){
                get_gender_icon = new ImageIcon("src/icons/male.png");
            }else if(UMT.getGender(get_currentUserId).equals(Helper.listOfGender[1])){
                get_gender_icon = new ImageIcon("src/icons/female.png");
            }else{
                get_gender_icon = new ImageIcon("src/icons/nogender.png");
            }
            genderLabel.setIcon(get_gender_icon);
            UMT.DisplayUserData(usersTable);
        }
    }//GEN-LAST:event_genderLabelMouseClicked

    private void salesTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesTable1MouseClicked
         setSelectedItem();
    }//GEN-LAST:event_salesTable1MouseClicked
    
    private void setSelectedItem(){
        try{
            int getSelectedRow = salesTable1.getSelectedRow();
            itemNameTextField.setText(salesTable1.getValueAt(getSelectedRow, 1).toString());
            descriptionTextField.setText(salesTable1.getValueAt(getSelectedRow, 2).toString());

            int quantityMax = Integer.parseInt(salesTable1.getValueAt(getSelectedRow, 3).toString());

            if (quantityMax == 0) {
                JOptionPane.showMessageDialog(this, "Out of stock");
            } else {
                SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, quantityMax, 1);
                quantitySpinner.setModel(spinnerModel1);

                Object priceValue = salesTable1.getValueAt(getSelectedRow, 4);
                priceSpinner.setValue(priceValue);
            }
        }catch(NullPointerException e){
            
        }
    }
    
    private void quantitySpinnerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_quantitySpinnerMouseClicked

    }//GEN-LAST:event_quantitySpinnerMouseClicked

    private double calculateTotalPrice(int quantity) {
        int getSelectedRow = salesTable1.getSelectedRow();
        double unitPrice = Double.parseDouble(salesTable1.getValueAt(getSelectedRow, 4).toString());
        return unitPrice * quantity;
    }    

    private void quantitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerStateChanged
        try{
            int newQuantity = (Integer) quantitySpinner.getValue();
            double totalPrice = calculateTotalPrice(newQuantity);
            totalSpinner.setValue(totalPrice);
        }catch(ArrayIndexOutOfBoundsException e){
            JOptionPane.showMessageDialog(this, "No product is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
            SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 0, 1);
            quantitySpinner.setModel(spinnerModel1);
        }
    }//GEN-LAST:event_quantitySpinnerStateChanged

    private void discountSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_discountSpinnerStateChanged
        
    }//GEN-LAST:event_discountSpinnerStateChanged

    private void payBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
        
        String getINV = SMT.generateInvoiceNumber();
        invoiceTextField.setText(getINV); 
        
        String getDate = Utilities.getCurrentDate(dateCHooser);
        Object dateRec = Utilities.get_AddedDate(dateCHooser);
        recieptTextArea.setText(SMT.generateReceipt(model,getDate,getINV));
        
        for (int i = 0; i < model.getRowCount(); i++) {
            
            IMT.reduceProductQuantity(model.getValueAt(i, 3), model.getValueAt(i, 0));
            
            Object item = model.getValueAt(i, 0);
            Object discountPercent = Utilities.convertPercentageToNumber(model.getValueAt(i, 2).toString());
            Object quantity = model.getValueAt(i, 3);
            Object subtotal = model.getValueAt(i, 4);
            Object total = model.getValueAt(i, 5);
            
            RMT.recordPurchase(invoiceTextField.getText(), item, discountPercent,quantity,subtotal,total,dateRec);
            System.out.println();
        }
        
        IMT.DisplayInventoryData(inventoryTable);
        String get_val = (String) salesCategoryComboBox.getSelectedItem(); 
        
        SMT.loadInventoryData(salesTable1, get_val);
        
        double total = Double.parseDouble(getTotalTextField.getText());
        double received = Double.parseDouble(receivedTextField.getText());
        
        double balance = received - total;

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = "₱ " + decimalFormat.format(balance);

        getBalanceTextField.setText(formattedBalance);
    }//GEN-LAST:event_payBtnActionPerformed

    private void inventoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryTableMouseClicked
        inventoryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(CMT.AddElementToComboBox())));
        inventoryTable.getColumnModel().getColumn(4).setCellEditor(new Utilities.IntCustomJSpinner());
        inventoryTable.getColumnModel().getColumn(5).setCellEditor(new Utilities.DoubleCustomJSpinner());  
    }//GEN-LAST:event_inventoryTableMouseClicked

    private void termServiceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_termServiceCheckBoxActionPerformed
        if(termServiceCheckBox.isSelected()){
            addUserBtn.setEnabled(true);
        }else{
            addUserBtn.setEnabled(false);
        }
    }//GEN-LAST:event_termServiceCheckBoxActionPerformed

    private void showPasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPasswordCheckBoxActionPerformed
        if(showPasswordCheckBox.isSelected()){
            getPasswordPasswordField.setEchoChar((char) 0);
            getConfirmPasswordPasswordField.setEchoChar((char) 0);
        }else{
            getPasswordPasswordField.setEchoChar('*');
            getConfirmPasswordPasswordField.setEchoChar('*');
        }
    }//GEN-LAST:event_showPasswordCheckBoxActionPerformed
    private ProfileHandler profileHandler;
    int countState = 0;
    private void imageAvatar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar1MouseClicked
        if(countState < 1){
            String get_id = AppManagement.getCurrentUser(this);
        
            String get_fname = UMT.getFName(get_id);
            String get_image = UMT.getImagePath(get_id);

            if (profileHandler == null) {
                profileHandler = new ProfileHandler(imageAvatar1.getIcon(), get_id, get_image);
                profileHandler.setVisible(true);

                profileHandler.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        Icon get_icon = profileHandler.returnIcon();
                        if (get_icon != null) {
                            imageAvatar1.setIcon(get_icon);
                            imageAvatar1.repaint();
                        }
                        if(profileHandler.changedState()){
                            countState++;
                        }
                        profileHandler = null; 
                    }
                });
            } else {
                if (profileHandler.getState() == Frame.ICONIFIED) {
                    profileHandler.setState(Frame.NORMAL); 
                } else {
                    profileHandler.setState(Frame.ICONIFIED); 
                }
            }
        }else{
                Object[] options = {"View Image", "Do Not View Image"};
                int choice = JOptionPane.showOptionDialog(null,
                            "Close the Application to Change Profile Again",
                            "Profile",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]);

                if (choice == JOptionPane.YES_OPTION) {
                    ImageManagement.openImage(imageAvatar1.getIcon());
                }
        }
    }//GEN-LAST:event_imageAvatar1MouseClicked

    private void imageAvatar2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar2MouseEntered
        
    }//GEN-LAST:event_imageAvatar2MouseEntered

    private void imageAvatar2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar2MouseExited
        
    }//GEN-LAST:event_imageAvatar2MouseExited

    private void imageAvatar1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar1MouseEntered
        imageAvatar1.setForeground(new Color(238,242,255));
    }//GEN-LAST:event_imageAvatar1MouseEntered

    private void imageAvatar1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar1MouseExited
        imageAvatar1.setForeground(new Color(67,56,202));
    }//GEN-LAST:event_imageAvatar1MouseExited

    private void receivedTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_receivedTextFieldKeyReleased
        try{
            double total = Double.parseDouble(getTotalTextField.getText());
            double received = Double.parseDouble(receivedTextField.getText());

            if (total > received) {
                payBtn.setEnabled(false);
            } else {
                payBtn.setEnabled(true);
            }
        }catch(NumberFormatException e){
                
        }
    }//GEN-LAST:event_receivedTextFieldKeyReleased

    private void receivedTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_receivedTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != KeyEvent.VK_PERIOD && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE
                && (c == '.' && receivedTextField.getText().contains("."))) {
            evt.consume(); 
        }
    }//GEN-LAST:event_receivedTextFieldKeyTyped

    private void totalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_totalSpinnerStateChanged
        try{
            int newQuantity = (Integer) quantitySpinner.getValue();
            double totalPrice = calculateTotalPrice(newQuantity);
            totalSpinner.setValue(totalPrice);
        }catch(ArrayIndexOutOfBoundsException e){
            JOptionPane.showMessageDialog(this, "No product is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
            SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 0, 1);
            totalSpinner.setModel(spinnerModel1);
        }
    }//GEN-LAST:event_totalSpinnerStateChanged

    private void titleLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleLabelMouseClicked
         
    }//GEN-LAST:event_titleLabelMouseClicked

    private void testBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testBtn1ActionPerformed
      
    }//GEN-LAST:event_testBtn1ActionPerformed

    private void printBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBtnActionPerformed
        System.out.println(recieptTextArea.getText());
    }//GEN-LAST:event_printBtnActionPerformed

    private void printBtnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_printBtnStateChanged

    }//GEN-LAST:event_printBtnStateChanged

    private void payBtnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_payBtnStateChanged
        if(payBtn.isEnabled()) {
            printBtn.setEnabled(true);
        } else {
            printBtn.setEnabled(false);
        }
    }//GEN-LAST:event_payBtnStateChanged

    private void inventoryTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_inventoryTablePropertyChange
        
    }//GEN-LAST:event_inventoryTablePropertyChange

    private void returnItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnItemBtnActionPerformed
        String getInvN = invoicenumtextField.getText();
        System.out.println(getInvN);
    }//GEN-LAST:event_returnItemBtnActionPerformed

    private void returnItemSearchBarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_returnItemSearchBarKeyReleased
        RMT.searchPurchasedData(returnitemTable,  returnItemSearchBar.getText());
    }//GEN-LAST:event_returnItemSearchBarKeyReleased

    private void returnitemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnitemTableMouseClicked

    }//GEN-LAST:event_returnitemTableMouseClicked

    private void adminEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminEditBtnActionPerformed
        int getSelectedRow = usersTable.getSelectedRow();
        int getSelectedColumn = usersTable.getSelectedColumn();
        
        if(getSelectedColumn != -1 || getSelectedRow != -1){
            Object getNewVal = null;
        

            if(getSelectedColumn == 6){
                getNewVal = Utilities.getComboxFromTable(usersTable, 6);
            }else{
                getNewVal = Utilities.getSpinnerFromTable(usersTable);
            }

            if(getNewVal != null){
                UMT.EditUserData(getNewVal, getSelectedColumn, Utilities.get_RecordID(usersTable));
                UMT.DisplayUserData(usersTable);
            }
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Admin", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_adminEditBtnActionPerformed

    private void usersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersTableMouseClicked
        usersTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox(Helper.listOfGender)));
        
    }//GEN-LAST:event_usersTableMouseClicked

    private void addCartBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCartBtn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addCartBtn1ActionPerformed

    public static void switchPanel(JLayeredPane layered, JPanel panel){
        layered.removeAll();
        layered.add(panel);
        layered.repaint();
        layered.revalidate();         
    }

    public void labelActions(PanelRound backPanel,JLabel label, JLayeredPane layered, PanelRound panel){
        label.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                backPanel.setBackground(Helper.colors[5]);
                switchPanel(layered, panel);
                backPanel.repaint();      
                recordInit();
            }
            
            @Override
            public void mouseReleased(MouseEvent e){
                backPanel.setBackground(Helper.colors[6]);
                
                backPanel.repaint();                        
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                backPanel.setBackground(Helper.colors[4]);
                
                backPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backPanel.setBackground(Helper.colors[6]);
                
                backPanel.repaint();        
            }
        });
    }
    
    public void labelActionNoBack(JLabel label,Color[] c){
        label.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                label.setForeground(c[2]);
            }
            
            @Override
            public void mouseReleased(MouseEvent e){     
                label.setForeground(c[0]);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(c[1]);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(c[0]);
            }
        });
    }
    
    public void logoutLabelActions(PanelRound backPanel,JLabel label){
        label.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                backPanel.setBackground(Helper.colors[7]);
                backPanel.repaint();                
            }
            
            @Override
            public void mouseReleased(MouseEvent e){
                backPanel.setBackground(Helper.colors[6]);
                
                backPanel.repaint();                        
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                backPanel.setBackground(Helper.colors[1]);
                
                backPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backPanel.setBackground(Helper.colors[6]);
                
                backPanel.repaint();        
            }
        });
    }
    
    public void backLabelActions(JLabel label,String[] iconName){
        label.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                label.setIcon(new ImageIcon("src/icons/"+iconName[2]));
                label.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e){
                label.setIcon(new ImageIcon("src/icons/"+iconName[0]));
                label.repaint();                       
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setIcon(new ImageIcon("src/icons/"+iconName[1]));
                label.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setIcon(new ImageIcon("src/icons/"+iconName[0]));
                label.repaint();       
            }
        });
    }
    
    public void sideBarMenu(){
        labelActions(dashboardBack, dashboardLabel, contentLayeredPane, dashboardPanel);
        labelActions(usersBack, usersLabel, contentLayeredPane, usersPanel);
        labelActions(inventoryBack, inventoryLabel, contentLayeredPane, inventoryPanel);
        labelActions(categoryBack, categoryLabel, contentLayeredPane, categoryPanel);
        labelActions(salesBack, salesLabel, contentLayeredPane, salesPanel);
        labelActions(returnItemBack, returnItemLabel, contentLayeredPane, returnItemPanel);
        labelActions(priceListBack, priceListLabel, contentLayeredPane, priceListPanel);
        labelActions(reportBack, reportLabel, contentLayeredPane, reportPanel);
        labelActions(settingsBack, settingsLabel, contentLayeredPane, settingsPanel);
        logoutLabelActions(logoutBack, logoutLabel);
        
        labelActions(dashboardBack1, dashboardLabel1, contentLayeredPane, dashboardPanel);
        labelActions(usersBack2, usersLabel2, contentLayeredPane, usersPanel);
        labelActions(salesBack1, salesLabel1, contentLayeredPane, salesPanel);
        labelActions(returnItemBack1, returnItemLabel1, contentLayeredPane, returnItemPanel);
        labelActions(priceListBack1, priceListLabel1, contentLayeredPane, priceListPanel);
        labelActions(settingsBack2, settingsLabel2, contentLayeredPane, settingsPanel);
        logoutLabelActions(logoutBack1, logoutLabel1);       
    
        labelActionNoBack(adminSettingsLabel,new Color[]{Helper.fontColors[0],Helper.colors[3],Helper.fontColors[0]});
//        labelActionNoBack(changePasswordLabel,new Color[]{Helper.fontColors[0],Helper.colors[3],Helper.fontColors[0]});
        labelActionNoBack(usernameLabel,new Color[]{Helper.fontColors[0],Helper.colors[6],Helper.fontColors[0]});
        labelActionNoBack(uploadImageLabel,new Color[]{Helper.colors[8],Helper.colors[4],Helper.colors[9]});
        
        backLabelActions(backLabel, new String[]{"back2.png","back1.png","back3.png"});
    }
    
    public void close_min_max_Screen(CircleButton closeBtn, CircleButton minBtn, CircleButton maxBtn){
        closeBtn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                ImageIcon icon = new ImageIcon("src/icons/close.png");
                closeBtn.setIcon(icon);
                closeBtn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e){
                closeBtn.setIcon(null);
                closeBtn.repaint();             
            }
        });
        
        minBtn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                ImageIcon icon = new ImageIcon("src/icons/minimize.png");
                minBtn.setIcon(icon);
                minBtn.repaint();              
            }

            @Override
            public void mouseExited(MouseEvent e){
                minBtn.setIcon(null);
                minBtn.repaint();               
            }
        });
        
        maxBtn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                ImageIcon icon = new ImageIcon("src/icons/maximize.png");
                maxBtn.setIcon(icon);
                maxBtn.repaint();                 
            }

            @Override
            public void mouseExited(MouseEvent e){
                maxBtn.setIcon(null);
                maxBtn.repaint();               
            }
        });        
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCartBtn;
    private javax.swing.JButton addCartBtn1;
    private javax.swing.JButton addUserBtn;
    private javax.swing.JButton adminDeleteBtn;
    private javax.swing.JButton adminEditBtn;
    private customComponents.PanelRound adminPanel;
    private javax.swing.JLabel adminSettingsLabel;
    private javax.swing.JLabel backLabel;
    private javax.swing.JButton categoryAddBtn;
    private customComponents.PanelRound categoryBack;
    private javax.swing.JButton categoryDeleteBtn;
    private javax.swing.JButton categoryEditBtn;
    private javax.swing.JTextField categoryInput;
    private javax.swing.JLabel categoryLabel;
    private customComponents.PanelRound categoryPanel;
    private customComponents.TextFieldWithIcon categorySearchBar;
    private javax.swing.JTable categoryTable;
    private customComponents.CircleButton closeBtn;
    private customComponents.CircleButton closeBtn1;
    private javax.swing.JPanel close_min_max_Panel;
    private javax.swing.JPanel close_min_max_Panel1;
    private javax.swing.JLayeredPane contentLayeredPane;
    private customComponents.PanelRound d1;
    private customComponents.PanelRound d2;
    private customComponents.PanelRound d3;
    private customComponents.PanelRound d4;
    private customComponents.PanelRound d5;
    private customComponents.PanelRound d6;
    private customComponents.PanelRound dashboardBack;
    private customComponents.PanelRound dashboardBack1;
    private javax.swing.JLabel dashboardLabel;
    private javax.swing.JLabel dashboardLabel1;
    private customComponents.PanelRound dashboardPanel;
    private com.toedter.calendar.JDateChooser dateCHooser;
    private javax.swing.JButton demoteBtn;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JSpinner discountSpinner;
    private javax.swing.JLabel fullnameLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JTextField getBalanceTextField;
    private com.toedter.calendar.JDateChooser getBirthDateDateChooser;
    private javax.swing.JPasswordField getConfirmPasswordPasswordField;
    private javax.swing.JPasswordField getConfirmPasswordPasswordField1;
    private javax.swing.JTextField getFNameTextField;
    private javax.swing.JComboBox<String> getGenderComboBox;
    private javax.swing.JTextField getLNameTextField;
    private javax.swing.JPasswordField getPasswordPasswordField;
    private javax.swing.JPasswordField getPasswordPasswordField1;
    private javax.swing.JTextField getTotalTextField;
    private javax.swing.JTextField getUNameTextField;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JTextField im1;
    private javax.swing.JTextField im2;
    private javax.swing.JSpinner im3;
    private javax.swing.JComboBox<String> im4;
    private javax.swing.JSpinner im5;
    private com.toedter.calendar.JDateChooser im6;
    private customComponents.ImageAvatar imageAvatar1;
    private customComponents.ImageAvatar imageAvatar2;
    private javax.swing.JButton inventoryAddBtn;
    private customComponents.PanelRound inventoryBack;
    private javax.swing.JButton inventoryDeleteBtn;
    private javax.swing.JButton inventoryEditBtn;
    private javax.swing.JLabel inventoryLabel;
    private customComponents.PanelRound inventoryPanel;
    private customComponents.TextFieldWithIcon inventorySearchBar;
    private javax.swing.JTable inventoryTable;
    private javax.swing.JTextField invoiceTextField;
    private javax.swing.JTextField invoicenumtextField;
    private javax.swing.JTextField itemNameTextField;
    private javax.swing.JLabel ivnLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private customComponents.PanelRound logoutBack;
    private customComponents.PanelRound logoutBack1;
    private javax.swing.JLabel logoutLabel;
    private javax.swing.JLabel logoutLabel1;
    private javax.swing.JLayeredPane mainLayeredPane;
    private javax.swing.JPanel mainPanel;
    private customComponents.CircleButton maximizeBtn;
    private customComponents.CircleButton maximizeBtn1;
    private javax.swing.JLayeredPane menuLayeredPane;
    private customComponents.PanelRound menuPanel1;
    private customComponents.PanelRound menuPanel2;
    private customComponents.CircleButton minimizeBtn;
    private customComponents.CircleButton minimizeBtn1;
    private javax.swing.JLabel outStockLabel;
    private customComponents.PanelRound panelRound1;
    private customComponents.PanelRound panelRound10;
    private customComponents.PanelRound panelRound11;
    private customComponents.PanelRound panelRound12;
    private customComponents.PanelRound panelRound13;
    private customComponents.PanelRound panelRound14;
    private customComponents.PanelRound panelRound15;
    private customComponents.PanelRound panelRound16;
    private customComponents.PanelRound panelRound17;
    private customComponents.PanelRound panelRound18;
    private customComponents.PanelRound panelRound19;
    private customComponents.PanelRound panelRound2;
    private customComponents.PanelRound panelRound20;
    private customComponents.PanelRound panelRound21;
    private customComponents.PanelRound panelRound22;
    private customComponents.PanelRound panelRound23;
    private customComponents.PanelRound panelRound3;
    private customComponents.PanelRound panelRound4;
    private customComponents.PanelRound panelRound5;
    private customComponents.PanelRound panelRound6;
    private customComponents.PanelRound panelRound7;
    private customComponents.PanelRound panelRound8;
    private customComponents.PanelRound panelRound9;
    private javax.swing.JButton payBtn;
    private customComponents.PanelRound priceListBack;
    private customComponents.PanelRound priceListBack1;
    private javax.swing.JLabel priceListLabel;
    private javax.swing.JLabel priceListLabel1;
    private customComponents.PanelRound priceListPanel;
    private javax.swing.JSpinner priceSpinner;
    private javax.swing.JButton printBtn;
    private javax.swing.JButton promoteBtn;
    private javax.swing.JSpinner quantitySpinner;
    private javax.swing.JTextField receivedTextField;
    private javax.swing.JTextArea recieptTextArea;
    private javax.swing.JButton removeCartBtn;
    private customComponents.PanelRound reportBack;
    private javax.swing.JLabel reportLabel;
    private customComponents.PanelRound reportPanel;
    private customComponents.PanelRound returnItemBack;
    private customComponents.PanelRound returnItemBack1;
    private javax.swing.JButton returnItemBtn;
    private javax.swing.JLabel returnItemLabel;
    private javax.swing.JLabel returnItemLabel1;
    private customComponents.PanelRound returnItemPanel;
    private customComponents.TextFieldWithIcon returnItemSearchBar;
    private javax.swing.JTable returnitemTable;
    private customComponents.PanelRound salesBack;
    private customComponents.PanelRound salesBack1;
    private javax.swing.JComboBox<String> salesCategoryComboBox;
    private javax.swing.JLabel salesLabel;
    private javax.swing.JLabel salesLabel1;
    private customComponents.PanelRound salesPanel;
    private javax.swing.JTable salesTable1;
    private javax.swing.JTable salesTable2;
    private customComponents.PanelRound settingsBack;
    private customComponents.PanelRound settingsBack2;
    private javax.swing.JLabel settingsLabel;
    private javax.swing.JLabel settingsLabel2;
    private customComponents.PanelRound settingsPanel;
    private javax.swing.JScrollPane settingsScrillPane;
    private javax.swing.JCheckBox showPasswordCheckBox;
    private javax.swing.JLabel soldOldLabel;
    private javax.swing.JLabel soldTotayLabel;
    private javax.swing.JCheckBox termServiceCheckBox;
    private javax.swing.JButton testBtn1;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel titleLabel1;
    private javax.swing.JLabel todaysalesLabel;
    private javax.swing.JLabel totalProductsLabel;
    private javax.swing.JLabel totalSalesLabel;
    private javax.swing.JSpinner totalSpinner;
    private javax.swing.JLabel uploadImageLabel;
    private customComponents.PanelRound uploadPanel1;
    private javax.swing.JLabel usernameLabel;
    private customComponents.PanelRound usersBack;
    private customComponents.PanelRound usersBack2;
    private javax.swing.JLabel usersLabel;
    private javax.swing.JLabel usersLabel2;
    private customComponents.PanelRound usersPanel;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
//Hi there, I'm Josuan, a second-year BSIT student, and I'm working on my final project for Information Management 1 Subject. 
//This project involves using the Java programming language to implement the 
//Create, Read, Update, and Delete (CRUD) functionalities.