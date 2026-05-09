# AV2 - Analise de desempenho de algoritmos de ordenacao

Projeto em Java para comparar desempenho de algoritmos de ordenacao em versoes **seriais** e **paralelas**, com coleta de metricas em lote e visualizacao grafica.

## O que o projeto faz

- Executa benchmarks com aquecimento da JVM (warmup) e 5 amostras por cenario.
- Compara implementacoes seriais e paralelas de:
  - Bubble Sort
  - Insertion Sort
  - Quick Sort
  - Merge Sort
- Varia os cenarios por:
  - tamanho do array
  - distribuicao dos dados
  - quantidade de threads
- Exporta resultados para CSV.
- Exibe um dashboard Swing com grafico de tempo medio (ms) por serie.

## Cenarios configurados atualmente

Esses valores estao definidos em `src\analisealgoritmos\Main.java`:

- **Tamanhos de entrada:** `1000`, `5000`, `10000`, `25000`, `50000`
- **Distribuicoes:** `RANDOM`, `NEARLY_SORTED`, `REVERSED`
- **Threads (algoritmos paralelos):** `2`, `4` e `numero de nucleos da maquina`
- **Amostras por cenario:** `5` (definido em `BenchmarkRunner`)

## Saidas geradas

- **CSV:** `analise_desempenho_algoritmos.csv`
  - Colunas: `Algoritmo;Paralelo;TamanhoArray;Distribuicao;Threads;Amostra;TempoNanos;TempoMillis`
  - O arquivo e **incremental** (anexa dados se ja existir).
- **Dashboard:** janela Swing `DashboardFrame` com as series e medias de tempo.

## Requisitos

- JDK 17 ou superior
- (Opcional) IntelliJ IDEA para executar pelo projeto `.iml`

## Como executar

### Opcao 1: IntelliJ IDEA

1. Abra o arquivo `av2.iml` no IntelliJ.
2. Execute a classe `analisealgoritmos.Main`.

### Opcao 2: Terminal (Windows)

Compile:

```bat
dir /s /b src\*.java > sources.txt
javac -d out\production\av2 @sources.txt
del sources.txt
```

Execute:

```bat
java -cp out\production\av2 analisealgoritmos.Main
```

## Estrutura principal

```text
src\analisealgoritmos
├── Main.java
├── core
│   ├── Sorter.java
│   └── ExecutionResult.java
├── framework
│   ├── BenchmarkRunner.java
│   └── DataGenerator.java
├── io
│   └── CSVExporter.java
├── view
│   └── DashboardFrame.java
└── algorithms
    ├── sequential
    └── parallel
```

## Observacoes

- Se quiser iniciar uma coleta limpa, apague `analise_desempenho_algoritmos.csv` antes de rodar novamente.
- O total de execucoes pode crescer bastante dependendo da combinacao de cenarios e threads.
