from threading import Thread
from random import randint
import time
import matplotlib.pyplot as plt

def Carrinho():
    for c in range(0, 100):
        a = randint(0, 100)
        b = randint(0, 100)
        soma = a + b
        print(soma)


def speedup(sequential_time, thread_times):
    return [sequential_time / thread_time for thread_time in thread_times]

def main():
    num_threads = [2, 4, 8, 16]
    sequential_time = 0
    thread_times = []

    # Execução sequencial
    start_time = time.time()
    Carrinho()
    sequential_time = time.time() - start_time

    # Execução com diferentes números de threads
    for num_thread in num_threads:
        start_time = time.time()
        threads = []

        for _ in range(num_thread):
            t = Thread(target=Carrinho)
            t.start()
            threads.append(t)

        for t in threads:
            t.join()

        thread_time = time.time() - start_time
        thread_times.append(thread_time)

    # Cálculo do SpeedUp
    speedup_values = speedup(sequential_time, thread_times)

    # Plotagem do gráfico de SpeedUp
    plt.plot(num_threads, speedup_values, 'ro-')
    plt.xlabel('Número de Threads')
    plt.ylabel('SpeedUp')
    plt.title('Gráfico de SpeedUp')
    plt.show()

if __name__ == '__main__':
    main()
