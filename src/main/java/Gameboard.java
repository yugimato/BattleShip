import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Gameboard extends JPanel {

    private final Model model;

    private final int startX = 5; //Vasakult paremale 5 pixlit
    private final int startY = 5; //Paremalt vasakule 5 pixlit
    private final int width = 30;
    private final int height = 30;
    private final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};


    public Gameboard(Model model) {
        this.model = model;
        setBackground(new Color(186, 113, 231));
    }

    @Override
    public Dimension getPreferredSize() {
        int w = (width * model.getBoardsize()) + width + (2* startX);
        int h = (height * model.getBoardsize()) + height + (2* startY);
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);

        //Kirjastiil mängualal
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        //Joonista tähestiku ruudustik
        drawColAlphabet(g);
        //Joonistab reanumbrid
        drawRowNumbers(g);
        //Joonistab ülejäänud laua osa
        drawGameGrid(g);
        // Kui on mäng siis näita laevu ka
        if(!model.getGame().isGameOver()) {
            model.drawUserBoard(g);
        }
        drawGameGrid(g);
    }

    private void drawColAlphabet(Graphics g) {
        int i = 0; // Abimuutuja tähestiku massiivist tähe saamiseks
        g.setColor(Color.BLUE);
        for(int x = startX; x <= (width * model.getBoardsize()) + width; x+=width) {
            g.drawRect(x, startY, width, height);
            if(x > startX) { // Esimene lahted jääb tühjaks
                g.drawString(alphabet[i], x + (width / 2) - 5, 2 * (startY + startY) + 5);
                i++;
            }

        }
    }
    private void drawRowNumbers(Graphics g) {
        int i = 1;
        g.setColor(Color.RED);
        for(int y = startY+height; y < (height * model.getBoardsize()) + height; y+=height) {
            g.drawRect(startX, y, width, height);
            if(i < 10) {
                g.drawString(String.valueOf(i), startX + (width / 2) -5, y+2 * (startY + startY));
            } else {
                g.drawString(String.valueOf(i), startX + (width / 2) -10, y+2 * (startY + startY));

            }
            i++;
        }
    }

    private void drawGameGrid(Graphics g) {
        ArrayList<GridData> matrix = new ArrayList<>();
        g.setColor(Color.BLACK);
        int x = startX + width;
        int y = startY + height;
        int i = 1;
        for(int r = 0; r < model.getBoardsize(); r++) {
            for(int c = 0; c < model.getBoardsize(); c++) {
                g.drawRect(x, y, width, height); // Joonistab jooned lauale
                matrix.add(new GridData(r, c, x, y, width, height));
                x += width; // Laiuse võrra x kasvab vasakult paremale
            }
            y = (startY + height) + (height * i);
            i += 1;
            x = startX + width;
        }
        model.setGriddata(matrix);
    }
}
