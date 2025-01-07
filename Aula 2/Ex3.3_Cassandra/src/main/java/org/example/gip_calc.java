package org.example;

import java.util.*;

public class gip_calc {
    // Classe para representar cada pessoa
    static class Pessoa {
        String nome;
        int custoHora;
        int[] competencias;

        public Pessoa(String nome, int custoHora, int[] competencias) {
            this.nome = nome;
            this.custoHora = custoHora;
            this.competencias = competencias;
        }
    }

    public static void main(String[] args) {
        // Lista completa de pessoas
        List<Pessoa> todasPessoas = Arrays.asList(
            new Pessoa("Alex", 86, new int[]{3, 1, 2, 2, 0}),
            new Pessoa("Ana", 160, new int[]{6, 5, 6, 3, 0}),
            new Pessoa("Diogo", 102, new int[]{3, 2, 4, 2, 0}),
            new Pessoa("Leonor", 87, new int[]{2, 3, 3, 0, 0}),
            new Pessoa("Felix", 89, new int[]{1, 0, 4, 0, 1}),
            new Pessoa("Rui", 89, new int[]{2, 4, 2, 0, 0}),
            new Pessoa("Jose", 135, new int[]{6, 3, 5, 2, 0}),
            new Pessoa("Joao", 125, new int[]{4, 6, 5, 0, 0}),
            new Pessoa("Miguel", 97, new int[]{1, 1, 0, 4, 6}),
            new Pessoa("Carlos", 70, new int[]{2, 0, 4, 0, 0}),
            new Pessoa("Marco", 89, new int[]{1, 0, 0, 5, 5}),
            new Pessoa("Paulo", 175, new int[]{5, 5, 6, 3, 1}),
            new Pessoa("Pedro", 128, new int[]{6, 4, 4, 1, 0}),
            new Pessoa("Maria", 151, new int[]{5, 3, 4, 5, 3}),
            new Pessoa("Tiago", 58, new int[]{3, 0, 3, 0, 0}),
            new Pessoa("Lucas", 97, new int[]{2, 4, 3, 0, 0})
        );

        Scanner scanner = new Scanner(System.in);

        // Entrada: Nomes das pessoas disponíveis
        System.out.println("Digite os nomes das pessoas disponíveis separados por vírgula:");
        String[] nomes = scanner.nextLine().split(",");

        // Filtrar as pessoas disponíveis
        List<Pessoa> pessoasDisponiveis = new ArrayList<>();
        for (String nome : nomes) {
            nome = nome.trim(); // Remover espaços extras
            for (Pessoa p : todasPessoas) {
                if (p.nome.equalsIgnoreCase(nome)) {
                    pessoasDisponiveis.add(p);
                    break;
                }
            }
        }

        // Entrada: Número de pessoas por grupo
        System.out.print("Digite o número de pessoas por grupo: ");
        int numPessoasPorGrupo = scanner.nextInt();

        // Gerar todas as combinações possíveis de grupos
        List<List<Pessoa>> combinacoes = gerarCombinacoes(pessoasDisponiveis, numPessoasPorGrupo);

        // Exibir todas as combinações e as estatísticas
        int grupoNumero = 1;
        for (List<Pessoa> grupo : combinacoes) {
            System.out.println("Grupo " + (grupoNumero++) + ":");
            int custoTotal = 0;
            int[] somaCompetencias = new int[5];

            for (Pessoa p : grupo) {
                System.out.println("- " + p.nome);
                custoTotal += p.custoHora;
                for (int j = 0; j < p.competencias.length; j++) {
                    somaCompetencias[j] += p.competencias[j];
                }
            }

            // Soma total das competências
            int somaTotalCompetencias = Arrays.stream(somaCompetencias).sum();

            System.out.println("Custo total: " + custoTotal);
            System.out.println("Competências totais (individuais): " + Arrays.toString(somaCompetencias));
            System.out.println("Soma total das competências: " + somaTotalCompetencias);
            System.out.println();
        }

        scanner.close();
    }

    // Método para gerar todas as combinações possíveis de grupos
    public static List<List<Pessoa>> gerarCombinacoes(List<Pessoa> pessoas, int tamanho) {
        List<List<Pessoa>> combinacoes = new ArrayList<>();
        combinar(new ArrayList<>(), pessoas, tamanho, 0, combinacoes);
        return combinacoes;
    }

    // Função recursiva para calcular combinações
    private static void combinar(List<Pessoa> grupoAtual, List<Pessoa> pessoas, int tamanho, int inicio,
                                 List<List<Pessoa>> combinacoes) {
        if (grupoAtual.size() == tamanho) {
            combinacoes.add(new ArrayList<>(grupoAtual));
            return;
        }

        for (int i = inicio; i < pessoas.size(); i++) {
            grupoAtual.add(pessoas.get(i));
            combinar(grupoAtual, pessoas, tamanho, i + 1, combinacoes);
            grupoAtual.remove(grupoAtual.size() - 1); // Backtracking
        }
    }
}


