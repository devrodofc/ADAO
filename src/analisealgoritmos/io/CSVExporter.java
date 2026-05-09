package analisealgoritmos.io;

import analisealgoritmos.core.ExecutionResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;

/**
 * Responsável por exportar as métricas de performance para CSV.
 * Ajustado para compatibilidade com Excel em sistemas com Locale PT-BR.
 */
public class CSVExporter {

    // Separador ponto e vírgula para evitar conflitos decimais no Excel brasileiro
    private static final String CSV_SEPARATOR = ";";
    private static final String HEADER = "Algoritmo;Paralelo;TamanhoArray;Distribuicao;Threads;Amostra;TempoNanos;TempoMillis";

    /**
     * Exporta resultados para CSV. Se o arquivo já existir, anexa os dados.
     * * @param results Lista de resultados das execuções.
     * @param fileName Nome do arquivo de saída.
     */
    public void export(List<ExecutionResult> results, String fileName) {
        Path path = Paths.get(fileName);
        boolean fileExists = Files.exists(path);

        // Opções: CREATE (cria se não existe), APPEND (anexa se existe), WRITE (permite escrita)
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            // Escreve o cabeçalho apenas se o arquivo for novo
            if (!fileExists) {
                writer.write(HEADER);
                writer.newLine();
            }

            for (ExecutionResult res : results) {
                // Cálculo de milissegundos a partir dos nanossegundos coletados
                double timeMillis = res.executionTimeNanos() / 1_000_000.0;

                String line = String.join(CSV_SEPARATOR,
                        res.algorithmName(),
                        String.valueOf(res.isParallel()),
                        String.valueOf(res.arraySize()),
                        res.dataDistribution().name(),
                        String.valueOf(res.threadCount()),
                        String.valueOf(res.sampleNumber()),
                        String.valueOf(res.executionTimeNanos()),
                        // Locale.US garante o ponto (.) como separador decimal para evitar que
                        // o valor quebre em múltiplas colunas caso o Excel use ponto e vírgula.
                        String.format(Locale.US, "%.4f", timeMillis)
                );

                writer.write(line);
                writer.newLine();
            }

            System.out.println("Resultados registrados em: " + path.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Falha crítica ao gravar CSV: " + e.getMessage());
        }
    }
}