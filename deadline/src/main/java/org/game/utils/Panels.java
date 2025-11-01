package org.game.utils;

import lombok.Getter;
import lombok.Setter;
import org.game.entity.ClassType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class Panels {
    private Panels(){}

    public static void drawNameBox(Graphics2D g2d, String name, int x, int y, int tileSize) {

        Object aaHint = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        Font original = g2d.getFont();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = new Font("Cascadia Code", Font.BOLD, 12);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);

        int padding = 6;
        int textW = fm.stringWidth(name);
        int textH = fm.getHeight();

        int boxW = textW + padding * 2;
        int boxH = textH + padding;


        int bx = x + (tileSize - boxW) / 2;
        int by = y - boxH - 10;


        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(bx, by, boxW, boxH);


        int textX = bx + padding;
        int textY = by + (boxH + fm.getAscent() - fm.getDescent()) / 2;
        g2d.setColor(new Color(0,0,0,180));
        g2d.drawString(name, textX + 1, textY + 1);


        g2d.setColor(Color.WHITE);
        g2d.drawString(name, textX, textY);


        g2d.setFont(original);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
    }

    public static void allowOnlyLetterOrDigit(JTextField textField, int maxLength) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            private final Pattern pattern = Pattern.compile("[\\p{javaLetter}|\\p{javaDigit}]*", Pattern.CASE_INSENSITIVE);

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength());
                newText = newText.substring(0, offset) + string + newText.substring(offset);

                if ((fb.getDocument().getLength() + string.length()) <= maxLength
                        && pattern.matcher(newText).matches()) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) return;

                String newText = fb.getDocument().getText(0, fb.getDocument().getLength());
                newText = newText.substring(0, offset) + text + newText.substring(offset + length);
                if (pattern.matcher(newText).matches() && (fb.getDocument().getLength() - length + text.length()) <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    public static JPanel drawClassPanel(Consumer<ClassType> onSelect) {
        ClassType[] classTypes = ClassType.values();

        List<SelectableClassPanel> panels = Arrays.stream(classTypes)
                .map(ct -> new SelectableClassPanel(ct, ct.getIcon()))
                .toList();

        Consumer<SelectableClassPanel> selectCharacter = panel -> {
            panels.forEach(SelectableClassPanel::deselect);
            panel.select();
            onSelect.accept(panel.getType());
        };

        for (SelectableClassPanel p : panels) {

            p.setSelectionAction(() -> selectCharacter.accept(p));


            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCharacter.accept(p);
                }
            });

            p.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER ||
                            e.getKeyCode() == KeyEvent.VK_SPACE) {
                        selectCharacter.accept(p);
                    }
                }
            });
        }

        if (!panels.isEmpty()) {
            selectCharacter.accept(panels.getFirst());
        }

        JPanel classPanel = new JPanel(new GridLayout(1, panels.size(), 5, 5));
        for (SelectableClassPanel p : panels) {
            classPanel.add(p);
        }

        return classPanel;
    }


     private static final class SelectableClassPanel extends JPanel {
        @Getter
        private final ClassType type;
        private final JLabel nameLabel;
        private final JLabel iconLabel;

        private final Border unselected = BorderFactory.createEmptyBorder(6, 6, 6, 6);
        private final Border selected = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLUE, 3),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)
        );
        @Setter
        private Runnable selectionAction = () -> {};

        public SelectableClassPanel(ClassType type, ImageIcon icon) {
            super(new BorderLayout());
            this.type = type;

            nameLabel = new JLabel(type.toString(),  SwingConstants.CENTER);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));

            iconLabel = new JLabel(icon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setBorder(unselected);
            iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            iconLabel.setToolTipText(type.toString());

            // Make iconLabel focusable so key events work
            iconLabel.setFocusable(true);
            iconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    requestFocusForIcon();
                    selectionAction.run();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    iconLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY, 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (iconLabel.getBorder() != selected) {
                        iconLabel.setBorder(unselected);
                    } else {
                        iconLabel.setBorder(selected);
                    }
                }
            });


            setFocusable(true);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            setPreferredSize(new Dimension(140, 150));

            add(nameLabel, BorderLayout.NORTH);
            add(iconLabel, BorderLayout.CENTER);
        }

        public void select() {
            iconLabel.setBorder(selected);
            nameLabel.setForeground(Color.BLUE.darker());
            repaint();
        }

        public void deselect() {
            iconLabel.setBorder(unselected);
            nameLabel.setForeground(Color.BLACK);
            repaint();
        }

        private void requestFocusForIcon() {
            iconLabel.requestFocusInWindow();
        }
    }
}
