import javafx.scene.control.Button;

public class AddButton extends Button{
    String style = "-fx-background-color: #306F37; " +
        "-fx-text-fill: #FFFFFF; " +
        "-fx-font-size: 14px; " +
        "-fx-padding: 10px 20px";
    public AddButton(String text){
        setText(text);
        setStyle(style);
    }
}
