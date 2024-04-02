# Concurrent stacks 
Comparing Treiber stack and Elimination Backoff Stack implementations.

## Experiment

The following experiment is based on the article ["A Scalable Lock-free Stack Algorithm"](https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf)

- Each stack is to perform push and pop operations within a time limit and using a certain number of threads
  - _time limits chosen for the experiment are: 1000, 2000, 4000, 8000 ms_
  - _numbers of threads chosen for the experiment are: 1, 2, 4, 8, 14, 32_
- Each thread performs stack operations and then waits for a period of time, whose length is chosen uniformly at random from the range: [0 ... workload]
  - $workload = 100$ _throughout the experiment_
  - $workload$ _is a simulation of the work that can be done by threads between stack operations in a real application_
- The experiment measures the number of operation performed by each stack within a time limit using the given number of threads

## Expected results
- The Elimination Backoff Stack is expected to considerably outperform Treiber Stack without optimization

## Actual results
- After conducting the experiment the expected results that are based on the [article](https://people.csail.mit.edu/shanir/publications/Lock_Free.pdf) could not be achieved
- The actual results show that the general tendency of both of the implementations are the same
- The performance of Elimination Backoff Stack indeed exceeds the performance of Treiber Stack without optimization, however the difference is not considerable
- The experiment results as well as their graphics are given in the following [table](https://docs.google.com/spreadsheets/d/1T_I1XgcCqwO5GQs7B-5uJxp3sBo4tt83j14Xztp5qxU/edit?usp=sharing)
