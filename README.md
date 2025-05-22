
# Calculadora Interativa com Suporte a Variáveis e Expressões (Java)

Este projeto é uma calculadora interativa implementada em Java com funcionalidades de avaliação de expressões infixas, conversão para notação pós-fixa (posfixa), gerenciamento de variáveis, e suporte a comandos especiais.

## Funcionalidades

- Avaliação de expressões matemáticas com suporte a parênteses, operadores e variáveis.
- Conversão de expressões de notação infixa para posfixa.
- Atribuição e armazenamento de variáveis (A = 10).
- Listagem e reinicialização de variáveis.
- Detecção de expressões inválidas e tratamento de exceções.
- Comandos interativos: `EXIT`, `VARS`, `RESET`, `CONVERTER`.

## Como usar

1. Compile os arquivos Java:

```bash
javac Main.java
```

2. Execute o programa:

```bash
java Main
```

3. Utilize os comandos disponíveis:

- `EXIT`: encerra o programa.
- `VARS`: lista todas as variáveis armazenadas.
- `RESET`: apaga todas as variáveis.
- `CONVERTER <expressao>`: exibe a expressão infixa e sua equivalente pós-fixa.
- `A = 5`: atribui valor a uma variável (A).
- `<expressao>`: avalia qualquer expressão matemática, podendo usar variáveis e operadores.

## Exemplo de Uso

```bash
> A = 5
A = 5.0
> B = 3
B = 3.0
> A + B * 2
Resultado: 11.0
> CONVERTER A + B * 2
Infixa: A + B * 2
Posfixa: A B 2 * +
> VARS
A = 5.0
B = 3.0
> RESET
Variáveis apagadas
```

## Estrutura do Código

- `Main`: inicia o REPL.
- `REPL`: gerencia o loop de entrada e processa os comandos.
- `GerenciadorVariaveis`: gerencia o armazenamento e recuperação de variáveis.
- `Variavel`: representa uma variável.
- `AvaliadorExpressoes`: valida, converte e avalia expressões.
- `PilhaChar` e `PilhaDouble`: implementações de pilhas para operadores e operandos.

## Requisitos

- Java 8 ou superior
- Terminal para entrada interativa

## Melhorias Futuras

- Suporte a mais operadores e funções matemáticas.
- Implementação de variáveis de múltiplos caracteres.
- Persistência de variáveis entre execuções.
- Interface gráfica.

## Autor

- Desenvolvido por Gabriel Medina Peres.