package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.event.InternalFrameEvent;

public class CRUD {

    // METODO PARA ADICIONAR UM NOVO CLUBE AOS ARQUIVOS
    public void createClub(Clube a) throws IOException {
        try {

            int ultimoID;
            byte[] ba;
            long pos;
            // abrindo o arquivo de dados e o arquivo de indices para leitura e escrita
            RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
            RandomAccessFile arq2 = new RandomAccessFile("dados/indices.db", "rw");

            // se o arquivo estiver vazio, escrever o ultimo id usado como 0
            if (arq.length() == 0) {
                arq.seek(0);
                arq.writeInt(0);
            }
            // indo para a posição 0 do arquivo de dados
            arq.seek(0);
            // lendo o ultimo id usado
            ultimoID = arq.readInt();
            // definindo o id do clube criando como o ultimo id + 1
            a.setIdClube(ultimoID + 1);
            // definindo pontos e partidas jogais iniciais como 0
            a.setPontos(0);
            a.setPartidasJogadas(0);
            // indo para o incio do arquivo
            arq.seek(0);
            // escrevendo o ultimo id usado como o id do novo clube
            arq.writeInt(a.getIdClube());
            // passando as informações do registro para um vetor de bytes
            ba = a.toByteArray();
            // indo para o final do arquivo
            arq.seek(arq.length());
            // capturando a posição atual do ponteiro
            pos = arq.getFilePointer();
            // escrevendo informações do registro
            arq.writeChars("'");
            arq.writeInt(ba.length);
            arq.write(ba);
            // escrevendo no final do arquivo de indices o id do novo clube e a posição que
            // ele se encontra no arquivo de dados
            arq2.seek(arq2.length());
            arq2.writeInt(a.getIdClube());
            arq2.writeLong(pos);
            // chamando metodo para escrever novos valores na lista invertida
            writeList(a.getIdClube(), a.getNome());
            writeList(a.getIdClube(), a.getCidade());

            System.out.println(a);
            // fechando os dois arquivos
            arq.close();
            arq2.close();

            // tratamento de erros
        } catch (Exception e) {
            System.out.println("\nERRO: Não foi possivel criar o Clube\n");
            e.printStackTrace();
        }

    }

    // METODO QUE LÊ DADOS DOS ARQUIVOS
    public Clube readClub(int a) throws Exception {

        char lapide;
        int tamanhoReg, ultimoID;
        long posArquivo;
        // abrindo o arquivo de dados para leitura e escrita
        RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
        Clube c = new Clube();
        // lendo o ultimo id usado
        ultimoID = arq.readInt();
        // se o id informado for igual ou menor a zero, o id informado é inválido
        if (a <= 0) {
            System.out.println("\nID INFORMADO INVÁLIDO\n");
        } else {
            // se o id informado for maior que o ultimo id usado, o clube ainda não existe
            if (a > ultimoID) {
                System.out.println("\nEsse Clube ainda não existe");
            } else {
                // chamando o método de pesquisa binária
                posArquivo = pesquisaBinaria(a);
                if (posArquivo != -1) {
                    // colocando o ponteiro na posição do registro com o id informado e lendo a
                    // lápide
                    arq.seek(posArquivo);
                    lapide = arq.readChar();
                    // se o arquivo ainda não foi excluido, extrair os dados do registro
                    if (lapide != '*') {
                        tamanhoReg = arq.readInt();
                        c.setIdClube(arq.readInt());
                        c.setNome(arq.readUTF());
                        c.setCnpj(arq.readUTF());
                        c.setCidade(arq.readUTF());
                        c.setPartidasJogadas(arq.readInt());
                        c.setPontos(arq.readInt());
                    } else {
                        System.out.println("Registro excluido");
                    }

                } else {
                    return c;
                }
            }
            arq.close();
        }

        return c;

    }

