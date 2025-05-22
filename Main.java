import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new REPL().iniciar(); // inicia o REPL
    }
}

class REPL {
    private final GerenciadorVariaveis gerenciador = new GerenciadorVariaveis(); 
    private final AvaliadorExpressoes avaliador = new AvaliadorExpressoes(); 
    private boolean executando = true; 

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Calculadora - Comandos: EXIT, VARS, RESET, CONVERTER");

        while (executando) { 
            System.out.print("> "); 
            String input = scanner.nextLine().trim(); 

            try {
                if (input.equalsIgnoreCase("EXIT")) {
                    executando = false; 
                } else if (input.equalsIgnoreCase("VARS")) {
                    listarVariaveis(); 
                } else if (input.equalsIgnoreCase("RESET")) {
                    resetarVariaveis(); 
                } else if (input.toUpperCase().startsWith("CONVERTER ")) {
                    mostrarConversao(input.substring(10)); 
                } else if (input.matches("[a-zA-Z]\\s*=\\s*-?\\d+(\\.\\d+)?")) {
                    processarAtribuicao(input);
                } else if (!input.isEmpty()) {
                    avaliarExpressao(input);
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage()); 
            }
        }
        scanner.close();
    }

    private void mostrarConversao(String infixa) {
        String normalizada = infixa.replaceAll("\\s+", " ").trim(); 
        if (!avaliador.validarExpressao(normalizada)) {
            System.out.println("Expressão inválida!");
            return;
        }
        String posfixa = avaliador.converterParaPosfixa(normalizada); 
        System.out.println("Infixa: " + normalizada);
        System.out.println("Posfixa: " + posfixa);
    }

    private void listarVariaveis() {
        System.out.println(gerenciador.listarVariaveis()); 
    }

    private void resetarVariaveis() {
        gerenciador.reset(); 
        System.out.println("Variáveis apagadas");
    }

    private void processarAtribuicao(String input) {
        String[] partes = input.replaceAll("\\s+", "").split("="); 
        char nome = partes[0].charAt(0); 
        double valor = Double.parseDouble(partes[1]); 
        gerenciador.setValor(nome, valor); 
        System.out.println(nome + " = " + valor);
    }

    private void avaliarExpressao(String expr) {
        String normalizada = expr.replaceAll("\\s+", " ").trim(); 
        if (!avaliador.validarExpressao(normalizada)) {
            System.out.println("Expressão inválida!");
            return;
        }
        String posfixa = avaliador.converterParaPosfixa(normalizada); 
        double resultado = avaliador.avaliar(posfixa, gerenciador); 
        System.out.println("Resultado: " + resultado);
    }
}

class GerenciadorVariaveis {
    private final Variavel[] variaveis = new Variavel[26]; 
    private int quantidade = 0; 

    public GerenciadorVariaveis() {} 

    public void setValor(char nome, double valor) {
        nome = Character.toUpperCase(nome); 
        for (int i = 0; i < quantidade; i++) {
            if (variaveis[i].getNome() == nome) {
                variaveis[i].setValor(valor); 
                return;
            }
        }
        variaveis[quantidade++] = new Variavel(nome, valor); 
    }

    public double getValor(char nome) {
        nome = Character.toUpperCase(nome);
        for (int i = 0; i < quantidade; i++) {
            if (variaveis[i].getNome() == nome) {
                return variaveis[i].getValor(); 
            }
        }
        throw new RuntimeException("Variável " + nome + " não definida"); 
    }

    public String listarVariaveis() {
        if (quantidade == 0) return "Nenhuma variável definida";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < quantidade; i++) {
            sb.append(variaveis[i]).append("\n"); 
        }
        return sb.toString();
    }

    public void reset() {
        quantidade = 0; 
    }
}

class Variavel {
    private final char nome; 
    private double valor; 

    public Variavel(char nome, double valor) {
        this.nome = Character.toUpperCase(nome);
        this.valor = valor;
    }

    public char getNome() {
        return nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return nome + " = " + valor; 
    }
}

class AvaliadorExpressoes {
    private final PilhaChar pilhaOp = new PilhaChar(100); 
    private final PilhaDouble pilhaNum = new PilhaDouble(100); 

    public boolean validarExpressao(String expr) {
        int parenteses = 0; 
        boolean esperandoOperando = true; 
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) continue; 
            
