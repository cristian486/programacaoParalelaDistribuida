package br.com.uri;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static br.com.uri.Utils.TAMANHO_DO_VETOR;
import static br.com.uri.Utils.quantidadeDeThreads;

public class AtividadeBegginer2234 {


    private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public static void main(String[] args) {

        Random random = new Random();
        List<Integer> quantidadeComida = new ArrayList<>();
        List<Integer> quantidadesDeParticipantes = new ArrayList<>();


        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            quantidadesDeParticipantes.add(random.nextInt(1000) + 1);
            quantidadeComida.add(random.nextInt(1000) + 1);
        });

        quantidadeDeThreads.forEach(qtdThreads -> {
            switch (qtdThreads) {
                case 0 -> processamentoSequencial(quantidadeComida, quantidadesDeParticipantes);
                case 2, 4, 8, 16 -> processamentoPorThreads(quantidadeComida, quantidadesDeParticipantes, qtdThreads);
            }
        });

        Utils.gerarGráfico(dataset, "speedup_chart_begginer_2234.jpg");
    }

    public static void processamentoSequencial(List<Integer> quantidadeComida, List<Integer> quantidadesDeParticipantes) {
        Instant tempoInicial = Instant.now();
        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            double result = (double) quantidadeComida.get(i) /(double) quantidadesDeParticipantes.get(i);
        });
        Instant tempoFinal = Instant.now();
        Duration duracao = Duration.between(tempoInicial, tempoFinal).abs();
        dataset.addValue(duracao.toMillis(), "Tempo", "0");
    }

    public static void processamentoPorThreads(List<Integer> quantidadeComida, List<Integer> quantidadesDeParticipantes, Integer quantidadeDeThreads) {
        final int quantidadeDeValorPorThread = TAMANHO_DO_VETOR / quantidadeDeThreads;
        Instant tempoInicial = Instant.now();
        IntStream.range(0, quantidadeDeThreads).forEach(i -> {
            int indiceInicial = quantidadeDeValorPorThread * i;
            int  indiceFinal = i == 0 ? quantidadeDeValorPorThread - 1 : (quantidadeDeValorPorThread * (i + 1) );

            Thread thread = gerarThread(quantidadeComida, quantidadesDeParticipantes, indiceInicial, indiceFinal);
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


    public static Thread gerarThread(final List<Integer> quantidadeComida, final List<Integer> quantidadesDeParticipantes, int indiceInicial, int indiceFinal) {
        return new Thread(() -> {
            IntStream.range(indiceInicial, indiceFinal).forEach(i -> {
                double result = (double) quantidadeComida.get(i) /(double) quantidadesDeParticipantes.get(i);
            });
        });
    }
}
