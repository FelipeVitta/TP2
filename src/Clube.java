package src;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Clube {

    //protected permite acesso as classes filhas
    protected int idClube;
    protected String nome;
    protected String cnpj;
    protected String cidade;
    protected int partidasJogadas;
    protected int pontos;

    public Clube(String nome, String cnpj, String cidade){
        this.nome = nome;
        this.cnpj = cnpj;
        this.cidade = cidade;
    }

    public Clube(int id, String nome, String cnpj, String cidade, int partidas, int pontos){
        this.idClube = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.cidade = cidade;
        this.partidasJogadas = partidas;
        this.pontos = pontos;
    }

    public Clube(){

    }


    public int getIdClube() {
        return idClube;
    }

    public void setIdClube(int idClube) {
        this.idClube = idClube;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getPartidasJogadas() {
        return partidasJogadas;
    }

    public void setPartidasJogadas(int partidasJogadas) {
        this.partidasJogadas = partidasJogadas;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    //metodo que retorna uma string ao chamar o objeto
    public String toString(){
        return "\nNome: " + nome + "\nCNPJ: " + cnpj + "\nCidade: " + cidade + "\nPartidas Jogadas: " + partidasJogadas + "\nPontos: " + pontos;
    }

     //metodo para passar as variaveis do objeto para um vetor de bytes
    public byte[] toByteArray() throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();   
        DataOutputStream dos = new DataOutputStream(baos);       
        dos.writeInt(idClube);        
        dos.writeUTF(nome);              
        dos.writeUTF(cnpj);
        dos.writeUTF(cidade);
        dos.writeInt(partidasJogadas);
        dos.writeInt(pontos);
        return baos.toByteArray();           
     }
 

    
}
