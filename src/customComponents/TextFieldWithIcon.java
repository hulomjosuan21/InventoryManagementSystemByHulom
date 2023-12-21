package customComponents;

import javax.swing.*;
import java.awt.*;

public class TextFieldWithIcon extends JTextField {

    private Icon prefixIcon;
    private Icon suffixIcon;

    public Icon getPrefixIcon() {
        return prefixIcon;
    }

    public void setPrefixIcon(Icon prefixIcon) {
        this.prefixIcon = prefixIcon;
        updateIcons();
    }

    public Icon getSuffixIcon() {
        return suffixIcon;
    }

    public void setSuffixIcon(Icon suffixIcon) {
        this.suffixIcon = suffixIcon;
        updateIcons();
    }

    public TextFieldWithIcon() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintIcon(g);
    }

    private void paintIcon(Graphics g) {
        if (prefixIcon != null) {
            Image prefix = ((ImageIcon) prefixIcon).getImage();
            int y = (getHeight() - prefixIcon.getIconHeight()) / 2;
            g.drawImage(prefix, 3, y, this);
        }
        if (suffixIcon != null) {
            Image suffix = ((ImageIcon) suffixIcon).getImage();
            int y = (getHeight() - suffixIcon.getIconHeight()) / 2;
            g.drawImage(suffix, getWidth() - suffixIcon.getIconWidth() - 3, y, this);
        }
    }

    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        int left = (prefixIcon != null) ? prefixIcon.getIconWidth() + 6 : insets.left;
        int right = (suffixIcon != null) ? suffixIcon.getIconWidth() + 6 : insets.right;
        return new Insets(insets.top, left, insets.bottom, right);
    }

    private void updateIcons() {
        revalidate();
        repaint();
    }
}