            if (c == '(') {
                parenteses++;
                esperandoOperando = true;
            }
            else if (c == ')') {
                parenteses--;
                esperandoOperando = false;
            }
            else if (isOperadorUnario(c, esperandoOperando)) {
                esperandoOperando = true;
            }
            else if (isOperador(c)) {
                if (esperandoOperando && c != '-') {
                    return false; 
                }
                esperandoOperando = true;
            }
            else if (!Character.isLetterOrDigit(c) && c != '.') {
                return false; 
            }
            else {
                esperandoOperando = false;
            }
            
            if (parenteses < 0) return false; 
        }
        return parenteses == 0; 
    }

    public String converterParaPosfixa(String infixa) {
        pilhaOp.limpar();
        StringBuilder posfixa = new StringBuilder();
        boolean esperandoOperando = true;

        for (int i = 0; i < infixa.length(); i++) {
            char c = infixa.charAt(i);
            if (Character.isWhitespace(c)) continue;

            if (Character.isLetterOrDigit(c) || c == '.') {
                posfixa.append(c).append(' '); 
                esperandoOperando = false;
            } 
            else if (isOperadorUnario(c, esperandoOperando)) {
                pilhaOp.empilhar('~'); 
                esperandoOperando = true;
            }
            else if (c == '(') {
                pilhaOp.empilhar(c); 
                esperandoOperando = true;
            } 
            else if (c == ')') {
                while (!pilhaOp.estaVazia() && pilhaOp.topo() != '(') {
                    posfixa.append(pilhaOp.desempilhar()).append(' '); 
                }
                pilhaOp.desempilhar(); 
            } 
            else if (isOperador(c)) {
                while (!pilhaOp.estaVazia() && precedencia(pilhaOp.topo()) >= precedencia(c)) {
                    posfixa.append(pilhaOp.desempilhar()).append(' '); 
                }
                pilhaOp.empilhar(c); 
                esperandoOperando = true;
            }
        }

        while (!pilhaOp.estaVazia()) {
            posfixa.append(pilhaOp.desempilhar()).append(' '); 
        }

        return posfixa.toString().trim(); 
    }

    public double avaliar(String posfixa, GerenciadorVariaveis gerenciador) {
        pilhaNum.limpar();
        for (String token : posfixa.split("\\s+")) {
            try {
                pilhaNum.empilhar(Double.parseDouble(token)); 
            } catch (NumberFormatException e) {
                if (token.equals("~")) {
                    double a = pilhaNum.desempilhar();
                    pilhaNum.empilhar(-a); 
                }
                else if (Character.isLetter(token.charAt(0))) {
                    pilhaNum.empilhar(gerenciador.getValor(token.charAt(0))); 
                } else {
                    double b = pilhaNum.desempilhar();
                    double a = pilhaNum.desempilhar();
                    pilhaNum.empilhar(calcular(a, b, token.charAt(0))); 
                }
            }
        }
        return pilhaNum.desempilhar(); 
    }

    private boolean isOperador(char c) {
        return "+-*/^".indexOf(c) != -1;
    }

    private boolean isOperadorUnario(char c, boolean esperandoOperando) {
        return c == '-' && esperandoOperando; 
    }

    private int precedencia(char op) {
        return switch (op) {
            case '^' -> 4; 
            case '~' -> 3;
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> 0;
        };
    }

    private double calcular(double a, double b, char op) {
        if (op == '/' && b == 0) {
        throw new ArithmeticException("divisão por zero");
    }
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            case '^' -> Math.pow(a, b);
            default -> throw new RuntimeException("Operador inválido");
        };
    }
}

class PilhaChar {
    private final char[] elementos; 
    private int topo; 

    public PilhaChar(int capacidade) {
        elementos = new char[capacidade];
        topo = -1; 
    }

    public void empilhar(char elemento) {
        elementos[++topo] = elemento; 
    }

    public char desempilhar() {
        return elementos[topo--]; 
    }

    public char topo() {
        return elementos[topo]; 
    }

    public boolean estaVazia() {
        return topo == -1; 
    }

    public void limpar() {
        topo = -1; 
    }
}

class PilhaDouble {
    private final double[] elementos; 
    private int topo; 

    public PilhaDouble(int capacidade) {
        elementos = new double[capacidade];
        topo = -1; 
    }

    public void empilhar(double elemento) {
        elementos[++topo] = elemento; 
    }

    public double desempilhar() {
        return elementos[topo--]; 
    }

    public boolean estaVazia() {
        return topo == -1; 
    }

    public void limpar() {
        topo = -1; 
    }
}