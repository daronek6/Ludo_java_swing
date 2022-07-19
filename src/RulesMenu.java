import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa okna wyświetlającego zasady gry
 */
public class RulesMenu extends JPanel {

    private final String rulesString = "Zasady gry w chińczyka: \n" +
            "W gre można grać od 2 do 4 osób. \n" +
            "Losowo wybrany gracz zaczyna grę. \n" +
            "Gra odbywa się turowo. Tura polega na rzucie kostką oraz decyzją ruchu pionkiem. \n" +
            "Rzucana kostka jest standardową sześciościenną kością do gry. Ilość wylosowanych oczek odpowiada następującym możliwym ruchom: \n" +
            "\t1 - Gracz może wyjść z bazy na planszę lub ruszyć się pionkiem, który już jest na planszy o jedno pole (ruch obejmują te same zasady jak dla rzutu od 2 do 5 oczek); \n" +
            "\t2 do 5 - Gracz musi ruszyć się pionkiem, który jest na planszy o tyle pól ile wylosował oczek, chyba, że nie ma pionków na planszy lub inny jego pionek zajmuje już to pole; \n" +
            "\t6 - Gracz może wyjść z bazy na planszę lub ruszyć się pionkiem, który już jest na planszy o sześć pól (ruch obejmują te same zasady jak dla rzutu od 2 do 5 oczek); \n" +
            "\tJeżeli żaden z ruchów nie jest możliwy tura gracza przepada. \n" +
            "Jeżeli gracz ruszy swoim pionekm na pole na którym stoi pionek innego gracza, pionek innego gracza zostaje zbity i wraca do bazy. \n" +
            "Gra polega na przejściu wszystkimi swoimi pionkami z bazy do domku. Wygrywa ten gracz, który zrobi to pierwszy.";

    private Window windowRef;
    private JButton returnButton;
    private JTextArea rulesInfoTA;

    public RulesMenu(Window ref) {
        windowRef = ref;

        returnButton = new JButton("Powrót");
        rulesInfoTA = new JTextArea(rulesString);
        rulesInfoTA.setFocusable(false);
        rulesInfoTA.setLineWrap(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                windowRef.showMainMenu();
            }
        });

        add(rulesInfoTA);
        add(returnButton);

        setVisible(false);
    }
}