    // METODO QUE ATUALIZA DADOS DOS ARQUIVOS (recebe o clube antigo e os novos
    // dados)
    public void updateClub(Clube c, String nome, String cnpj, String cidade, int partidas, int pontos) {
        try {
            // abrindo o arquivo de dados e o arquivo de indices para leitura e escrita
            RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
            RandomAccessFile arq2 = new RandomAccessFile("dados/indices.db", "rw");
            // chamando o método para atualizar os valores na lista invertida para a cidade
            // e para o clube
            updateList(c.getIdClube(), c.getNome(), nome);
            updateList(c.getIdClube(), c.getCidade(), cidade);
            long pos;
            char lapide;
            int tamanhoReg;
            byte[] bytee;
            // chamando o metodo de pesquisa binaria no arquivo de indices (que retorna a
            // posição no arquivo de dados)
            pos = pesquisaBinaria(c.getIdClube());
            // colocando o ponteiro na posição retornada
            arq.seek(pos);
            // se a posição for diferente de -1, colocar os novos valores do clube
            if (pos != -1) {
                c.setNome(nome);
                c.setCnpj(cnpj);
                c.setCidade(cidade);
                c.setPartidasJogadas(partidas);
                c.setPontos(pontos);
                // passando os novos dados do clube para um array de bytes
                bytee = c.toByteArray();
                lapide = arq.readChar();
                if (lapide != '*') {
                    tamanhoReg = arq.readInt();
                    // se o tamanho do registro antigo for maior que o novo, escrever o novo na
                    // mesma posição
                    if (bytee.length <= tamanhoReg) {
                        arq.write(bytee);
                    } else {
                        // se o tamanho do registro antigo for menor que o novo
                        long posNova;
                        // indo para a posição retornada
                        arq.seek(pos);
                        // escrevendo lápide no registro
                        arq.writeChars("*");
                        // indo para o final do arquivo
                        arq.seek(arq.length());
                        // pegando a nova posição do registro atualizado no final do arquivo
                        posNova = arq.getFilePointer();
                        // escrevendo os dados do registro no arquivo
                        arq.writeChars("'");
                        arq.writeInt(bytee.length);
                        arq.write(bytee);
                        // chamando o método para atualizar a posição no arquivo de indices
                        atualizarPosicao(c.getIdClube(), posNova);
                    }

                } else {
                    System.out.println("Registro não encontrado");
                }
            } else {
                System.out.println("Registro excluido");
            }

            // fechando fluxos de dados para os arquivos
            arq.close();
            arq2.close();

            // tratando erros
        } catch (Exception e) {
            System.out.println("Não foi possivel atualizar as informações do Clube");
            e.printStackTrace();
        }

    }

    // METODO QUE DELETA UM CLUBE DOS ARQUIVOS
    public void deleteClub(int c) {
        try {
            char lapide;
            long pos;
            // retornando o clube com o id informado
            Clube a = readClub(c);
            // chamando o método para deletar os ids da lista invertida para a cidade e para
            // o nome do clube
            deleteList(a.getIdClube(), a.cidade);
            deleteList(a.getIdClube(), a.nome);
            // abrindo o arquivo de dados para leitura e escrita
            RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
            // chamando o metodo de pesquisa binaria no arquivo de indices
            pos = pesquisaBinaria(c);
            // se a posição retornada for diferente de -1{}
            if (pos != -1) {
                // indo para a posição retornada pela pesquisa binaria
                arq.seek(pos);
                // lendo a lápide
                lapide = arq.readChar();
                // dupla checagem
                if (lapide != '*') {
                    // indo para a posição retornada
                    arq.seek(pos);
                    // atualizando lápide como: registro excluido
                    arq.writeChars("*");
                    // chamando o método para atualizar posição no arquivo de indices
                    atualizarPosicao(c, -1);
                } else {
                    System.out.println("Registro já excluido");
                }
            } else {
                System.out.println("Registro já excluido");
            }

            // fechando fluxos de dados para o arquivo
            arq.close();
            // tratando erros
        } catch (Exception e) {
            System.out.println("ERRO: Não foi possivel deletar o arquivo");
            e.printStackTrace();
        }

    }

    // METODO QUE REALIZA UMA PARTIDA ENTRE CLUBES
    public void teamMatch(Clube a, int gol1, Clube b, int gol2) throws Exception {

        RandomAccessFile arq = new RandomAccessFile("dados/clubes.db", "rw");
        // SE O PRIMEIRO TIME GANHAR, ADICIONAR +1 PARTIDA AOS DOIS CLUBES E ADICIONAR +
        // 3 PONTOS PARA DO CLUBE a
        if (gol1 > gol2) {
            updateClub(a, a.getNome(), a.getCnpj(), a.getCidade(), a.getPartidasJogadas() + 1, a.getPontos() + 3);
            updateClub(b, b.getNome(), b.getCnpj(), b.getCidade(), b.getPartidasJogadas() + 1, b.getPontos());
            System.out.println("\nO " + a.getNome() + " ganhou a partida!");
            // SE O SEGUNDO TIME GANHAR, ADICIONAR +1 PARTIDA AOS DOIS CLUBE E ADICIONAR + 3
            // PONTOS PARA O CLUBE b
        } else if (gol1 < gol2) {
            updateClub(b, b.getNome(), b.getCnpj(), b.getCidade(), b.getPartidasJogadas() + 1, b.getPontos() + 3);
            updateClub(a, a.getNome(), a.getCnpj(), a.getCidade(), a.getPartidasJogadas() + 1, a.getPontos());
            System.out.println("\nO " + b.getNome() + " ganhou a partida!");
            // SE A PARTIDA EMPATAR, ADICIONAR +1 PARTIDA AOS DOIS CLUBES E +1 PONTO AOS
            // DOIS CLUBES
        } else if (gol1 == gol2) {
            updateClub(a, a.getNome(), a.getCnpj(), a.getCidade(), a.getPartidasJogadas() + 1, a.getPontos() + 1);
            updateClub(b, b.getNome(), b.getCnpj(), b.getCidade(), b.getPartidasJogadas() + 1, b.getPontos() + 1);
            System.out.println("\nO jogo empatou!");
        }
        // fechando fluxos de dados para o arquivo
        arq.close();

    }

