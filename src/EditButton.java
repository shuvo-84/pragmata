import javafx.scene.control.Button;

public class EditButton extends Button{
    String style = "-fx-background-color: #30AABA; " +
        "-fx-text-fill: #FFFFFF; " +
        "-fx-font-size: 14px; " +
        "-fx-padding: 10px 20px";
    public EditButton(String text){
        setText(text);
        setStyle(style);
    }
}
