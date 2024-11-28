package Recuperação.Ra4;

import java.io.FileWriter;
import java.io.IOException;

public class SortingComparison {

    public static void main(String[] args) throws IOException {
        // Inicializando datasets como vetores
        Vetor dataset1 = new Vetor(50);
        Vetor dataset2 = new Vetor(50);
        Vetor dataset3 = new Vetor(50);

        for (int i = 0; i < 50; i++) {
            dataset1.add(i + 1);
            dataset1.add(100 - i);
            dataset2.add(100 - i);
            dataset3.add(50 - i);
        }

        Vetor[] datasets = {dataset1, dataset2, dataset3};
        String[] algorithms = {"Merge Sort", "Radix Sort", "Quick Sort"};

        FileWriter writer = new FileWriter("results.csv");
        writer.append("Algorithm,Dataset,Time(ns),Swaps,Iterations\n");

        for (int datasetIndex = 0; datasetIndex < datasets.length; datasetIndex++) {
            for (int algorithmIndex = 0; algorithmIndex < algorithms.length; algorithmIndex++) {
                // Criar uma cópia do vetor
                Vetor data = datasets[datasetIndex].clone();

                long startTime = System.nanoTime();

                Metrics metrics;
                if (algorithmIndex == 0) {
                    metrics = mergeSort(data);
                } else if (algorithmIndex == 1) {
                    metrics = radixSort(data);
                } else { // Quick Sort
                    metrics = quickSort(data);
                }

                long endTime = System.nanoTime();
                long elapsedTime = endTime - startTime;

                // Exibir resultados no console
                System.out.println(algorithms[algorithmIndex] + " - Dataset " + (datasetIndex + 1) +
                        ": Time = " + elapsedTime + "ns, Swaps = " + metrics.swaps +
                        ", Iterations = " + metrics.iterations);

                // Salvar resultados no arquivo CSV
                writer.append(algorithms[algorithmIndex] + "," + (datasetIndex + 1) + "," +
                        elapsedTime + "," + metrics.swaps + "," + metrics.iterations + "\n");
            }
        }

        writer.flush();
        writer.close();
    }

    static class Metrics {
        int swaps;
        int iterations;

        Metrics(int swaps, int iterations) {
            this.swaps = swaps;
            this.iterations = iterations;
        }
    }

    static class Vetor {
        private int[] data;
        private int size;

        public Vetor(int capacity) {
            data = new int[capacity];
            size = 0;
        }

        public void add(int value) {
            if (size < data.length) {
                data[size++] = value;
            }
        }

        public int get(int index) {
            if (index >= 0 && index < size) {
                return data[index];
            }
            return -1; // Valor inválido para representar erro
        }

        public void set(int index, int value) {
            if (index >= 0 && index < size) {
                data[index] = value;
            }
        }

        public int size() {
            return size;
        }

        public Vetor clone() {
            Vetor copy = new Vetor(data.length);
            for (int i = 0; i < size; i++) {
                copy.add(data[i]);
            }
            return copy;
        }
    }

    // Merge Sort adaptado para Vetor
    public static Metrics mergeSort(Vetor arr) {
        int n = arr.size();
        Vetor temp = new Vetor(n);
        for (int i = 0; i < n; i++) {
            temp.add(0);
        }
        int iterations = 0, swaps = 0;

        for (int size = 1; size < n; size *= 2) {
            for (int start = 0; start < n; start += 2 * size) {
                int mid = Math.min(start + size, n);
                int end = Math.min(start + 2 * size, n);

                int left = start, right = mid, k = start;

                while (left < mid && right < end) {
                    iterations++;
                    if (arr.get(left) <= arr.get(right)) {
                        temp.set(k++, arr.get(left++));
                    } else {
                        temp.set(k++, arr.get(right++));
                        swaps++;
                    }
                }

                while (left < mid) {
                    iterations++;
                    temp.set(k++, arr.get(left++));
                }

                while (right < end) {
                    iterations++;
                    temp.set(k++, arr.get(right++));
                }

                for (int i = start; i < end; i++) {
                    arr.set(i, temp.get(i));
                }
            }
        }

        return new Metrics(swaps, iterations);
    }

    // Radix Sort adaptado para Vetor
    public static Metrics radixSort(Vetor arr) {
        int max = findMax(arr);
        int exp = 1;
        int swaps = 0, iterations = 0;
        int n = arr.size();
        Vetor output = new Vetor(n);

        for (int i = 0; i < n; i++) {
            output.add(0);
        }

        while (max / exp > 0) {
            Vetor count = new Vetor(10);
            for (int i = 0; i < 10; i++) {
                count.add(0);
            }

            for (int i = 0; i < n; i++) {
                int digit = (arr.get(i) / exp) % 10;
                count.set(digit, count.get(digit) + 1);
            }

            for (int i = 1; i < 10; i++) {
                count.set(i, count.get(i) + count.get(i - 1));
            }

            for (int i = n - 1; i >= 0; i--) {
                int digit = (arr.get(i) / exp) % 10;
                output.set(count.get(digit) - 1, arr.get(i));
                count.set(digit, count.get(digit) - 1);
                swaps++;
            }

            for (int i = 0; i < n; i++) {
                arr.set(i, output.get(i));
                iterations++;
            }

            exp *= 10;
        }

        return new Metrics(swaps, iterations);
    }

    // Quick Sort adaptado para Vetor
    public static Metrics quickSort(Vetor arr) {
        int swaps = 0, iterations = 0;
        Vetor stack = new Vetor(arr.size());
        int top = -1;

        stack.add(0);
        stack.add(arr.size() - 1);
        top += 2;

        while (top > 0) {
            int high = stack.get(--top);
            int low = stack.get(--top);

            int p = partition(arr, low, high);
            swaps += p;

            if (p - 1 > low) {
                stack.add(low);
                stack.add(p - 1);
                top += 2;
            }
            if (p + 1 < high) {
                stack.add(p + 1);
                stack.add(high);
                top += 2;
            }
            iterations++;
        }

        return new Metrics(swaps, iterations);
    }

    public static int partition(Vetor arr, int low, int high) {
        int pivot = arr.get(high);
        int i = low - 1;
        int swaps = 0;

        for (int j = low; j < high; j++) {
            if (arr.get(j) <= pivot) {
                i++;
                int temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
                swaps++;
            }
        }

        int temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);
        swaps++;
        return i + 1;
    }

    public static int findMax(Vetor arr) {
        int max = arr.get(0);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) > max) {
                max = arr.get(i);
            }
        }
        return max;
    }
}
