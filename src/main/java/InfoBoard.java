import javax.swing.*;
import java.awt.*;

public class InfoBoard extends JPanel {

    private final JPanel pnlComponents = new JPanel(new GridBagLayout()); // Siia labelid, combobox, radiobtn
    private final GridBagConstraints gbc = new GridBagConstraints();

    private final Font fontBold = new Font("Verdana", Font.BOLD, 14); //Rasvane 14 verdana
    private final Font fontNormal = new Font("Verdana", Font.PLAIN, 14); //Tavaline 14 verdana

    private final String[] boardsizez = {"10", "11", "12", "13", "14", "15"};

    private JLabel lblMouseXY;
    private JLabel lblID;
    private JLabel lblRowCol;
    private JLabel lblTime;
    private JLabel lblShip;
    private JLabel lblGameboard;

    private JComboBox<String> cmbSize;

    private JButton btnNewGame;
    private JButton btnScoreboard;

    private final JLabel lblScoreboard = new JLabel("Edetabeli variant");
    private final JRadioButton rdoTable = new JRadioButton("Lihtne tabel");
    private final JRadioButton rdoTableModel = new JRadioButton("Model tabel");
    private final JRadioButton rdoDatabase = new JRadioButton("Andmebaas");

    private final ButtonGroup btnGrp = new ButtonGroup();

    public InfoBoard() {
        setBackground(new Color(229, 234, 231, 206));
        setPreferredSize(new Dimension(380, 188)); // Paneeli suurus

        pnlComponents.setBackground(new Color(37, 125, 231));

        gbc.anchor = GridBagConstraints.WEST; // Lahtris tekstid vasakult
        gbc.insets = new Insets(2, 2, 2, 2);

        setupLabels();
        setupCombobox();
        setupButtons();
        setupRadioButtons();



        add(pnlComponents);

    }

    private void setupLabels() {
        //Esimene rida
        JLabel lblMouseText = new JLabel("Hiir");
        lblMouseText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 0; // Rida
        pnlComponents.add(lblMouseText, gbc);

        lblMouseXY = new JLabel("X ; 0 & Y: 0");
        lblMouseXY.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 0; // Rida
        pnlComponents.add(lblMouseXY, gbc);

        //Teine rida
        JLabel lblIdText = new JLabel("ID");
        lblIdText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 1; // Rida
        pnlComponents.add(lblIdText, gbc);

        lblID = new JLabel("Teadmata");
        lblID.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 1; // Rida
        pnlComponents.add(lblID, gbc);

        //Kolmas rida
        JLabel lblRowColText = new JLabel("Rida : Veerg");
        lblRowColText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 2; // Rida
        pnlComponents.add(lblRowColText, gbc);

        lblRowCol = new JLabel("1 : 1");
        lblRowCol.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 2; // Rida
        pnlComponents.add(lblRowCol, gbc);

        //Neljas rida
        JLabel lblTimeText= new JLabel("M\u00E4ngu aeg");
        lblTimeText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 3; // Rida
        pnlComponents.add(lblTimeText, gbc);

        lblTime = new JLabel("00:00");
        lblTime.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 3; // Rida
        pnlComponents.add(lblTime, gbc);

        //Viies rida
        JLabel lblShipText= new JLabel("Laevad");
        lblShipText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 4; // Rida
        pnlComponents.add(lblShipText, gbc);

        lblShip = new JLabel("0 / 0");
        lblShip.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 4; // Rida
        pnlComponents.add(lblShip, gbc);

        //Kuues rida
        JLabel lblGameboardText = new JLabel("Valitud laua suurus");
        lblGameboardText.setFont(fontBold);
        gbc.gridx = 0; // Veerg
        gbc.gridy = 5; // Rida
        pnlComponents.add(lblGameboardText, gbc);

        lblGameboard = new JLabel("15 x 15");
        lblGameboard.setFont(fontNormal);
        gbc.gridx = 1; // Veerg
        gbc.gridy = 5; // Rida
        pnlComponents.add(lblGameboard, gbc);
    }

    private void setupCombobox() {
        JLabel lblCmbSizeText = new JLabel("Vali laua suurus");
        lblCmbSizeText.setFont(fontBold);
        gbc.gridx = 0;
        gbc.gridy = 6;
        pnlComponents.add(lblCmbSizeText, gbc);

        cmbSize = new JComboBox<>(boardsizez);
        cmbSize.setFont(fontNormal);
        cmbSize.setPreferredSize(new Dimension(106, 28));
        gbc.gridx = 1;
        gbc.gridy = 6;
        pnlComponents.add(cmbSize, gbc);

    }

    private void setupButtons() {
        JLabel lblNewGameText = new JLabel("Uus m\u00E4ng?");
        lblNewGameText.setFont(fontBold);
        gbc.gridx = 0;
        gbc.gridy = 7;
        pnlComponents.add(lblNewGameText, gbc);

        btnNewGame = new JButton("Uus m\u00E4ng");
        btnNewGame.setFont(fontNormal);
        btnNewGame.setPreferredSize(new Dimension(106, 28));
        gbc.gridx = 1;
        gbc.gridy = 7;
        pnlComponents.add(btnNewGame, gbc);

        btnScoreboard = new JButton("Edetabel");
        btnScoreboard.setFont(fontNormal);
        btnScoreboard.setPreferredSize(new Dimension(106, 28));
        gbc.gridx = 1;
        gbc.gridy = 8;
        pnlComponents.add(btnScoreboard, gbc);



    }

    private void setupRadioButtons() {
        lblScoreboard.setFont(fontBold);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 9;
        pnlComponents.add(lblScoreboard,gbc);

        rdoTable.setFont(fontNormal);
        rdoTable.setSelected(true);
        gbc.gridx = 1;
        gbc.gridy = 9;
        pnlComponents.add(rdoTable, gbc);

        rdoTableModel.setFont(fontNormal);
        gbc.gridx = 1;
        gbc.gridy = 10;
        pnlComponents.add(rdoTableModel, gbc);

        rdoDatabase.setFont(fontNormal);
        gbc.gridx = 1;
        gbc.gridy = 11;
        pnlComponents.add(rdoDatabase, gbc);

        //lisame raadionupud gruppi
        btnGrp.add(rdoTable);
        btnGrp.add(rdoTableModel);
        btnGrp.add(rdoDatabase);




    }

    public JLabel getLblMouseXY() {
        return lblMouseXY;
    }

    public JLabel getLblID() {
        return lblID;
    }

    public JLabel getLblRowCol() {
        return lblRowCol;
    }

    public JLabel getLblTime() {
        return lblTime;
    }

    public JLabel getLblShip() {
        return lblShip;
    }

    public JLabel getLblGameboard() {
        return lblGameboard;
    }

    public JComboBox<String> getCmbSize() {
        return cmbSize;
    }

    public JButton getBtnNewGame() {
        return btnNewGame;
    }

    public JButton getBtnScoreboard() {
        return btnScoreboard;
    }

    public JRadioButton getRdoTable() {
        return rdoTable;
    }

    public JRadioButton getRdoTableModel() {
        return rdoTableModel;
    }

    public JRadioButton getRdoDatabase() {
        return rdoDatabase;
    }
}

