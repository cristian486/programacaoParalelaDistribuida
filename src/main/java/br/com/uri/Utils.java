package br.com.uri;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {

    public final static int TAMANHO_DO_VETOR = 128;
    public final static List<Integer> quantidadeDeThreads = List.of(0, 2, 4, 8, 16);

    public static void gerarGráfico(DefaultCategoryDataset dataset, String nomeDoArquivo) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Speedup vs. Número de Threads",
                "Número de Threads",
                "Tempo de Execução (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        try {
            ChartUtilities.saveChartAsJPEG(new File(nomeDoArquivo), chart, 500, 300);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o gráfico: " + e.getMessage());
        }
    }
}