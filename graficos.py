import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv("c:/Users/mateu/OneDrive/Documentos/PUC/Resolução de Problemas Estruturados em Computação/Recuperação/Ra4/results.csv")

for metric in ["Time(ns)", "Swaps", "Iterations"]:
    plt.figure(figsize=(10, 6))
    for algorithm in data["Algorithm"].unique():
        subset = data[data["Algorithm"] == algorithm]
        plt.plot(subset["Dataset"], subset[metric], label=algorithm)

    plt.title(f"Comparison of {metric}")
    plt.xlabel("Dataset")
    plt.ylabel(metric)
    plt.legend()
    plt.grid(True)
    plt.show()
