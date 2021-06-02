import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Controller implements MouseListener, MouseMotionListener {

    private final Model model;
    private final View view;
    private final GameTimer gametimer;
    private final String dbname = "scores.db";
    String[] columnNames = new String[]{"Nimi", "Aeg", "Klikke", "Laua suurus", "M\u00E4ngitud"};
    private JDialog scoreboarddialog;
    private boolean doRdoTbl = true;
    private boolean doRdoTblMdl = false;
    private boolean doRdoDb = false;
    private JTable table;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.gametimer = new GameTimer(view);

        view.registerCombobox(new MyComboboxListener());
        view.registerNewGame(new MyNewGameListener());
        view.registerScoresButton(new MyScoreButton());
    }

    private class MyNewGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gametimer.isRunning()) { //aeg ei käi
                model.setupNewgame();
                gametimer.startTimer();
                gametimer.setRunning(true);
                view.getBtnNewGame().setText("L\u00F5peta");
                view.getCmbSize().setEnabled(false); //Comboboxist ei saa valida
                model.getGame().setupNewGame(); //See paneb laevad reaalselt matriksisse
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts());
                model.getGame().showGameboardmatrix(); // Test näita konsoolis laevade asukohta
            } else { // Mäng käib
                gametimer.stopTimer();
                gametimer.setRunning(false);
                view.getBtnNewGame().setText("Uus m\u00E4ng");
                model.getGame().setGameOver();
                view.getCmbSize().setEnabled(true);
                resetPlayerData();
            }
        }
    }

    private void resetPlayerData() {
        gametimer.setMinutes(0);
        gametimer.setSeconds(0);
        model.getGame().resetClickCounter();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gametimer.isRunning()) { // Mäng käib, seega võib klikkida
            int id = model.checkGridIndex(e.getX(), e.getY());
            int row = model.getRowById(id);
            int col = model.getColById(id);
            int[][] matrix = model.getGame().getBoardmatrix(); // Hetke laud
            if (matrix[row][col] == 0) { // vesi ehk mööda
                model.getGame().setUserClick(row, col, 8);
                model.getGame().setClickCounter(1);
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts() + " m\u00F6\u00F6da ");
            } else if (matrix[row][col] > 0 && matrix[row][col] < 5) { // Pihtas
                model.getGame().setUserClick(row, col, 7);
                model.getGame().setClickCounter(1);
                model.getGame().setShipsCounter(1);
                view.getLblShip().setText(model.getGame().getShipsCounter() + " / " + model.getGame().getShipsParts() + " pihtas ");
            }
            model.getGame().showGameboardmatrix(); //TEstimiseks
            view.repaint();
            //Kontrollime ega mäng pole läbi saanud
            if (model.getGame().isGameOver()) {
                gametimer.stopTimer();
                gametimer.setRunning(false);
                String name = JOptionPane.showInputDialog(view, "Kuidas on sinu nimi?");
                if (name.trim().isEmpty()) {
                    name = "Teadmata";
                }
                JOptionPane.showMessageDialog(view, "M2ng on l2bi inimene nimega " + name);
                view.getBtnNewGame().setText("Uus m2ng");
                view.getCmbSize().setEnabled(true);
                // TODO Lisa andmebaasi ja teksti faili võitja nimi
                //Kirjuta faili
                view.writeToFile(name, model.getBoardsize(), model.getGame().getClickCounter(), gametimer.getPlayedTimeInSeconds());
                //Kirjuta andmebaasi
                addIntoDatabase(name, model.getBoardsize(), model.getGame().getClickCounter(), gametimer.formatGameTime());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        String mouse = String.format("x: %03d & Y: %03d", e.getX(), e.getY());
        view.getLblMouseXY().setText(mouse);
        int id = model.checkGridIndex(e.getX(), e.getY());
        int row = model.getRowById(id);
        int col = model.getColById(id);

        if (id != -1) {
            view.getLblID().setText(String.valueOf(id + 1));
        }
        //row ja col näitamine
        String rowcol = String.format("%d : %d", row + 1, col + 1); //Inimlikud numbrid
        if (row == -1 || col == -1) {
            rowcol = "Pole m\u00E4ngulaual";
        }
        view.getLblRowCol().setText(rowcol); // Näitab reaalselt
    }

    private class MyComboboxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            //System.out.println(e.getItem()); // Eelmine ja uus valik, pole hea
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println(e.getItem()); //Testiks
                String number = e.getItem().toString(); // Näiteks suurus 15
                int size = Integer.parseInt(number);
                view.getLblGameboard().setText(number + " x " + number);
                model.setBoardsize(size);
                view.pack(); //Et laua suurus muutuks
                view.repaint();
            }
        }
    }

    /**
     * Lisab mängija info andmebaasi
     *
     * @param name      nimi
     * @param boardsize laua suurus
     * @param clicks    tehtud klikkide arv
     * @param gtime     mänguaeg sekundites
     */
    private void addIntoDatabase(String name, int boardsize, int clicks, String gtime) {
        File f = new File(dbname); // Vaatame kas db on olema ja kui pole siis teeme.
        if (!f.exists()) {
            String url = "jdbc:sqlite:" + dbname;
            String sql = "CREATE TABLE \"scores\" (\n" +
                    "\t\"id\"\tINTEGER,\n" +
                    "\t\"name\"\tTEXT,\n" +
                    "\t\"board\"\tINTEGER,\n" +
                    "\t\"clicks\"\tINTEGER,\n" +
                    "\t\"gametime\"\tINTEGER,\n" +
                    "\t\"playedtime\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                    ");";
            try (Connection con = DriverManager.getConnection(url);
                 Statement stat = con.createStatement()) {
                stat.execute(sql);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                //System.out.println(e.getMessage());
            }
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            String[] parts = gtime.split(":");
            int mtime = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            String sql = "INSERT INTO scores (name, board, clicks, gametime, playedtime) VALUES (?, ?, ?, ?, datetime('now', 'localtime'))";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, boardsize);
            statement.setInt(3, clicks);
            statement.setInt(4, mtime);
            statement.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.println("Andmebaasiga miski kamm");
            e.printStackTrace();
        }
    }

    private boolean createTable(ArrayList<Scoredata> scoredatas) {
        if (scoredatas.size() > 0) {
            orderArrayList(scoredatas);
            String[][] data = new String[scoredatas.size()][5];
            for (int i = 0; i < scoredatas.size(); i++) {
                data[i][0] = scoredatas.get(i).getName();
                data[i][1] = String.valueOf(scoredatas.get(i).convertSecToMMSS(scoredatas.get(i).getTime()));
                data[i][2] = String.valueOf(scoredatas.get(i).getClickcount());
                data[i][3] = String.valueOf(scoredatas.get(i).getBoardsize());
                data[i][4] = scoredatas.get(i).getPlayedtime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            table = new JTable(data, columnNames); // Teeb tabeli andmetega
            scoreboarddialog = new ScoreboardDialog(view);
            scoreboarddialog.add(new JScrollPane(table));
            scoreboarddialog.setTitle("Edetabel JTable");
            return true;
        }
        return false;
    }

    private boolean createTableDb(ArrayList<Scoredata> scoredatas) {
        if(scoredatas.size() > 0) {
            DefaultTableModel tablemodel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Lahtri sisu ei saa muuta
                }
            };

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer(); // Joondamine paremale 1
            rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT); // Joondamine paremale 2

            table = new JTable(tablemodel);

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    JOptionPane.showMessageDialog(null, table.getValueAt(row,col));
                }
            });

            //Tabeli päis
            for(String columnName : columnNames) {
                tablemodel.addColumn(columnNames);
            }

            // Tabeli sisu / andmed
            for(Scoredata scoredata : scoredatas) {
                String name = scoredata.getName();
                int mytime = scoredata.getTime();
                int click = scoredata.getClickcount();
                int board = scoredata.getBoardsize();
                String played = scoredata.getPlayedtime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                tablemodel.addRow(new Object[] {name, scoredata.convertSecToMMSS(mytime), click, board, played});
            }
            table.getColumn("Aeg").setCellRenderer(rightRenderer);
            table.getColumn("Klikke").setCellRenderer(rightRenderer);
            table.getColumn(columnNames[2]).setCellRenderer(rightRenderer);
            table.getColumn(columnNames[3]).setCellRenderer(rightRenderer);
            //Veeru laiuse määramine
            table.getColumnModel().getColumn(1).setPreferredWidth(5);
            table.getColumnModel().getColumn(2).setPreferredWidth(10);
            table.getColumnModel().getColumn(3).setPreferredWidth(20);

            //Teeme dialoogi akna kus näidata edetabelit
            scoreboarddialog = new ScoreboardDialog(view);


        } else {
            return false;

    }

    private class MyScoreButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Scoredata> result;
            if(doRdoTbl) {
                try {
                    result = view.readFromFile();
                    if(createTable(result)) {
                        scoreboarddialog.setModal(true);
                        scoreboarddialog.pack();
                        scoreboarddialog.setLocationRelativeTo(null);
                        scoreboarddialog.setVisible(true);
                    }
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            } else if(doRdoTblMdl) {
                //TODO rdoTblMdl
            }else if(doRdoDb) {
                result = readFromDatabase(model.getBoardsize());
                if(result != null && createTableDb(result)) {
                    scoreboarddialog.setModal(true);
                    scoreboarddialog.pack();
                    scoreboarddialog.setLocationRelativeTo(null);
                    scoreboarddialog.setVisible(true);
                }
            }
        }
    }

    /**
     * Sorteerib massiivi
     * @param scoredatas massiiv mis loetud failist
     */
    private void orderArrayList(ArrayList<Scoredata> scoredatas) {
        scoredatas.sort((o1, o2) -> {
            // Aeg
            Integer x1 = o1.getTime();
            Integer x2 = o2.getTime();
            int sComp = x2.compareTo(x1);
            if (sComp != 0) {
                return sComp;
            }
            // Klikkide arv
            Integer y1 = o1.getClickcount();
            Integer y2 = o2.getClickcount();
            return y2.compareTo(y1);
        });
    }

    private ArrayList<Scoredata> readFromDatabase(int boardsize) {
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            Statement stat = con.createStatement();
            String sql = "SELECT * FROM scores WHERE board = " + boardsize + " ORDER BY gametime, clicks";
            ResultSet rs = stat.executeQuery(sql);
            ArrayList<Scoredata> sd = new ArrayList<>();
            if(rs.isBeforeFirst()) {
                while(rs.next()) {
                    String name = rs.getString("name");
                    int board = rs.getInt("board");
                    int click = rs.getInt("click");
                    int seconds = rs.getInt("gametime");
                    String ptime = rs.getString("playedtime");
                    LocalDateTime mytime = LocalDateTime.parse(ptime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                    sd.add(new Scoredata(name, seconds, board, click, mytime));
                }
                con.close();
                return sd;
            } else {
                return null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

}
