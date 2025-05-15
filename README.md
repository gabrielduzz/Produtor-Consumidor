# 🍞 Problema do Produtor-Consumidor com Buffer Circular em JavaFX

Projeto acadêmico baseado no problema clássico da concorrência conhecido como **Produtor-Consumidor**, desenvolvido com **JavaFX** e **programação concorrente** utilizando `Threads` e `Semáforos` para controlar o acesso ao buffer circular compartilhado.

---

## 📷 Demonstração

<img src="/resources/produtor-consumidor.gif">

---

## 🎯 Objetivo

Simular a interação entre **produtores** e **consumidores** que compartilham um **buffer circular**, garantindo que:

- Produtores insiram itens no buffer apenas quando houver espaço disponível.
- Consumidores retirem itens do buffer apenas quando houver itens disponíveis.
- A sincronização ocorra de forma segura e sem condições de corrida, utilizando:
  - Semáforo de espaço cheio (`full`)
  - Semáforo de espaço vazio (`empty`)
  - Semáforo de exclusão mútua (`mutex`)
- A movimentação e o estado do buffer sejam visualmente representados com **JavaFX**.

---

## 🛠️ Tecnologias Utilizadas

- **Java** (versão 8.0.1_351)  
- **JavaFX**  
- **Programação Concorrente com Threads**  
- **Semáforos**
