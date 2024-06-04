// Плавный переход от одной игровой панели к другой
package client.ui;

import client.main.Main;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PaneMover {

    public static void moveTo(VBox newPane, String direction) {
        new Thread(() -> {
            if (direction.equals("right")) {
                Platform.runLater(() -> Main.getCt().getPane().getChildren().add(newPane));
                Main.getCt().getPane().setMouseTransparent(true);
                Label l = new Label("0");
                for (int i = -1; i >= -Game.WIDTH; i-=3) {
                    l.setText(Integer.toString(i));
                    Platform.runLater(() -> Main.getCt().getPane().setLayoutX(Integer.parseInt(l.getText())));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    Main.getCt().getPane().getChildren().removeFirst();
                    Main.getCt().getPane().setLayoutX(0);
                    Main.getCt().getPane().setMouseTransparent(false);
                });
            }
            else if (direction.equals("left")) {
                Platform.runLater(() -> {
                    Main.getCt().getPane().getChildren().add(0, newPane);
                    Main.getCt().getPane().setLayoutX(-Game.WIDTH);
                });
                //if (true) return;
                Main.getCt().getPane().setMouseTransparent(true);
                Label l = new Label("0");
                for (int i = -Game.WIDTH; i <= 0; i+=3) {
                    l.setText(Integer.toString(i));
                    Platform.runLater(() -> Main.getCt().getPane().setLayoutX(Integer.parseInt(l.getText())));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    Main.getCt().getPane().getChildren().removeLast();
                    Main.getCt().getPane().setLayoutX(0);
                    Main.getCt().getPane().setMouseTransparent(false);
                });
            }
        }).start();

    }
}