    // METODO PARA DELETAR OS IDS DA LISTA INVERTIDA
    public void deleteList(int id, String string) throws IOException {
        String nomeArq;
        long pos;
        // abrindo a lista invertida para leitura e escrita
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        // enquanto não for o final do arquivo
        while (arq3.getFilePointer() < arq3.length()) {
            // lendo o termo
            nomeArq = arq3.readUTF();
            // se o termo for igual a string informada pelo usuário:
            if (nomeArq.toLowerCase().equals(string.toLowerCase())) {
                // colocando o ponteiro na posição em que o id é igual ao id do clube
                while (arq3.readInt() != id)
                    ;
                // sobrescrevendo o id encontrado e escrevendo 0 para ser reutilizado novamente
                arq3.seek(arq3.getFilePointer() - 4);
                arq3.writeInt(0);
                // saindo do while
                break;
            } else {
                // se o termo não for igual ao informado, pular os espaços dos ids e ler o
                // proximo termo
                arq3.seek(arq3.getFilePointer() + 80);
            }
        }

        arq3.close();

    }

    // METODO PARA ATUALIZAR OS VALORES NA LISTA INVERTIDA
    public void updateList(int id, String antigo, String novo) throws IOException {
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        // se a string informada pelo usuário não manteve o mesmo nome:
        if (!antigo.equals(novo)) {
            // chamando o metodo para deletar da lista invertida
            deleteList(id, antigo);
            // chamando o metodo para escrever o novo termo na lista invertida
            writeList(id, novo);
        }

    }

    // METODO PARA ESCREVER NA LISTA INVERTIDA
    public void writeList(int id, String string) throws IOException {
        // abrindo o arquivo da lista invertida para leitura e escrita
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        String nome;
        long pos;
        // se o tamanho do arquivo for igual a 0, escrever no inicio do arquivo a string
        // e o id
        if (arq3.length() == 0) {
            arq3.writeUTF(string.toLowerCase());
            arq3.writeInt(id);
            // escrevendo os espaços reservados para os ids que contem aquele termo
            for (int i = 0; i < 19; i++) {
                arq3.writeInt(0);
            }
        } else {
            // chamando o método que retorna a posição para ser escrito o id na lista
            // invertida
            pos = confirmList(id, string);
            // se a posição for diferente de -1 (o metodo retorna -1 se ainda não existir o
            // termo na lista invertida): escrever
            if (pos != -1) {
                arq3.seek(pos);
                arq3.writeInt(id);
            } else {
                // escrevendo no final do arquivo
                arq3.seek(arq3.length());
                arq3.writeUTF(string.toLowerCase());
                arq3.writeInt(id);
                // escrevendo os espaços reservados para aquele termo
                for (int i = 0; i < 19; i++) {
                    arq3.writeInt(0);
                }
            }

        }

    }

    // METODO QUE RETORNA OS IDS QUE CONTEM A STRING PASSADA
    public ArrayList<Integer> readList(String string) throws IOException {
        // criando um arraylist de inteiros para armazenar os ids
        ArrayList<Integer> arl = new ArrayList<Integer>();
        int id;
        // abrindo o arquivo da lista invertida para escrita e leitura
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        // enquanto não for o final do arquivo
        while (arq3.getFilePointer() < arq3.length()) {
            // se a string informada (em letra minuscula) for igual a string lida (em letra
            // minuscula)
            if (string.toLowerCase().equals(arq3.readUTF().toLowerCase())) {
                // lendo os espaços reservados para os ids
                for (int i = 0; i < 20; i++) {
                    id = arq3.readInt();
                    // apenas se o id for diferente de 0, ele vai adicionar no arraylist para
                    // retornar para o usuário
                    if (id != 0) {
                        arl.add(id);
                    }
                }
            } else {
                // senão: pulando os espaços reservados para os ids daquele termo
                arq3.seek(arq3.getFilePointer() + 80);
            }

        }
        // fechando o fluxo de dados para o arquivo
        arq3.close();
        // retornando o arraylist
        return arl;
    }

