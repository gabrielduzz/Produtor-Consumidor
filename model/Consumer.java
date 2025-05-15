/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 16/10/2023
* Ultima alteracao: 29/10/2023
* Nome: Producer.java
* Funcao: Consumir os objetos(cerveja)
********************************************************************/

package model;

import java.lang.Thread;
import control.PrincipalController;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.application.Platform;

public class Consumer extends Thread {
  private PrincipalController controller = new PrincipalController();
  private Slider slider;
  private boolean isPaused = false;
  private boolean reset = false;
  private ProgressBar progressBar;

  // construtor
  public Consumer(PrincipalController controller, Slider slider, ProgressBar progressBar) {
    this.controller = controller;
    this.slider = slider;
    this.progressBar = progressBar;
  }

  // setters e getters
  public void setIsPaused(boolean isPaused) {
    this.isPaused = isPaused;
  }

  public boolean getIsPaused() {
    return isPaused;
  }

  public void setReset(boolean reset) {
    this.reset = reset;
  }

  /********************************************************************
   * Metodo: getSpeed
   * Funcao: calcular a quantidade de tempo que a thread deve ficar
   * ficar bloqueada usando o valor do slider
   * Parametros: nenhum
   * Retorno: speedProducer(int) = quantidade de tempo que a thread deve
   * ficar bloqueada
   ********************************************************************/
  public int getSpeed() {
    double speedAux = slider.getValue();
    int speedConsumer = (int) speedAux;
    return speedConsumer * 50;
  }

  /********************************************************************
   * Metodo: run
   * Funcao: executa o codigo da thread
   * Parametros: nenhum
   * Retorno: void
   ********************************************************************/
  public void run() {
    while (true) {
      try {
        Consumer.sleep(getSpeed());

        //verifica se o reset foi solicitado e termina o laco caso tenha sido
        if (reset) {
          break;
        }

        //verifica se o pause foi solicitado e prende o laco enquanto for necessario
        while (isPaused && !reset) {
          Consumer.sleep(1);
        }

        //verifica se o reset foi solicitado e termina o laco caso tenha sido
        if (reset) {
          break;
        }

        PrincipalController.items.acquire(); //confere se tem items no buffer para consumir
        PrincipalController.mutex.acquire(); //confere se eh possivel acessar a regiao critica

        Consumer.sleep(100);  //pequeno tempo de bloqueio para que nao aconteca de o produtor colocar a cerveja no
                                     //balcao e o consumidor ja bebe-la instantaneamente

        //checar pause e reset
        if (reset) {
          break;
        }
        while (isPaused && !reset) {
          Consumer.sleep(1);
        }
        if (reset) {
          break;
        }

        Platform.runLater(() -> controller.consumeBeer()); //chama a funcao que consome a primeira cerveja

        //checa reset
        if (reset) {
          break;
        }

        /*as linhas a seguir contem os calculos necessarios para que a barra de progresso seja fiel a velocidade
        do slider. Vale ressaltar que a barra vai se mover na velocidade que o slider indicava no inicio da producao, ou seja,
        alterar o valor do slider durante a producao nao afetara a velocidade atual */

        double value = 1.0 / getSpeed();
        double scale = Math.pow(10, 4);
        value = Math.round(value * scale) / scale;
        double progress = 0;

        progressBar.setVisible(true);
        progressBar.setProgress(0.0);

        /*controla a barra de progresso, aumentando seu valor a cada iteracao e bloqueando a thread por determinado tempo
        para que o progresso seja equivalente ao tempo de bloqueio, representando o tempo de producao da cerveja*/
        while (progressBar.getProgress() <= 1.0) {
          // System.out.println(progress);
          progress += value;
          progressBar.setProgress(progress);
          double time = value * (Math.pow(10, 9));

          if (time < 1000000) {
            Consumer.sleep(0, (int) time);
          } else {
            int millis = (int) time / 1000000;
            Consumer.sleep(millis, (int) (time - (millis * 1000000)));
          }

          //checa pause ou reset
          while (isPaused && !reset) {
            Consumer.sleep(1);
          }
          if (reset) {
            break;
          }
        }

        progressBar.setVisible(false);

        //checa pause ou reset
        while (isPaused && !reset) {
          Consumer.sleep(1);
        }
        if (reset) {
          break;
        }

        Platform.runLater(() -> controller.stopDrinking()); //chama a funcao que muda a imagem do consumidor e realiza o 
                                                            //o efeito de esteira nas cervejas

        //checa reset
        if (reset) {
          break;
        }

        PrincipalController.mutex.release();
        PrincipalController.spaces.release();

        //checa pause ou reset
        while (isPaused && !reset) {
          Consumer.sleep(1);
        }
        if (reset) {
          break;
        }

      } catch (InterruptedException e) {
        // e.printStackTrace();
      }
    }
  }
}
