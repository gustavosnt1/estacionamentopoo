import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Estacionamento {
    private String[] placas; //array de strings usada pra armazenar as placas
    private String livre = "Livre"; //usada pra marcar uma vaga como vazia no estaciona

    public Estacionamento(int capacidade) throws Exception { //recebe parametro capacidade q e o numero de vagas
        try { //o try vai verificar e  a capcidade e maior que 0 caso seja cria um novo array de string chamado placas com o capacidade + 1
            // e pra evitar o uso do indice 0 que ajuda na semantica do numero de vagas
            if (capacidade > 0)
                this.placas = new String[capacidade + 1]; // Inicializando o array de placas de acordo com as vagas
            validateArraysVagas(); //preenche as vagas do array placas como livre
        } catch (Exception ex) {
            throw new Exception("Não foi possível definir o número de vagas no estacionamento!");
        }
    }

/*    public String[] getPlacas() {
        return this.placas;
    }

    public void setPlacas(String[] placa){
        this.placas = placa;
    }*/

    public void validateArraysVagas() {
        for (int i = 1; i < this.placas.length; i++) { //percorre o array placas comecando do indice 1 ate que seja menor que o array placas
            if (this.placas[i] == null) //pra cada iteracao ele verifica se o elemento no indice e null(vaga vazia) caso seja
                this.placas[i] = this.livre; //a vaga vazia no indice i e preenchida com "livre"
        }
    }

    public void entrar(String placa, int vaga) throws Exception {
        FileWriter file = null;
        if (vaga < 1 || vaga > this.placas.length)
            throw new ArrayIndexOutOfBoundsException("Error E1 - Vaga inexistente");

        if (this.placas[vaga] != livre)
            throw new Exception("Error E2 - Vaga ocupada!");

        for (String p : this.placas) //percorre placas pra verificar se a placa fornecida ja esta no estacionamento
            if (p != null && p.equals(placa))
                throw new Exception("Error E3 - A placa já se encontra no estacionamento!");

        this.placas[vaga] = placa; //caso passe de todas verificacao a placa do carro e atribuida a vaga correspodente no array placas
        try {
            file = new FileWriter("historico.csv", true);

            LocalDateTime dataAtual = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            String data = dataAtual.format(formatter);

            String linha = data + "; " + placa + "; " + vaga + "; " + "entrada\n";
            file.write(linha);
            file.close();

        } catch (Exception ex) {
            throw new Exception("Error E4 - Não foi possível cadastrar o veículo na vaga!");
        }
        gravarDados();
    }

    public void sair(int vaga) throws Exception {
        FileWriter file = null;
        if (vaga < 1 || vaga > this.placas.length)
            throw new ArrayIndexOutOfBoundsException("Error S1 - Vaga inexistente");

        if (this.placas[vaga] == livre)
            throw new Exception("Error S2 - Vaga já está desocupada!");
        try {
            file = new FileWriter("historico.csv", true);

            LocalDateTime dataAtual = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            String data = dataAtual.format(formatter);

            String linha = data + "; " + this.placas[vaga] + "; " + vaga + "; " + "saída\n";
            file.write(linha);
            file.close();

            if (this.placas[vaga] != livre)//verifica se ja nao esta vazia
                this.placas[vaga] = this.livre; //caso sim atribuo "livre" a vaga
        } catch (Exception ex) {
            throw new Exception("Error S3 - Não foi possível retirar o veículo da vaga!");
        }
        gravarDados();
    }

    public int consultarPlaca(String placa) {
        int vaga = 1; //vaga e inicializa com valor 1
        for (int i = 1; i < this.placas.length; i++) { //percorre o array placas comecando do indice 1 ate o indice ser menor q o tamanho da array placas
            if (this.placas[i].equals(placa)) //a cada iteracao verifica e o valor do indice i de placas e igual a placa consultada
                return vaga; // caso verdadeira retorna o numero da vaga
            vaga++; //a vaga vai incrementando +1 pra acompanahr o numero da vaga em q a proxima placa vai se verificada
        }
        return -1; //caso n seja encontrada retorna -1 pra indicar q n foi encontrada
    }

    public void transferir(int vagaAtual, int vagaFutura) throws Exception {
        if (this.placas[vagaAtual] == this.livre)
            throw new Exception("Error T1 - A vaga atual está livre!");

        if (this.placas[vagaFutura] != this.livre)
            throw new Exception(("Error T2 - A vaga futura está ocupada!"));
        try {
            this.placas[vagaFutura] = this.placas[vagaAtual]; //tranferindo atribuindo o valor do indice vgfutura pra vgatual
            this.placas[vagaAtual] = this.livre; //atribuindo o valor do indice vgatual pra "livre"

        } catch (Exception ex) {
            throw new Exception("Error T4 - Não foi possível transferir o veículo!");
        }
        gravarDados();
    }

    public String[] listarGeral() {
        String[] result = new String[this.placas.length - 1]; // nova array de string e iniciada 

        for (int i = 1; i < this.placas.length; i++) {
            result[i - 1] = "Carro: " + this.placas[i] + "; Vaga: " + i; //cada iteracao o result e preench com infos
        }//como o loop comeca com i=1 mas o array comeca com indice 0 preciso subtrair -1de i pra correponder ao indices de result

        return result; //dp pecorre todass vg retorna result
    }

    public ArrayList<Integer> listarLivres() {
        ArrayList<Integer> result = new ArrayList<Integer>(this.placas.length); //crio uma lista result com o tamanho do array placas

        for (int i = 1; i < this.placas.length; i++) {
            if (this.placas[i] == this.livre) //verifica o valor no indice i e igual a livre
            {
                result.add(i); //se a vg tiver livre adiciono na lista o numero da vg(i)
            }
        }

        return result;//dp percorre toda vg retorna result
    }

    public void gravarDados() throws Exception {
        try {
            FileWriter file = new FileWriter("placas.csv");
            for (int i = 1; i < this.placas.length; i++) {
                String placa = this.placas[i];
                String vaga = String.valueOf(i);
                String linha = String.format(placa + ";" + vaga + "\n");
                file.write(linha);
            }
            System.out.println("Dados salvos com sucesso!");

            file.close();
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("Error GD1: Arquivo não encontrado!");
        } catch (IOException ex) {
            throw new IOException("Error GD2: Não foi possível gravar este arquivo!");
        } catch (Exception ex) {
            throw new Exception("Error GD3: Ocorreu um erro ao tentar gravar arquivo!");
        }
    }
    public void lerDados() throws FileNotFoundException { // ler do arquivo “placas.csv”, a placa de cada vaga ocupada no momento

        try {
            Scanner arquivo = new Scanner(new File("placas.csv"));
            String[] divisao;
            String linha;

            do {
                linha = arquivo.nextLine();
                divisao = linha.split(";");
                int vaga = Integer.parseInt(divisao[0]);
                this.placas[vaga - 1] = divisao[1];

            } while (arquivo.hasNextLine());

            arquivo.close();

        } catch (Exception e) {
            e.getMessage();
        }

    }
}