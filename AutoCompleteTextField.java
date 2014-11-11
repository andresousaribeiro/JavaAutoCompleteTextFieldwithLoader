/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author AndreRibeiro
 */
public class AutoCompleteTextField extends JPanel {

  
    private static JLabel iconLabel;
    private static int minCharToSearch;

    public AutoCompleteTextField(String pathToIcon, int minCharToSearch) {

       
        this.minCharToSearch = minCharToSearch;
        setUPLayout(pathToIcon);

    }

    private void setUPLayout(String pathToIcon) {
        this.setBackground(new Color(255, 255, 255));
        setLayout(new MigLayout("insets 0 0 0 0 "));
        ArrayList<String> items = new ArrayList<String>();
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            String item = locales[i].getDisplayName();
            items.add(item);
        }
        this.setBorder(new JTextField().getBorder());

        setPreferredSize(new Dimension(200, 30));
        JTextField txtInput = new JTextField();
        txtInput.setPreferredSize(new Dimension(200, 30));
        txtInput.setBorder(null);
        setupAutoComplete(txtInput, items);

        this.add(txtInput);

        if (pathToIcon != null) {
            iconLabel = new JLabel(new ImageIcon(pathToIcon));
            this.add(iconLabel, "gapright 5");
            showIcon(false);
        }

    }

    public static void setupAutoComplete(final JTextField txtInput, final ArrayList<String> items) {
        final DefaultComboBoxModel model = new DefaultComboBoxModel();
        final JComboBox cbInput = new JComboBox(model) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
        setAdjusting(cbInput, false);
        for (String item : items) {
            model.addElement(item);
        }
        cbInput.setSelectedItem(null);
        cbInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdjusting(cbInput)) {
                    if (cbInput.getSelectedItem() != null) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                    }
                }
            }
        });

        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setAdjusting(cbInput, true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (cbInput.isPopupVisible()) {
                        e.setKeyCode(KeyEvent.VK_ENTER);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.setSource(cbInput);
                    cbInput.dispatchEvent(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtInput.setText(cbInput.getSelectedItem().toString());
                        cbInput.setPopupVisible(false);
                        showIcon(false);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cbInput.setPopupVisible(false);
                    showIcon(false);
                }
                setAdjusting(cbInput, false);
            }
        });
        txtInput.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() >= minCharToSearch) {
                    updateList();
                }

            }

            public void removeUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() < minCharToSearch) {

                    cbInput.hidePopup();
                    showIcon(false);
                }else{
                 updateList();
                }
            }

            public void changedUpdate(DocumentEvent e) {
            }

            private void updateList() {
                //here we update the combo list to show, and enable or disable image loader in function off the request.
                //the request can be, a database request, http request, ...
                showIcon(true);
                setAdjusting(cbInput, true);
                model.removeAllElements();
                String input = txtInput.getText();
                if (!input.isEmpty()) {
                    for (String item : items) {
                        if (item.toLowerCase().startsWith(input.toLowerCase())) {
                            model.addElement(item);
                        }
                    }
                }
                cbInput.hidePopup();
                cbInput.setPopupVisible(model.getSize() > 0);
                setAdjusting(cbInput, false);

            }
        });
        txtInput.setLayout(new BorderLayout());
        txtInput.add(cbInput, BorderLayout.SOUTH);
        showIcon(false);
    }

    private static boolean isAdjusting(JComboBox cbInput) {
        if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) cbInput.getClientProperty("is_adjusting");
        }
        return false;
    }

    private static void setAdjusting(JComboBox cbInput, boolean adjusting) {
        cbInput.putClientProperty("is_adjusting", adjusting);
    }

    private static void showIcon(boolean isToShow) {

        if (iconLabel != null) {
            iconLabel.setVisible(isToShow);
        }
    }
}