    // MÉTODO PARA CONFIMAR SE O PARAMETRO PASSADO PELO USUÁRIO JÁ EXISTE NA LISTA
    // INVERTIDA (retorna uma posição valida para escrita de um novo id)
    public long confirmList(int id, String string) throws IOException {
        String nomeArq;
        long pos;
        int idz;
        // abrindo o arquivo da lista invertida para escrita e leitura
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        // enquanto não for o final do arquivo
        while (arq3.getFilePointer() < arq3.length()) {
            // lendo o termo
            nomeArq = arq3.readUTF();
            // se o termo for igual a string informada (o termo existe na lista invertida)
            if (nomeArq.equals(string.toLowerCase())) {
                // enquanto não encontrar um espaço para escrever um novo id
                while (true) {
                    pos = arq3.getFilePointer();
                    idz = arq3.readInt();
                    // se for encontrado um espaço para escrever um novo id
                    if (idz == 0) {
                        // retornando a posição valida para escrita de um novo id
                        return pos;
                    }
                }
            } else {
                // pulando todos os espaços reservados para ids daquele termo
                arq3.seek(arq3.getFilePointer() + 80);
            }
        }
        // fechando o fluxo de dados do arquivo
        arq3.close();
        // não foi encontrado o termo na lista, retornando -1
        return -1;
    }

    // METODO DE PESQUISA BINARIA NO ARQUIVO DE ÍNDICE
    private long pesquisaBinaria(int id) throws IOException {
        //abrindo o arquivo de indices para leitura e escrita
        RandomAccessFile arq2 = new RandomAccessFile("dados/indices.db", "rw");
        long inicio = 0;
        long fim = arq2.length();
        long pos, tam, meio;
        int idArq;
        //enquanto o fim do escopo de procura for maior ou igual ao inicio do escopo de procura
        while (inicio <= fim) {
            tam = inicio + fim;
            // colocando o ponteiro no meio em um id válido no arquivo de índices
            if ((tam / 12) % 2 == 0) {
                meio = tam / 2;
            } else {
                meio = (tam / 2) - 6;
            }
            //indo para a posição do id
            arq2.seek(meio);
            //lendo o id
            idArq = arq2.readInt();
            //se o id for igual ao informado
            if (id == idArq) {
                //lendo a posição no arquivo de dados
                pos = arq2.readLong();
                //fechando o fluxo de dados do arquivo
                arq2.close();
                //retornando a posição encontrada
                return pos;
                //se o id for maior que o lido no arquivo
            } else if (id > idArq) {
                //o inicio do escopo de procura dos ids se torna o meio
                inicio = meio;
                //se o id for menor que o lido no arquivo
            } else if (id < idArq) {
                //o fim do escopo de procura dos ids se torna o meio
                fim = meio;
            }

        }
        //fechando o fluxo de dados do arquivo
        arq2.close();
        //o id não for encontrado no arquivo de indices, assim retornando -1
        return -1;

    }

    // METODO DE PESQUISA BINÁRIA PARA ATUALIZAR A NOVA POSIÇÃO NO ARQUIVO DE ÍNDICE
    private void atualizarPosicao(int id, long novaPos) throws IOException {
        //abrindo o arquivo indices para leitura e escrita
        RandomAccessFile arq2 = new RandomAccessFile("dados/indices.db", "rw");
        long inicio = 0;
        long fim = arq2.length();
        long tam, meio;
        int idArq;
        //enquanto o fim do escopo de procura for maior ou igual ao inicio do escopo de procura
        while (inicio <= fim) {
            tam = inicio + fim;
            // colocando o ponteiro no meio em um id válido no arquivo de índices
            if ((tam / 12) % 2 == 0) {
                meio = tam / 2;
            } else {
                meio = (tam / 2) - 6;
            }
            //indo para a posição do id
            arq2.seek(meio);
            //lendo o id
            idArq = arq2.readInt();
            //se o id for igual ao informado
            if (id == idArq) {
                //escrevendo a nova posição no arquivo de indices
                arq2.writeLong(novaPos);
                arq2.close();
                //saindo da repetição
                break;
                //se o id for maior que o lido no arquivo
            } else if (id > idArq) {
                //o inicio do escopo de procura dos ids se torna o meio
                inicio = meio;
                //se o id for menor que o lido no arquivo
            } else if (id < idArq) {
                //o fim do escopo de procura dos ids se torna o meio
                fim = meio;
            }

        }
        //fechando o fluxo de dados do arquivo
        arq2.close();

    }

}
