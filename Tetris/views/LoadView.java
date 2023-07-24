package views;

import model.TetrisModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Load File View
 *
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class LoadView {

    private TetrisView tetrisView;
    private Label selectBoardLabel;
    private Button selectBoardButton;
    private ListView<String> boardsList;


    /**
     * Constructor
     *
     * @param tetrisView master view
     */
    public LoadView (TetrisView tetrisView) {
        tetrisView.paused = true;
        this.tetrisView = tetrisView;
        selectBoardLabel = new Label(String.format("Currently playing: Default Board"));
        boardsList = new ListView<>(); //list of tetris.boards

        final Stage dialog = new Stage(); //dialogue box
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tetrisView.stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #121212;");

        selectBoardLabel.setId("CurrentBoard"); // DO NOT MODIFY ID

        boardsList.setId("BoardsList");  // DO NOT MODIFY ID
        boardsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        getFiles(boardsList); //get files for file selector

        selectBoardButton = new Button("Change board");
        selectBoardButton.setId("ChangeBoard"); // DO NOT MODIFY ID

        //on selection, do somethine
        selectBoardButton.setOnAction(e -> {
            try {
                selectBoard(selectBoardLabel, boardsList);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox selectBoardBox = new VBox(10, selectBoardLabel, boardsList, selectBoardButton);

        // Default styles which can be modified
        boardsList.setPrefHeight(100);

        selectBoardLabel.setStyle("-fx-text-fill: #e8e6e3");
        selectBoardLabel.setFont(new Font(16));

        selectBoardButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        selectBoardButton.setPrefSize(200, 50);
        selectBoardButton.setFont(new Font(16));

        selectBoardBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().add(selectBoardBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
        dialog.setOnCloseRequest(event -> {
            tetrisView.paused = false;
        });
    }

    /**
     * Populate the listView with all the .SER files in the boards directory
     *
     * @param listView ListView to update
     * @return the index in the listView of Stater.ser
     */
    private void getFiles(ListView<String> listView) {
        File[] files = new File("boards/").listFiles();
        for (File file : files) {
            String ext = file.toString().substring(file.toString().lastIndexOf("."));
            if (ext.equalsIgnoreCase(".ser")) {
                listView.getItems().add(file.toString());
            }
        }
    }

    /**
     * Select and load the board file selected in the boardsList and update selectBoardLabel with the name of the new Board file
     *
     * @param selectBoardLabel a message Label to update which board is currently selected
     * @param boardsList a ListView populated with tetris.boards to load
     */
    private void selectBoard(Label selectBoardLabel, ListView<String> boardsList) throws IOException {
        this.tetrisView.model = this.loadBoard(boardsList.getSelectionModel().getSelectedItem());
        selectBoardLabel.setText("Currently Playing: " + boardsList.getSelectionModel().getSelectedItem());
    }

    /**
     * Load the board from a file
     *
     * @param boardFile file to load
     * @return loaded Tetris Model
     */
    public TetrisModel loadBoard(String boardFile) throws IOException {
        System.out.println("boardFile: " + boardFile);

        // Reading the object from a file
        FileInputStream file = null;
        ObjectInputStream in = null;
        try {
            file = new FileInputStream(boardFile);
            in = new ObjectInputStream(file);
            return (TetrisModel) in.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            in.close();
            file.close();
        }
    }
}
