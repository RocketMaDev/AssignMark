package cn.rocket.assaignmark.gui;

import com.jfoenix.controls.JFXProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * @author Rocket
 * @version 0.9.8
 */
public class Controller {
    public Label progressLabel;
    public Label statusLabel;
    public JFXProgressBar progressBar;
    public AnchorPane pane;

    public static void main(String[] args) {

    }

    public void initialize() {
        System.out.println(pane.getStyle());
    }
}
