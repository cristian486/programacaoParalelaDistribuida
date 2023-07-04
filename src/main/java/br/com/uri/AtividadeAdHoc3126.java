package br.com.uri;

import org.jfree.data.category.DefaultCategoryDataset;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static br.com.uri.Utils.TAMANHO_DO_VETOR;
import static br.com.uri.Utils.quantidadeDeThreads;

public class AtividadeAdHoc3126 {

    private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public static void main(String[] args) {
        Random random = new Random();
        List<Integer> participantes = new ArrayList<>();

        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            participantes.add(random.nextInt(2));
        });

        quantidadeDeThreads.forEach(qtdThreads -> {
            switch (qtdThreads) {
                case 0 -> processamentoSequencial(participantes);
                case 2, 4, 8, 16 -> processamentoPorThreads(participantes, qtdThreads);
            }
        });

        Utils.gerarGráfico(dataset, "speedup_chart_adhoc_3126.jpg");
    }

    public static void processamentoSequencial(List<Integer> participantes) {
        Instant tempoInicial = Instant.now();

        AtomicInteger valor = new AtomicInteger(0);
        IntStream.range(0, TAMANHO_DO_VETOR).forEach(i -> {
            if(participantes.get(i) > 0)
                valor.addAndGet(1);
        });

        Instant tempoFinal = Instant.now();

        Duration duracao = Duration.between(tempoInicial, tempoFinal).abs();
        dataset.addValue(duracao.toMillis(), "Tempo", "0");
    }


    public static void processamentoPorThreads(List<Integer> participantes, Integer quantidadeDeThreads) {
        final int quantidadeDeValorPorThread = TAMANHO_DO_VETOR / quantidadeDeThreads;
        Instant tempoInicial = Instant.now();
        AtomicInteger valor = new AtomicInteger(0);
        List<Thread> threads = new ArrayList<>();

        IntStream.range(0, quantidadeDeThreads).forEach(i -> {
            int indiceInicial = quantidadeDeValorPorThread * i;
            int  indiceFinal = i == 0 ? quantidadeDeValorPorThread - 1 : (quantidadeDeValorPorThread * (i + 1) );
            Thread thread = gerarThread(participantes, indiceInicial, indiceFinal, valor);
            thread.start();
            threads.add(thread);
        });

        threads.forEach(thread -> {
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


    public static Thread gerarThread(final List<Integer> participantes, int indiceInicial, int indiceFinal, AtomicInteger valor) {
        return new Thread(() -> {
            IntStream.range(indiceInicial, indiceFinal).forEach(i -> {
                if(participantes.get(i) > 0)
                    valor.addAndGet(1);
            });
        });
    }
}
