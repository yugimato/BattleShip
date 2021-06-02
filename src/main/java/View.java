import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class View  extends JFrame {

    private final Model model;
    private final Gameboard gameboard;
    private final InfoBoard infoBoard;
    private final String filename = "scores.txt"; // Edetabeli fail

    public View(Model model) {
        super("Laevade pommitamine");
        this.model = model;

        this.gameboard = new Gameboard(model);
        this.infoBoard = new InfoBoard();

        JPanel container = new JPanel(new BorderLayout());

        container.add(gameboard, BorderLayout.CENTER); // Containeri peale tuleb gameboard vasakule
        container.add(infoBoard, BorderLayout.EAST); // Containeri peale tuleb infoboard vasakule

        add(container); // Lisa container Jframe peale
    }

    public void registerCombobox(ItemListener il) {
        infoBoard.getCmbSize().addItemListener(il);
    }

    public void registerGameboardMouse(Controller controller) {
        gameboard.addMouseListener(controller);
        gameboard.addMouseMotionListener(controller);
    }

    public void registerNewGame(ActionListener al) {
        infoBoard.getBtnNewGame().addActionListener(al);
    }

    public void registerScoresButton(ActionListener al) {
        infoBoard.getBtnScoreboard().addActionListener(al);

    }

    public JLabel getLblMouseXY() {
        return infoBoard.getLblMouseXY();
    }

    public JLabel getLblID() {
        return infoBoard.getLblID();
    }

    public JLabel getLblRowCol() {
        return infoBoard.getLblRowCol();
    }

    public JLabel getLblTime() {
        return infoBoard.getLblTime();
    }

    public JLabel getLblShip() {
        return infoBoard.getLblShip();
    }

    public JLabel getLblGameboard() {
        return infoBoard.getLblGameboard();
    }

    public JComboBox<String> getCmbSize() {
        return infoBoard.getCmbSize();
    }

    public JButton getBtnNewGame() {
        return infoBoard.getBtnNewGame();
    }

    public JButton getBtnScoreboard() {
        return infoBoard.getBtnScoreboard();
    }

    public JRadioButton getRdoTable() {
        return infoBoard.getRdoTable();
    }

    public JRadioButton getRdoTableModel() {
        return infoBoard.getRdoTableModel();
    }

    public JRadioButton getRdoDatabase() {
        return infoBoard.getRdoDatabase();
    }

    public void writeToFile(String name, int boardsize, int clicks, int gtime) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            String line = name + ";" + gtime + ";" + boardsize + ";" + clicks + ";" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write(line);
            bw.newLine(); // Reavahetus
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, "Edetabeli faili ei leitud" + filename);
        }
    }

    public ArrayList<Scoredata> readFromFile() throws IOException{ // Faili pole , teeb uue faili
        ArrayList<Scoredata> scoredatas = new ArrayList<>();
        File f = new File(filename);
        if(f.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
                for(String line; (line = br.readLine()) != null;) {
                    String[] parts = line.split(";");
                    if(Integer.parseInt(parts[2]) == model.getBoardsize()) {
                        String name = parts[0];
                        int gametime = Integer.parseInt(parts[1]); // Mängu aeg sek
                        int board = Integer.parseInt(parts[2]); // Laua suurus
                        int click = Integer.parseInt(parts[3]); // Klikke
                        LocalDateTime played = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        scoredatas.add(new Scoredata(name, gametime, board, click, played));

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(filename);
            file.createNewFile();
        }
        return scoredatas;
    }
}
