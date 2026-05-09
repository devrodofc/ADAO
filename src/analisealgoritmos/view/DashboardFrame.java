package analisealgoritmos.view;

import analisealgoritmos.core.ExecutionResult;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DashboardFrame extends JFrame {

    // Paleta de cores moderna para as séries do gráfico
    private static final List<Color> PALETTE = List.of(
            new Color(52, 152, 219),   // Azul
            new Color(231, 76, 60),    // Vermelho
            new Color(46, 204, 113),   // Verde
            new Color(155, 89, 182),   // Roxo
            new Color(241, 196, 15),   // Amarelo
            new Color(26, 188, 156),   // Turquesa
            new Color(230, 126, 34),   // Laranja
            new Color(149, 165, 166)   // Cinza
    );

    private final ChartPanel panel = new ChartPanel();
    // Armazena as séries de dados: Nome da Série -> (Tamanho do Array -> Acumulador de Média)
    private final Map<String, TreeMap<Integer, AverageAccumulator>> series = new LinkedHashMap<>();

    public DashboardFrame(String title) {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 600));
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Adiciona um resultado ao gráfico e recalcula a média se houver amostras repetidas.
     */
    public void addResult(ExecutionResult result) {
        String seriesName = buildSeriesName(result);

        // Converte nanossegundos para milissegundos para visualização
        double timeMillis = result.executionTimeNanos() / 1_000_000.0;

        TreeMap<Integer, AverageAccumulator> points = series.computeIfAbsent(seriesName, ignored -> new TreeMap<>());
        points.computeIfAbsent(result.arraySize(), ignored -> new AverageAccumulator()).add(timeMillis);

        panel.repaint();
    }

    /**
     * Constrói o nome da linha no gráfico baseado na natureza da execução.
     */
    private static String buildSeriesName(ExecutionResult result) {
        String dist = result.dataDistribution().name();
        if (!result.isParallel()) {
            return result.algorithmName() + " [" + dist + " - Serial]";
        }
        return result.algorithmName() + " [" + dist + " - " + result.threadCount() + "T]";
    }

    private final class ChartPanel extends JPanel {
        private static final int LEFT_MARGIN = 70;
        private static final int RIGHT_MARGIN = 300; // Margem larga para acomodar a legenda
        private static final int TOP_MARGIN = 40;
        private static final int BOTTOM_MARGIN = 70;

        // --- CONSTANTES DE ESTILO DE LINHA ---
        private static final Stroke SOLID_STROKE = new BasicStroke(2f);
        // Tracejado longo para o ForkJoin
        private static final Stroke DASHED_STROKE = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10f, 5f}, 0.0f);
        // Pontilhado curto para o Odd-Even
        private static final Stroke DOTTED_STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{3f, 4f}, 0.0f);

        // Define a linha correta baseado no algoritmo
        private Stroke getStrokeForSeries(String seriesName) {
            if (seriesName.contains("ForkJoin")) {
                return DASHED_STROKE;
            } else if (seriesName.contains("Odd-Even")) {
                return DOTTED_STROKE;
            } else {
                return SOLID_STROKE; // Padrão para Serial
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fundo Branco
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int chartWidth = getWidth() - LEFT_MARGIN - RIGHT_MARGIN;
            int chartHeight = getHeight() - TOP_MARGIN - BOTTOM_MARGIN;

            if (chartWidth <= 0 || chartHeight <= 0) {
                g2.dispose();
                return;
            }

            if (series.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
                g2.drawString("Aguardando resultados...", LEFT_MARGIN, TOP_MARGIN + 20);
                g2.dispose();
                return;
            }

            // Descobre o menor e maior eixo X (Tamanho do Array)
            int minSize = series.values().stream()
                    .flatMap(map -> map.keySet().stream())
                    .min(Comparator.naturalOrder())
                    .orElse(0);
            int maxSize = series.values().stream()
                    .flatMap(map -> map.keySet().stream())
                    .max(Comparator.naturalOrder())
                    .orElse(1);
            if (minSize == maxSize) {
                maxSize++;
            }

            // Descobre o maior tempo (Eixo Y) para dimensionar a altura
            double maxY = series.values().stream()
                    .flatMap(map -> map.values().stream())
                    .mapToDouble(AverageAccumulator::average)
                    .max()
                    .orElse(1.0);
            if (maxY <= 0.0) {
                maxY = 1.0;
            }

            int axisX = LEFT_MARGIN;
            int axisY = TOP_MARGIN + chartHeight;

            // Desenha Eixos Principais
            g2.setColor(Color.BLACK);
            g2.drawLine(axisX, TOP_MARGIN, axisX, axisY);
            g2.drawLine(axisX, axisY, axisX + chartWidth, axisY);

            drawGridAndLabels(g2, axisX, axisY, chartWidth, chartHeight, maxY, minSize, maxSize);
            drawSeries(g2, axisX, axisY, chartWidth, chartHeight, maxY, minSize, maxSize);
            drawLegend(g2, axisX + chartWidth + 20, TOP_MARGIN + 20);

            g2.dispose();
        }

        private void drawGridAndLabels(Graphics2D g2, int axisX, int axisY, int chartWidth, int chartHeight, double maxY, int minSize, int maxSize) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(new Color(220, 220, 220));

            // Linhas horizontais (Grid Y)
            for (int i = 1; i <= 5; i++) {
                int y = axisY - (i * chartHeight / 5);
                g2.drawLine(axisX, y, axisX + chartWidth, y);
                g2.setColor(Color.DARK_GRAY);
                double label = (maxY * i) / 5.0;
                g2.drawString(String.format("%.1f ms", label), 8, y + 4);
                g2.setColor(new Color(220, 220, 220));
            }

            // Títulos dos eixos
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Tamanho do Array de Entrada", axisX + (chartWidth / 2) - 80, axisY + 45);
            g2.drawString("Tempo Médio (ms)", 10, TOP_MARGIN - 12);

            // Marcações do eixo X
            int ticks = 6;
            for (int i = 0; i <= ticks; i++) {
                int x = axisX + (i * chartWidth / ticks);
                int size = minSize + ((maxSize - minSize) * i / ticks);
                g2.drawLine(x, axisY, x, axisY + 5);
                g2.drawString(Integer.toString(size), x - 16, axisY + 20);
            }
        }

        private void drawSeries(Graphics2D g2, int axisX, int axisY, int chartWidth, int chartHeight, double maxY, int minSize, int maxSize) {
            int colorIndex = 0;

            for (Map.Entry<String, TreeMap<Integer, AverageAccumulator>> entry : series.entrySet()) {
                String seriesName = entry.getKey();
                TreeMap<Integer, AverageAccumulator> points = entry.getValue();
                if (points.isEmpty()) continue;

                Color color = PALETTE.get(colorIndex % PALETTE.size());
                colorIndex++;
                g2.setColor(color);

                // Aplica o estilo de linha dinâmico (Sólido, Tracejado ou Pontilhado)
                g2.setStroke(getStrokeForSeries(seriesName));

                List<Point> transformed = new ArrayList<>();
                for (Map.Entry<Integer, AverageAccumulator> pointEntry : points.entrySet()) {
                    int x = axisX + scale(pointEntry.getKey(), minSize, maxSize, chartWidth);
                    int y = axisY - scale(pointEntry.getValue().average(), 0.0, maxY, chartHeight);
                    transformed.add(new Point(x, y));
                }

                // Desenha a linha conectando os pontos
                for (int i = 1; i < transformed.size(); i++) {
                    Point previous = transformed.get(i - 1);
                    Point current = transformed.get(i);
                    g2.drawLine(previous.x(), previous.y(), current.x(), current.y());
                }

                // Desenha os pontos (nós) na linha (voltando o traço para sólido)
                g2.setStroke(SOLID_STROKE);
                for (Point point : transformed) {
                    g2.fillOval(point.x() - 4, point.y() - 4, 8, 8);
                }
            }
        }

        private void drawLegend(Graphics2D g2, int startX, int startY) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            int y = startY;
            int colorIndex = 0;

            for (String seriesName : series.keySet()) {
                Color color = PALETTE.get(colorIndex % PALETTE.size());
                colorIndex++;

                // Desenha uma amostra da linha na legenda
                g2.setColor(color);
                g2.setStroke(getStrokeForSeries(seriesName));
                g2.drawLine(startX, y - 4, startX + 20, y - 4);

                // Desenha uma bolinha no meio da linha da legenda
                g2.setStroke(SOLID_STROKE);
                g2.fillOval(startX + 8, y - 7, 6, 6);

                // Desenha o texto
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(seriesName, startX + 28, y + 1);

                y += 20; // Espaçamento vertical entre os itens da legenda
            }
        }

        private int scale(int value, int minValue, int maxValue, int availablePixels) {
            if (maxValue <= minValue) return 0;
            return (int) Math.round(((value - minValue) / (double) (maxValue - minValue)) * availablePixels);
        }

        private int scale(double value, double minValue, double maxValue, int availablePixels) {
            if (maxValue <= minValue) return 0;
            return (int) Math.round(((value - minValue) / (maxValue - minValue)) * availablePixels);
        }
    }

    private record Point(int x, int y) {}

    /**
     * Classe utilitária para acumular valores e calcular a média.
     * Essencial para tratar as amostras exigidas no edital.
     */
    private static final class AverageAccumulator {
        private double sum;
        private int count;

        private void add(double value) {
            sum += value;
            count++;
        }

        private double average() {
            return count == 0 ? 0.0 : sum / count;
        }
    }
}