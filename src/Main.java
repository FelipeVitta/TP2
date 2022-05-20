package src;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.lang.model.util.ElementScanner14;
import javax.xml.catalog.CatalogException;

public class Main {

   public static void main(String[] args) {

      Scanner sc = new Scanner(System.in);
      CRUD crud = new CRUD();
      int opcao, op;
      int id, pontos, partidas;
      String nome, cnpj, cidade;

      // MENU DE OPÇÕES
      System.out.println("\n MENU \n");
      System.out.println("O que você gostaria de fazer?");
      System.out.println("\n1 - Criar um clube");
      System.out.println("\n2 - Ler dados de um clube");
      System.out.println("\n3 - Atualizar dados de um clube");
      System.out.println("\n4 - Deletar um clube");
      System.out.println("\n5 - Realizar uma partida");
      System.out.println("\n6 - Procurar id do Clube pelo nome ou cidade\n");
      opcao = sc.nextInt();

      try {

         switch (opcao) {

            // CRIAR UM CLUBE

            case 1:
               // entrada de dados do usuário:
               // usuário digita nome
               System.out.println("\nDigite o nome do Clube\n");
               nome = sc.nextLine();
               nome = sc.nextLine();
               // usuário digita cnpj
               System.out.println("\nDigite o CNPJ do Clube\n");
               cnpj = sc.nextLine();
               System.out.println("\nDigite a cidade do Clube\n");
               // usuário digita cidade
               cidade = sc.nextLine();
               // chamada do método para a criação de um clube
               crud.createClub(new Clube(nome, cnpj, cidade));

               break;

            // LER DADOS DE UM CLUBE

            case 2:

               try {
                  // abrindo o arquivo de dados para ler e escrever
                  RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
                  int ultimoId = arq.readInt();
                  Clube b = new Clube();
                  System.out.println("\n");
                  // for que mostra todos os ids e nomes dos clubes validos no arquivo
                  for (int i = 1; i <= ultimoId; i++) {
                     // chamando o método de leitura de clube pelo id
                     b = crud.readClub(i);
                     if (b.getIdClube() > 0) {
                        System.out.println("ID:" + b.getIdClube() + "  Nome: " + b.getNome());
                     }
                  }
                  // obter o id desejado pelo usuário
                  System.out.println("\nDigite o ID do clube que deseja obter os dados: \n");
                  id = sc.nextInt();
                  b = crud.readClub(id);
                  // se o id do clube for valido, irá imprimi-lo na tela
                  if (b.getIdClube() > 0) {
                     System.out.println(b);
                  } else {
                     System.out.println("Registro não encontrado");
                  }
                  // fechando o arquivo
                  arq.close();
                  // tratamento de erros
               } catch (Exception e) {
                  System.out.println("Não foi possivel ler o arquivo");
                  e.printStackTrace();
               }

               break;

            // ATUALIZAR DADOS DE UM CLUBE

            case 3:

               try {
                  // abrindo o arquivo de dados para leitura e escrita
                  RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
                  int ultimoId = arq.readInt();
                  Clube b = new Clube();
                  System.out.println("\n");
                  // mostrando na tela os ids e o nome de todos os clubes validos
                  for (int i = 1; i <= ultimoId; i++) {
                     b = crud.readClub(i);
                     if (b.getIdClube() > 0) {
                        System.out.println("ID:" + b.getIdClube() + "  Nome: " + b.getNome());
                     }
                  }

                  Clube clube = new Clube();
                  // usuario digita o id que ele quer atualizar os dados
                  System.out.println("\nQual o ID do clube que você quer atualizar os dados?");
                  opcao = sc.nextInt();
                  // retornando o clube do respectivo id
                  clube = crud.readClub(opcao);
                  // se o id for valido:
                  if (clube.getIdClube() > 0) {
                     // confirmação de atualização
                     System.out.println("\nQuer mesmo atualizar os dados desse clube?: ");
                     System.out.println("\n1-SIM");
                     System.out.println("2-NÃO\n");
                     op = sc.nextInt();
                     if (op == 1) {
                        //usuario digita os novos dados do clube
                        System.out.println("\nDigite o novo nome: \n");
                        nome = sc.nextLine();
                        nome = sc.nextLine();
                        System.out.println("\nDigite o novo CNPJ: \n");
                        cnpj = sc.nextLine();
                        System.out.println("\nDigite a cidade: \n");
                        cidade = sc.nextLine();
                        //definindo partidas e pontos do clube colo igual ao anterior 
                        partidas = clube.getPartidasJogadas();
                        pontos = clube.getPontos();
                        //chamando o metodo para atualizar os dados
                        crud.updateClub(clube, nome, cnpj, cidade, partidas, pontos);
                     } else if (op == 2) {
                        System.out.println("Dados do clube mantidos");
                     } else {
                        System.out.println("Opção inválida");
                     }
                  } else {
                     System.out.println("Registro não encontrado");
                  }
                
                  //fechando o fluxo de dados para o arquivo
                  arq.close();
                  // tratamento de erros
               } catch (Exception e) {
                  System.out.println("ERRO");
                  e.printStackTrace();
               }
               break;

            // DELETAR UM CLUBE

            case 4:

               try {

                  RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
                  int ultimoId = arq.readInt();
                  Clube b = new Clube();
                  System.out.println("\n");
                  // for para mostrar todos os clubes do arquivo
                  for (int i = 1; i <= ultimoId; i++) {
                     b = crud.readClub(i);
                     if (b.getIdClube() != 0) {
                        System.out.println("ID:" + b.getIdClube() + "  Nome: " + b.getNome());
                     }
                  }

                  System.out.println("\nDigite o ID do clube que você quer deletar");
                  id = sc.nextInt();
                  // chamando o metodo no CRUD para ler as informações do clube com o id informado
                  b = crud.readClub(id);
                  if (b.getIdClube() > 0) {
                     System.out.println("\nQuer mesmo deletar esse Clube?: ");
                     System.out.println(b);
                     System.out.println("\n1-Sim");
                     System.out.println("2-Não\n");
                     op = sc.nextInt();
                     if (op == 1) {
                        // chamando o metodo no CRUD para deletar um Clube
                        crud.deleteClub(id);
                     } else if (op == 2) {
                        System.out.println("Clube não deletado");
                     } else {
                        System.out.println("Opção inválida");
                     }
                  } else {
                     System.out.println("Registro não encontrado");
                  }
                  arq.close();
                  // tratamento de erros
               } catch (Exception e) {
                  System.out.println("ERRO");
                  e.printStackTrace();
               }

               break;

            // REALIZAR UMA PARTIDA

            case 5:
               try {
                  //abrindo o arquivo de dados para leitura e escrita 
                  RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
                  int ultimoId = arq.readInt();
                  Clube b = new Clube();
                  System.out.println("\n");
                  // for para mostrar todos os clubes do arquivo
                  for (int i = 1; i <= ultimoId; i++) {
                     b = crud.readClub(i);
                     if (b.getIdClube() != 0) {
                        System.out.println("ID:" + b.getIdClube() + "  Nome: " + b.getNome());
                     }
                  }

                  int id1, id2;
                  int gols1, gols2;
                  Clube clube1 = new Clube();
                  Clube clube2 = new Clube();
                  //usuario digita o id dos clubes que ele quer realizar a partida
                  System.out.println("\nDigite o ID dos dois Clubes que gostaria de realizar a partida\n");
                  // lendos os ids escritos pelo usuário
                  id1 = sc.nextInt();
                  id2 = sc.nextInt();

                  // lendos as informações dos ids dos clubes escrito pelo usuario

                  clube1 = crud.readClub(id1);
                  clube2 = crud.readClub(id2);

                  // se os dois clubes existirem no arquivo

                  if (clube1.getNome() != null && clube2.getNome() != null) {

                     // lendo a quantidade de gols que cada clube fez

                     System.out.println("Quantos gols o " + clube1.getNome() + " fez?");
                     gols1 = sc.nextInt();
                     System.out.println("Quantos gols o " + clube2.getNome() + " fez?");
                     gols2 = sc.nextInt();

                     // chamando o metodo para realizar partida no CRUD
                     crud.teamMatch(clube1, gols1, clube2, gols2);

                  } else {
                     System.out.println("Clube(s) não encontrado(s)");
                  }

                  arq.close();

                  // tratamento de erros
               } catch (Exception e) {
                  System.out.println("ERRO");
                  e.printStackTrace();
               }
               break;

            // PROCURAR ID POR CLUBE OU CIDADE

            case 6:
               //criando um arraylist para armazenas os ids retornados
               ArrayList<Integer> arll = new ArrayList<Integer>();
               String resposta;
               //usuario digita o termo que ele quer encontrar na lista invertida
               System.out.println("\nDigite o nome ou a cidade do(s) Clube(s) que deseja retornar o id: \n");
               resposta = sc.nextLine();
               resposta = sc.nextLine();
               //arraylist criado recebe os ids do metodo para ler a lista invertida
               arll = crud.readList(resposta);
               //se o arraylist for igual a 0, não existem ids com o termo
               if (arll.size() == 0) {
                  System.out.println("Não foi encontrado nenhum clube");
               } else {
                  //imprimindo na tela os ids
                  System.out.println("IDS: " + arll.stream().distinct().collect(Collectors.toList()).toString());

               }
                
               break;

            default:
               System.out.println("\nValor inválido");
               break;

         }

         // tratamento de erros
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

}