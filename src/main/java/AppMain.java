import javax.swing.*;

public class AppMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Model model = new Model(); // Teeb mudeli
                View view = new View(model); // Teeb vaata JFrame
                Controller controller = new Controller(model,view);

                view.registerGameboardMouse(controller);


                view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                view.pack(); // Et objektid JFrameil oma koha leiaks
                view.setLocationRelativeTo(null); //Jframe keset ekraani
                view.setResizable(false); // Akna suurust ei saa muuta false puhul, truega saab
                view.setVisible(true); //
            }
        });
    }
}
