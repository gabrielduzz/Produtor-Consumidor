/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 16/10/2023
* Ultima alteracao: 29/10/2023
* Nome: PrincipalController.java
* Funcao: Gerenciar os objetos da cena e inicializar as threads
********************************************************************/

package control;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.lang.System;
import java.lang.InterruptedException;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import model.Producer;
import model.Consumer;

public class PrincipalController implements Initializable{

  @FXML private ImageView bar;
  @FXML private AnchorPane primaryAnchorPane;
  @FXML private ImageView beer0;
  @FXML private ImageView beer1;
  @FXML private ImageView beer2;
  @FXML private ImageView beer3;
  @FXML private ImageView beer4;
  @FXML private ImageView manDrinking;
  @FXML private ImageView manSitting;
  @FXML private ImageView pauseButton1;
  @FXML private ImageView pauseButton2; 
  @FXML private Slider sliderConsumer;
  @FXML private Slider sliderProducer;
  @FXML private ImageView resetButton;
  @FXML private ImageView barmanProducing;
  @FXML private ImageView barmanWaiting;
  @FXML private ImageView closeButton;
  @FXML private ImageView consumerText;
  @FXML private ImageView minus1;
  @FXML private ImageView minus2;
  @FXML private ImageView plus1;
  @FXML private ImageView plus2;
  @FXML private ImageView producerText;
  @FXML private ProgressBar progressBarProducer;
  @FXML private ProgressBar progressBarConsumer;
  
  Producer producer;
  Consumer consumer;
  ArrayList<ImageView> beers = new ArrayList<>();

  final static int BUFFER_LENGTH = 5;

  public static Semaphore mutex = new Semaphore(1); //semaforo que controla o acesso a regiao critica
  public static Semaphore items = new Semaphore(0); //semaforo que controla a quantidade de itens no buffer
  public static Semaphore spaces = new Semaphore(BUFFER_LENGTH); //semaforo que controla a quantidade de espacos 
                                                                 //vazios no buffer

  public static boolean[] buffer = new boolean[BUFFER_LENGTH];
  public int[] barmanPositions = {620, 488, 320, 148, - 12};

  /********************************************************************
  * Metodo: produceBeer
  * Funcao: colocar a cerveja no balcao e trocar a imagem do barman
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  public void produceBeer(){
    barmanProducing.setVisible(false);
    barmanWaiting.setVisible(true);
    beers.get(4 - spaces.availablePermits()).setVisible(true);
    buffer[4 - spaces.availablePermits()] = true;
  }

  /********************************************************************
  * Metodo: startProducing
  * Funcao: trocar a imagem do barman para que ele inicie a producao
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  public void startProducing(){
    barmanProducing.setVisible(true);
    barmanWaiting.setVisible(false);
    barmanProducing.setLayoutX(barmanPositions[4 - spaces.availablePermits()]);
    barmanWaiting.setLayoutX(barmanPositions[4 - spaces.availablePermits()]);
    progressBarProducer.setLayoutX(barmanPositions[4 - spaces.availablePermits()] + 30);
  }

  /********************************************************************
  * Metodo: consumeBeer
  * Funcao: troca a imagem do consumidor para que ele beba a primeira cerveja
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  public void consumeBeer(){
    beer0.setVisible(false);
    buffer[items.availablePermits()] = false;
    manDrinking.setVisible(true);
    manSitting.setVisible(false);
  }
  
  /********************************************************************
  * Metodo: stopDrinking
  * Funcao: troca a imagem do consumidor e realiza um efeito de esteira
            para indicar que uma cerveja foi consumida
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  public void stopDrinking(){
    manDrinking.setVisible(false);
    manSitting.setVisible(true);

    int i = 0;

    while(i < items.availablePermits()){
      beers.get(i).setVisible(true);
      i++;
    }

    while(i < BUFFER_LENGTH){
      beers.get(i).setVisible(false);
      i++;
    }
    
  }

  /********************************************************************
  * Metodo: pauseConsumer
  * Funcao: pausa ou retoma a execucao do consumidor
  * Parametros: event = MouseEvent, click do mouse sobre o botao de pause
  * Retorno: void
  ********************************************************************/
  @FXML
  void pauseConsumer(MouseEvent event) {
    if(!consumer.getIsPaused()){
      pauseButton2.setImage(new Image("/resources/resume button.png"));
      consumer.setIsPaused(true);
    }
    else{
      pauseButton2.setImage(new Image("/resources/pause button.png"));
      consumer.setIsPaused(false);
    }
  }

  /********************************************************************
  * Metodo: pauseProducer
  * Funcao: pausa ou retoma a execucao do produtor
  * Parametros: event = MouseEvent, click do mouse sobre o botao de pause
  * Retorno: void
  ********************************************************************/
  @FXML
  void pauseProducer(MouseEvent event) {
    if(!producer.getIsPaused()){
      pauseButton1.setImage(new Image("/resources/resume button.png"));
      producer.setIsPaused(true);
    }
    else{
      pauseButton1.setImage(new Image("/resources/pause button.png"));
      producer.setIsPaused(false);
    }
  } 

  /********************************************************************
  * Metodo: reset
  * Funcao: reinicia os objetos e a execucao do produtor e do consumidor
  * Parametros: event = MouseEvent, click do mouse sobre o botao de reset
  * Retorno: void
  ********************************************************************/
  @FXML
  void reset(MouseEvent event) throws InterruptedException{

    producer.setReset(true);
    consumer.setReset(true);

    producer = new Producer(this, sliderProducer, progressBarProducer);
    consumer = new Consumer(this, sliderConsumer, progressBarConsumer);

    for (ImageView beer : beers) {
      beer.setVisible(false);
    }

    for(int i = 0; i < BUFFER_LENGTH; i++) {
      buffer[i] = false;
    }

    manDrinking.setVisible(false);
    manSitting.setVisible(true);
    barmanProducing.setVisible(false);
    barmanWaiting.setVisible(true);
    progressBarConsumer.setVisible(false);
    progressBarProducer.setVisible(false);
    progressBarConsumer.setProgress(0.0);
    progressBarProducer.setProgress(0.0);
    pauseButton1.setImage(new Image("/resources/pause button.png"));
    pauseButton2.setImage(new Image("/resources/pause button.png"));
    barmanProducing.setLayoutX(barmanPositions[0]);
    barmanWaiting.setLayoutX(barmanPositions[0]);
    sliderConsumer.setValue(50);
    sliderProducer.setValue(50);

    mutex = new Semaphore(1);
    items = new Semaphore(0);
    spaces = new Semaphore(BUFFER_LENGTH);

    producer.start();
    consumer.start();
  }

  /********************************************************************
  * Metodo: close
  * Funcao: para a execucao e fecha a tela
  * Parametros: event = MouseEvent, click do mouse sobre o botao de fechar
  * Retorno: void
  ********************************************************************/
  @FXML
  void close(MouseEvent event) {
    System.exit(0);
  }


  @Override
  public void initialize(URL arg0, ResourceBundle rb){
    beers.add(beer0);
    beers.add(beer1);
    beers.add(beer2);
    beers.add(beer3);
    beers.add(beer4);
    producer = new Producer(this, sliderProducer, progressBarProducer);
    consumer = new Consumer(this, sliderConsumer, progressBarConsumer);
    producer.start();
    consumer.start();
  }   
}
