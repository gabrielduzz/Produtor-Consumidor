/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 16/10/2023
* Ultima alteracao: 29/10/2023
* Nome: Principal.java
* Funcao: Carregar a cena inicial
********************************************************************/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.lang.System;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import control.PrincipalController;

public class Principal extends Application {

  /********************************************************************
  * Metodo: createContent
  * Funcao: carregar a tela principal da simulacao
  * Parametros: nenhum
  * Retorno: root(tipo Parent) = os elementos da cena para ser carregada no stage principal, apos
             ser liberado o inicio da simulacao
  ********************************************************************/
  private Parent createContent() throws Exception{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/principal_view.fxml"));
    Pane root2 = loader.load();
    return root2;
  }

  /********************************************************************
  * Metodo: start
  * Funcao: metodo padrao que tem a funcao de definir o container da 
            aplicacao
  * Parametros: primaryStage = janela principal
  * Retorno: void
  ********************************************************************/
  @Override
  public void start(Stage primaryStage) throws Exception {

    Pane root1 = new Pane();
    root1.setPrefSize(1023, 650);

    Image backgroundImage = new Image("/resources/tela de inicio bar.png");
    ImageView background = new ImageView(backgroundImage);
    background.setImage(backgroundImage);
    background.setFitWidth(1023);
    background.setFitHeight(650);

    Image startButtonImage = new Image("/resources/start button.png");
    ImageView startButton = new ImageView(startButtonImage);
    startButton.setFitHeight(108);
    startButton.setFitWidth(216);
    startButton.setLayoutX(408);
    startButton.setLayoutY(356);


    Image closeButtonImage = new Image("/resources/close button.png");
    ImageView closeButton = new ImageView(closeButtonImage);
    closeButton.setFitHeight(58);
    closeButton.setFitWidth(58);
    closeButton.setLayoutX(948);
    closeButton.setLayoutY(31);

    root1.getChildren().addAll(background, startButton, closeButton);

    Scene scene1 = new Scene(root1);

    startButton.setOnMouseClicked(event -> {
      try{
        Scene scene2 = new Scene(createContent());
        primaryStage.setScene(scene2); //quando esse evento ocorrer, sera trocada a tela
      } catch (Exception e){
        e.printStackTrace();
      }  
    });

    closeButton.setOnMouseClicked(event -> {
      System.exit(0);
    });

    primaryStage.setScene(scene1);
    primaryStage.setResizable(false);
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
