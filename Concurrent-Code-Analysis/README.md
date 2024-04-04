# Concurrent Code Analysis
## Overview
>For the task I chose the [Conway's Game of Life Parallel implementation](https://github.com/harisboiii/ConwayGameOfLifeOnThreads?tab=readme-ov-file).
>This project was chosen to achieve a better visualization for a data race condition, since synchronizing the threads execution is would be vital for following the game's rules.


Parallel implementations were carried out in a shared memory model of Game Of Life. The code is written in C language and `Posix Threads` (`Pthreads`) were used.

## Source code analysis
>When checking the just-compiled program with `valgrind --tool=helgrind ./cgol`  (`ConwayGameOfLife`) command, `Helgrind` tool did not show any parallelism related issues.

- The game board is divided into areas depending on the number of the threads passed as an argument and default to 1
- Processing the game board using multiple threads is implemented using `barriers` provided by `pthread` library used as a synchronization mechanism
- `Barrier` waits for all the threads to finish processing their area of the board during the current round of the game
- This ensures board to be processed by different threads correctly without breaking the game's rules
## Creating the data race condition
>The only way to create a data race in this implementation is to somehow break the synchronization mechanism

- The synchronization mechanism used in the program is `POSIX threads`'s `barrier`
- If the following conditions are fulfilled, the data race condition would be ensured:
	- `barrier` does not wait for all the threads to finish their execution
	- threads do not modify the game's board immediately and thus the other threads do not get access to the latest data

## Implementing the data race condition
- `Barrier` mechanism was removed from the source code
- During each thread's execution the `usleep` function is invoked and makes the thread sleep for `random_sleep` time, where `random_sleep` is a value in [0;100] ms

## Results
>Creating the data race resulted in game's rules being violated.
>`Helgrind` successfully managed to detect the exact pieces of code (function) to be responsible for the data race.
