package Recuperação.Ra4;

import java.io.FileWriter;
import java.io.IOException;

public class SortingComparison {

    public static void main(String[] args) throws IOException {
        Vetor dataset1 = new Vetor(50);
        int[] data1 = {1, 100, 2, 99, 3, 98, 4, 97, 5, 96, 6, 95, 7, 94, 8, 93, 9, 92, 10, 91, 11, 90,
                12, 89, 13, 88, 14, 87, 15, 86, 16, 85, 17, 84, 18, 83, 19, 82, 20, 81, 21, 80,
                22, 79, 23, 78, 24, 77, 25, 76};
        for (int val : data1) {
            dataset1.add(val);
        }

        Vetor dataset2 = new Vetor(50);
        int[] data2 = {1, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82,
                81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62,
                61, 60, 59, 58, 57, 56, 55, 54, 53, 52};
        for (int val : data2) {
            dataset2.add(val);
        }

        Vetor dataset3 = new Vetor(50);
        int[] data3 = {50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31,
                30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11,
                10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        for (int val : data3) {
            dataset3.add(val);
        }

        Vetor datasets = new Vetor(3);
        datasets.add(dataset1);
        datasets.add(dataset2);
        datasets.add(dataset3);

        String[] algorithms = {"Merge Sort", "Radix Sort", "Quick Sort"};

        FileWriter writer = new FileWriter("results.csv");
        writer.append("Algorithm,Dataset,Time(ns),Swaps,Iterations\n");

        for (int datasetIndex = 0; datasetIndex < datasets.size(); datasetIndex++) {
            for (int algorithmIndex = 0; algorithmIndex < algorithms.length; algorithmIndex++) {
                Vetor data = ((Vetor) datasets.get(datasetIndex)).clone();

                long startTime = System.nanoTime();

                Metrics metrics;
                if (algorithmIndex == 0) {
                    metrics = mergeSort(data);
                } else if (algorithmIndex == 1) {
                    metrics = radixSort(data);
                } else {
                    metrics = quickSort(data);
                }

                long endTime = System.nanoTime();
                long elapsedTime = endTime - startTime;

                System.out.println(algorithms[algorithmIndex] + " - Dataset " + (datasetIndex + 1) +
                        ": Time = " + elapsedTime + "ns, Swaps = " + metrics.swaps +
                        ", Iterations = " + metrics.iterations);

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
        private Object[] data;
        private int size;
        private int capacity;

        public Vetor(int capacity) {
            this.capacity = capacity;
            this.data = new Object[capacity];
            this.size = 0;
        }

        public void add(Object value) {
            if (size < capacity) {
                data[size++] = value;
            }
        }

        public Object get(int index) {
            if (index >= 0 && index < size) {
                return data[index];
            }
            return null;
        }

        public void set(int index, Object value) {
            if (index >= 0 && index < size) {
                data[index] = value;
            }
        }

        public int size() {
            return size;
        }

        public int capacity() {
            return capacity;
        }

        public Vetor clone() {
            Vetor copy = new Vetor(capacity);
            for (int i = 0; i < size; i++) {
                copy.add(data[i]);
            }
            return copy;
        }
    }

    public static Metrics mergeSort(Vetor arr) {
        return mergeSortHelper(arr, 0, arr.size() - 1);
    }

    private static Metrics mergeSortHelper(Vetor arr, int start, int end) {
        if (start >= end) {
            return new Metrics(0, 0);
        }

        int mid = (start + end) / 2;

        Metrics leftMetrics = mergeSortHelper(arr, start, mid);
        Metrics rightMetrics = mergeSortHelper(arr, mid + 1, end);

        Metrics mergeMetrics = merge(arr, start, mid, end);

        return new Metrics(
                leftMetrics.swaps + rightMetrics.swaps + mergeMetrics.swaps,
                leftMetrics.iterations + rightMetrics.iterations + mergeMetrics.iterations
        );
    }

    private static Metrics merge(Vetor arr, int start, int mid, int end) {
        Vetor temp = new Vetor(end - start + 1);
        int left = start, right = mid + 1, swaps = 0, iterations = 0;

        while (left <= mid && right <= end) {
            iterations++;
            if ((int) arr.get(left) <= (int) arr.get(right)) {
                temp.add(arr.get(left++));
            } else {
                temp.add(arr.get(right++));
                swaps++;
            }
        }

        while (left <= mid) {
            iterations++;
            temp.add(arr.get(left++));
        }

        while (right <= end) {
            iterations++;
            temp.add(arr.get(right++));
        }

        for (int i = 0; i < temp.size(); i++) {
            arr.set(start + i, temp.get(i));
        }

        return new Metrics(swaps, iterations);
    }

    public static Metrics quickSort(Vetor arr) {
        return quickSortHelper(arr, 0, arr.size() - 1);
    }

    private static Metrics quickSortHelper(Vetor arr, int start, int end) {
        if (start >= end) {
            return new Metrics(0, 0);
        }

        Metrics metrics = new Metrics(0, 0);

        int pivotIndex = partition(arr, start, end, metrics);
        Metrics leftMetrics = quickSortHelper(arr, start, pivotIndex - 1);
        Metrics rightMetrics = quickSortHelper(arr, pivotIndex + 1, end);

        metrics.swaps += leftMetrics.swaps + rightMetrics.swaps;
        metrics.iterations += leftMetrics.iterations + rightMetrics.iterations;

        return metrics;
    }

    private static int partition(Vetor arr, int start, int end, Metrics metrics) {
        int pivot = (int) arr.get(end);
        int i = start - 1;

        for (int j = start; j < end; j++) {
            metrics.iterations++;
            if ((int) arr.get(j) <= pivot) {
                i++;
                swap(arr, i, j, metrics);
            }
        }

        swap(arr, i + 1, end, metrics);
        return i + 1;
    }

    private static void swap(Vetor arr, int i, int j, Metrics metrics) {
        Object temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
        metrics.swaps++;
    }

    public static Metrics radixSort(Vetor arr) {
        return new Metrics(0, 0);
    }
}
