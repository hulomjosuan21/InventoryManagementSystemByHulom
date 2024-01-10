import DbOperations.*;
import com.formdev.flatlaf.IntelliJTheme;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class LoginFrame extends javax.swing.JFrame {
    private final DbConnection DBC = new DbConnection();
    private final UserManagement UMT = new UserManagement(this);
    
    public LoginFrame() {
        initComponents();
        
        Image appIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/loginPage.png"));
        this.setIconImage(appIcon);
        
        setBackground(new Color(0,0,0,0));
        close_min_max_Screen();     
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainLayeredPane = new javax.swing.JLayeredPane();
        loginPanel = new customComponents.PanelRound();
        panelRound1 = new customComponents.PanelRound();
        logInBtn = new customComponents.ButtonRound();
        getPassword = new customComponents.PasswordField();
        getUserName = new customComponents.TextField();
        panelRound2 = new customComponents.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        closeBtn = new customComponents.CircleButton();
        minimizeBtn = new customComponents.CircleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        loginPanel.setBackground(new java.awt.Color(224, 231, 255));
        loginPanel.setRoundBottomLeft(25);
        loginPanel.setRoundBottomRight(25);
        loginPanel.setRoundTopLeft(25);
        loginPanel.setRoundTopRight(25);

        panelRound1.setBackground(new java.awt.Color(165, 180, 252));
        panelRound1.setRoundBottomLeft(25);
        panelRound1.setRoundTopLeft(25);

        logInBtn.setBackground(new java.awt.Color(79, 70, 229));
        logInBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/login.png"))); // NOI18N
        logInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logInBtnActionPerformed(evt);
            }
        });

        getPassword.setBackground(new java.awt.Color(165, 180, 252));
        getPassword.setForeground(new java.awt.Color(255, 255, 255));
        getPassword.setCaretColor(new java.awt.Color(79, 70, 229));
        getPassword.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getPassword.setLabelText("Password");
        getPassword.setLineColor(new java.awt.Color(79, 70, 229));
        getPassword.setSelectionColor(new java.awt.Color(79, 70, 229));
        getPassword.setShowAndHide(true);
        getPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getPasswordActionPerformed(evt);
            }
        });

        getUserName.setBackground(new java.awt.Color(165, 180, 252));
        getUserName.setForeground(new java.awt.Color(255, 255, 255));
        getUserName.setCaretColor(new java.awt.Color(79, 70, 229));
        getUserName.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getUserName.setLabelText("Username");
        getUserName.setLineColor(new java.awt.Color(79, 70, 229));
        getUserName.setSelectionColor(new java.awt.Color(79, 70, 229));
        getUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getUserNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(getUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(logInBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(getUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(getPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(logInBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
        );

        panelRound2.setBackground(new java.awt.Color(79, 70, 229));
        panelRound2.setRoundBottomRight(25);
        panelRound2.setRoundTopRight(25);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LOG IN");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setOpaque(false);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minimizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(closeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
            .addComponent(minimizeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        mainLayeredPane.setLayer(loginPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout mainLayeredPaneLayout = new javax.swing.GroupLayout(mainLayeredPane);
        mainLayeredPane.setLayout(mainLayeredPaneLayout);
        mainLayeredPaneLayout.setHorizontalGroup(
            mainLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainLayeredPaneLayout.setVerticalGroup(
            mainLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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
        System.exit(0);
    }//GEN-LAST:event_closeBtnActionPerformed

    private void minimizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeBtnActionPerformed
        this.setExtendedState(this.ICONIFIED);
    }//GEN-LAST:event_minimizeBtnActionPerformed

    private void getPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPasswordActionPerformed
        String get_username = getUserName.getText();
        char[] get_p = getPassword.getPassword();
        String get_password = new String(get_p);
        
        
        loginMethod(get_username,get_password);
    }//GEN-LAST:event_getPasswordActionPerformed
   
    private void logInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInBtnActionPerformed
        String get_username = getUserName.getText();
        char[] get_p = getPassword.getPassword();
        String get_password = new String(get_p);
        
        
        loginMethod(get_username,get_password);
    }//GEN-LAST:event_logInBtnActionPerformed

    private void getUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getUserNameActionPerformed
        String get_username = getUserName.getText();
        char[] get_p = getPassword.getPassword();
        String get_password = new String(get_p);
        
        
        loginMethod(get_username,get_password);
    }//GEN-LAST:event_getUserNameActionPerformed

    private void loginMethod(String u, String p){
        short result = UMT.checkUserCredentials(u, p);

        switch (result) {
            case 1:
                JOptionPane.showMessageDialog(this, "Login successful!");
                String current_user = UMT.getUserId(u, p);
                AppManagement.setCurrentUser(current_user, this);
                System.out.println("Current user: "+current_user);            
                
                Application newApp = new Application();    
                newApp.setVisible(true);
                this.dispose();
                break;

            case 2:
                JOptionPane.showMessageDialog(this, "Incorrect password!");
                break;
            case 3:
                JOptionPane.showMessageDialog(this, "Incorrect username!");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid username and password!");
                break;
        }       
    }
    
//    public static void main(String args[]) {
//        IntelliJTheme.setup(Application.class.getResourceAsStream("/theme_eclipse.theme.json"));
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new LoginFrame().setVisible(true);
//            }
//        });
//    }
    
    public void close_min_max_Screen(){
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
        
        minimizeBtn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                ImageIcon icon = new ImageIcon("src/icons/minimize.png");
                minimizeBtn.setIcon(icon);
                minimizeBtn.repaint();              
            }

            @Override
            public void mouseExited(MouseEvent e){
                minimizeBtn.setIcon(null);
                minimizeBtn.repaint();               
            }
        });    
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private customComponents.CircleButton closeBtn;
    private customComponents.PasswordField getPassword;
    private customComponents.TextField getUserName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private customComponents.ButtonRound logInBtn;
    private customComponents.PanelRound loginPanel;
    private javax.swing.JLayeredPane mainLayeredPane;
    private customComponents.CircleButton minimizeBtn;
    private customComponents.PanelRound panelRound1;
    private customComponents.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables
}
