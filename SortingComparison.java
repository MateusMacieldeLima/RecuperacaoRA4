package Recuperação.Ra4;
import java.io.FileWriter;
import java.io.IOException;

public class SortingComparison {

    public static void main(String[] args) throws IOException {
        int[][] datasets = {
            {1, 100, 2, 99, 3, 98, 4, 97, 5, 96, 6, 95, 7, 94, 8, 93, 9, 92, 10, 91, 11, 90, 12, 89, 13, 88, 14, 87, 15, 86, 16, 85, 17, 84, 18, 83, 19, 82, 20, 81, 21, 80, 22, 79, 23, 78, 24, 77, 25, 76},
            {1, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52},
            {50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}
        };

        String[] algorithms = {"Merge Sort", "Radix Sort", "Quick Sort"};

        FileWriter writer = new FileWriter("results.csv");
        writer.append("Algorithm,Dataset,Time(ns),Swaps,Iterations\n");

        for (int datasetIndex = 0; datasetIndex < datasets.length; datasetIndex++) {
            for (int algorithmIndex = 0; algorithmIndex < algorithms.length; algorithmIndex++) {
                // Fazer uma cópia do dataset para cada algoritmo
                int[] data = copyArray(datasets[datasetIndex]);

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

    // Classe simples para armazenar métricas
    static class Metrics {
        int swaps;
        int iterations;

        Metrics(int swaps, int iterations) {
            this.swaps = swaps;
            this.iterations = iterations;
        }
    }

    // Função auxiliar para copiar um array
    public static int[] copyArray(int[] original) {
        int[] copy = new int[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];
        }
        return copy;
    }

    // Merge Sort iterativo
    public static Metrics mergeSort(int[] arr) {
        int n = arr.length;
        int[] temp = new int[n];
        int iterations = 0, swaps = 0;

        for (int size = 1; size < n; size *= 2) {
            for (int start = 0; start < n; start += 2 * size) {
                int mid = start + size;
                if (mid > n) mid = n;

                int end = start + 2 * size;
                if (end > n) end = n;

                int left = start, right = mid, k = start;

                while (left < mid && right < end) {
                    iterations++;
                    if (arr[left] <= arr[right]) {
                        temp[k++] = arr[left++];
                    } else {
                        temp[k++] = arr[right++];
                        swaps++;
                    }
                }

                while (left < mid) {
                    iterations++;
                    temp[k++] = arr[left++];
                }

                while (right < end) {
                    iterations++;
                    temp[k++] = arr[right++];
                }

                for (int i = start; i < end; i++) {
                    arr[i] = temp[i];
                }
            }
        }

        return new Metrics(swaps, iterations);
    }

    // Radix Sort
    public static Metrics radixSort(int[] arr) {
        int max = findMax(arr);
        int exp = 1;
        int swaps = 0, iterations = 0;

        while (max / exp > 0) {
            int[] count = new int[10];
            int[] output = new int[arr.length];

            for (int i = 0; i < arr.length; i++) {
                count[(arr[i] / exp) % 10]++;
            }

            for (int i = 1; i < 10; i++) {
                count[i] += count[i - 1];
            }

            for (int i = arr.length - 1; i >= 0; i--) {
                output[count[(arr[i] / exp) % 10] - 1] = arr[i];
                count[(arr[i] / exp) % 10]--;
                swaps++;
            }

            for (int i = 0; i < arr.length; i++) {
                arr[i] = output[i];
                iterations++;
            }

            exp *= 10;
        }

        return new Metrics(swaps, iterations);
    }

    // Quick Sort iterativo
    public static Metrics quickSort(int[] arr) {
        int swaps = 0, iterations = 0;
        int[] stack = new int[arr.length];
        int top = -1;

        stack[++top] = 0;
        stack[++top] = arr.length - 1;

        while (top >= 0) {
            int high = stack[top--];
            int low = stack[top--];

            int p = partition(arr, low, high);
            swaps += p;

            if (p - 1 > low) {
                stack[++top] = low;
                stack[++top] = p - 1;
            }
            if (p + 1 < high) {
                stack[++top] = p + 1;
                stack[++top] = high;
            }
            iterations++;
        }

        return new Metrics(swaps, iterations);
    }

    public static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        int swaps = 0;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                swaps++;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        swaps++;
        return i + 1;
    }

    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
}
