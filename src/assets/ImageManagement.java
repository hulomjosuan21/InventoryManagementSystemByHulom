package assets;

import customComponents.ImageAvatar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageManagement {
    public static String makeImageName(String name) {
        String lowercaseName = name.toLowerCase().replace(" ", "_");
        return java.sql.Date.valueOf(LocalDate.now()) + "_" + lowercaseName + ".jpg";
    }

    public static File getImage(Component p_c) {
        JFileChooser fileChooser = new JFileChooser();
        String picturesDir = System.getProperty("user.home");
        fileChooser.setCurrentDirectory(new File(picturesDir)); 
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(p_c);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static String addImages(String name, File g_img) {
        File selectedFile = g_img;

        if (selectedFile != null) {
            String imageName = makeImageName(name);
            Path destination = Paths.get("src/images", imageName);

            try {
                if (Files.exists(destination)) {
                    String baseName = imageName.substring(0, imageName.lastIndexOf('.'));
                    String extension = imageName.substring(imageName.lastIndexOf('.'));
                    int count = 1;

                    while (Files.exists(destination)) {
                        imageName = baseName + "_(" + count + ")" + extension;
                        destination = Paths.get("src/images", imageName);
                        count++;
                    }
                }

                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                return imageName;
            } catch (IOException e) {
                return "nullProfile.jpg";
            }
        }
        return "nullProfile.jpg";
    }
    
    public static void addNewImage(String name, File imgFile) {
        String imageName = makeImageName(name);
        Path destination = Paths.get("src/images", imageName);

        try {
            if (!imgFile.getName().toLowerCase().endsWith(".jpg")) {
                BufferedImage image = ImageIO.read(imgFile);
                File newFile = new File(imgFile.getParent(), imageName + ".jpg");
                ImageIO.write(image, "jpg", newFile);
                destination = Paths.get("src/images", newFile.getName());
            }

            Files.copy(imgFile.toPath(), destination);

        } catch (IOException e) {
            e.printStackTrace();
        }
}  
    
    public static void deleteImage(Icon icon) {
        String imagePath = icon.toString(); 
        System.out.println(imagePath);
        File imageFile = new File(imagePath);
        
        if (imageFile.exists()) {
            boolean deleted = imageFile.delete();
            if (deleted) {
                System.out.println("Image deleted successfully!");
            } else {
                System.out.println("Failed to delete the image.");
            }
        } else {
            System.out.println("Image file not found.");
        }      
    }   
    
    public static void insertImage(File newImageFile, String newImageName) {
        File sourceDir = new File("src/images");
        File destinationFile = new File(sourceDir, newImageName+".jpg");

        try {
            Files.copy(newImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Image inserted successfully to src/images as " + newImageName);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void setImageToAvatar(ImageAvatar imgAvatar, String imageName){
        ImageIcon img = new ImageIcon("src/images/"+imageName);
        imgAvatar.setIcon(img);
        imgAvatar.repaint();
    }
    
    private static File getdroppedFile;
    
    public static File getDroppedFile() {
        return getdroppedFile;
    }
    
    public static void setupFileDragAndDrop(customComponents.PanelRound uploadPanel, Color dropColor, customComponents.ImageAvatar ImgAvatar) {
        final Color defaultColor = uploadPanel.getBackground();
        uploadPanel.setBackground(defaultColor);
        
        uploadPanel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }

                try {
                    Transferable transferable = support.getTransferable();
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() > 0) {
                        File droppedFile = files.get(0);
                        if (!isJPEGFile(droppedFile)) {
                            showFileFormatError();
                            return false;
                        }
                        return true;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                Transferable transferable = support.getTransferable();
                try {
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() > 0) {
                        File droppedFile = files.get(0);
                        handleDroppedFile(droppedFile,ImgAvatar);
                        return true;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }

            private boolean isJPEGFile(File file) {
                String fileName = file.getName();
                return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
            }

            private void showFileFormatError() {
                JOptionPane.showMessageDialog(null, "Only .jpg and .jpeg files are accepted.", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
            }
        });
        uploadPanel.setDropTarget(new DropTarget(uploadPanel, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            public void dragEnter(DropTargetDragEvent event) {
                uploadPanel.setBackground(dropColor);
                uploadPanel.repaint();
            }

            public void dragExit(DropTargetEvent event) {
                uploadPanel.setBackground(defaultColor);
                uploadPanel.repaint();    
            }

            public void drop(DropTargetDropEvent event) {
                uploadPanel.setBackground(defaultColor);
                uploadPanel.repaint();                
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        if (files.size() > 0) {
                            File droppedFile = files.get(0);
                            if (isJPEGFile(droppedFile)) {
                                handleDroppedFile(droppedFile,ImgAvatar);
                                getdroppedFile = droppedFile;
                            } else {
                                showFileFormatError();
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                event.dropComplete(true);
            }
            private boolean isJPEGFile(File file) {
                String fileName = file.getName();
                return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
            }
            private void showFileFormatError() {
                JOptionPane.showMessageDialog(null, "Only .jpg and .jpeg files are accepted.", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
            }
        }));
    }
    
    public static void handleDroppedFile(File file,customComponents.ImageAvatar ImgAvatar) {
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        if (icon != null && icon.getIconWidth() > 0) {
            ImgAvatar.setIcon(icon);
            ImgAvatar.repaint();
        } else {
            JOptionPane.showMessageDialog(null, file.getAbsolutePath(), "Invalid file", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void openImage(Icon getImage){
        String imagePath = getImage.toString();
        
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists() && (imageFile.isFile() || imageFile.canRead())) {
                Desktop.getDesktop().open(imageFile);
            } else {
                System.err.println("File does not exist or cannot be read.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
}
