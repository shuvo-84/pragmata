import javafx.scene.control.Button;

public class markAsComplete extends Button{
    String style = "-fx-background-color: #000000; " +
        "-fx-text-fill: #FFFFFF; " +
        "-fx-font-size: 14px; " +
        "-fx-padding: 10px 20px";
    public markAsComplete(String text){
        setText(text);
        setStyle(style);
    }
}
