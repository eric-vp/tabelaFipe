package com.example.tabelaFipe.principal;

import com.example.tabelaFipe.model.Dados;
import com.example.tabelaFipe.model.Modelos;
import com.example.tabelaFipe.model.Veiculo;
import com.example.tabelaFipe.service.ConsumoApi;
import com.example.tabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitor = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1";

    public void exibeMenu() {
        boolean opcaoValida = false;
        String endereco = "";

        do {
            var menu = """
                    *** OPÇÕES ***
                    
                    1 - Carro
                    2 - Moto
                    3 - Caminhão
                    
                    Selecione uma opção:
                    """;
            System.out.println(menu);
            var opcao = leitor.nextLine();

            if (opcao.equalsIgnoreCase("carro") || opcao.equalsIgnoreCase("1")) {
                opcaoValida = true;
                endereco = URL_BASE + "/carros/marcas";
            } else if (opcao.equalsIgnoreCase("moto") || opcao.equalsIgnoreCase("2")) {
                opcaoValida = true;
                endereco = URL_BASE + "/motos/marcas";
            } else if (opcao.contains("caminh") || opcao.equalsIgnoreCase("3")) {
                opcaoValida = true;
                endereco = URL_BASE + "/caminhoes/marcas";
            } else {
                System.out.println("Opção inválida!");
            }
        } while (!opcaoValida);

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca para consulta: ");
        var codigoMarca = leitor.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do veículo a ser buscado: ");
        var nomeVeiculo = leitor.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nInforme o código do modelo para buscar valores de avaliação:");
        var codigoModelo = leitor.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();
        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);
    }
}
