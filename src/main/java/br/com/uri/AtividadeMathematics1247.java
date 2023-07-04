package br.com.uri;

import org.jfree.data.category.DefaultCategoryDataset;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static br.com.uri.Utils.TAMANHO_DO_VETOR;
import static br.com.uri.Utils.quantidadeDeThreads;

public class AtividadeMathematics1247 {


    private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public static void main(String[] args) {

        Random random = new Random();
        List<Integer> distanciaEntreLadraoEPolicia = new ArrayList<>();
        List<Integer> velidadeLadrao = new ArrayList<>();
        List<Integer> velidadePolicia = new ArrayList<>();


        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            distanciaEntreLadraoEPolicia.add(random.nextInt(100) + 1);
            velidadeLadrao.add(random.nextInt(100) + 1);
            velidadePolicia.add(random.nextInt(100) + 1);
        });

        quantidadeDeThreads.forEach(qtdThreads -> {
            switch (qtdThreads) {
                case 0 -> processamentoSequencial(distanciaEntreLadraoEPolicia, velidadeLadrao, velidadePolicia);
                case 2, 4, 8, 16 -> processamentoPorThreads(distanciaEntreLadraoEPolicia, velidadeLadrao, velidadePolicia, qtdThreads);
            }
        });

        Utils.gerarGráfico(dataset, "speedup_chart_mathmatics_1247.jpg");
    }

    public static void processamentoSequencial(List<Integer> distanciaEntreLadraoEPolicia, List<Integer> velidadeLadrao, List<Integer> velidadePolicia) {
        Instant tempoInicial = Instant.now();
        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            double hip = Math.sqrt(distanciaEntreLadraoEPolicia.get(i) * distanciaEntreLadraoEPolicia.get(i) + 144);
            boolean consegueAlcancar = hip / (double) velidadePolicia.get(i)  <= 12.0 / (double) velidadeLadrao.get(i);
        });
        Instant tempoFinal = Instant.now();
        Duration duracao = Duration.between(tempoInicial, tempoFinal).abs();
        dataset.addValue(duracao.toMillis(), "Tempo", "0");
    }

    public static void processamentoPorThreads(List<Integer> distanciaEntreLadraoEPolicia, List<Integer> velidadeLadrao, List<Integer> velidadePolicia, Integer quantidadeDeThreads) {
        final int quantidadeDeValorPorThread = TAMANHO_DO_VETOR / quantidadeDeThreads;
        Instant tempoInicial = Instant.now();
        IntStream.range(0, quantidadeDeThreads).forEach(i -> {
            int indiceInicial = quantidadeDeValorPorThread * i;
            int  indiceFinal = i == 0 ? quantidadeDeValorPorThread - 1 : (quantidadeDeValorPorThread * (i + 1) );

            Thread thread = gerarThread(distanciaEntreLadraoEPolicia, velidadeLadrao, velidadePolicia, indiceInicial, indiceFinal);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Instant tempoFinal = Instant.now();

        Duration duracao = Duration.between(tempoInicial, tempoFinal).abs();
        dataset.addValue(duracao.toMillis(), "Tempo", quantidadeDeThreads);
    }


    public static Thread gerarThread(final List<Integer> distanciaEntreLadraoEPolicia, final List<Integer> velidadeLadrao, final List<Integer> velidadePolicia, int indiceInicial, int indiceFinal) {
        return new Thread(() -> {
            IntStream.range(indiceInicial, indiceFinal).forEach(i -> {
                double hip = Math.sqrt(distanciaEntreLadraoEPolicia.get(i) * distanciaEntreLadraoEPolicia.get(i) + 144);
                boolean consegueAlcancar = hip / (double) velidadePolicia.get(i)  <= 12.0 / (double) velidadeLadrao.get(i);
            });
        });
    }
}
