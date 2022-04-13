# COP-4520-Spring-2022-Programming-Assignment-3
## Problem 1
All source files are in the `src` folder.<br>
To compile: `javac *.java`<br>
To run: `java Problem1`<br>
Uses a lock free linked list to simulate a chain of presents connected together. The linked list code is taken from the textbook and modified to work for the problem. 

Initially, it was difficult to even get the program to finish in a timely manner because of the way the servant threads were calling add and remove with a random int, but I didn't keep track of what was added or removed. This caused the servants to constantly try to add or remove items that had already been added or removed. Eventually, I realized I could use a shuffled queue to simulate an unordered bag after which the program was able to run in a reasonable amount of time with 500,000 gifts. There is still an element of randomness in whether a servant thread decides to add, remove, or check for a gift in the chain which causes the runtime to vary significantly. Overall, I found the time for my solution to run takes around 1.2 to 5.0 seconds.

## Problem 2
To compile: `javac *.java`<br>
To run: `java Problem2`<br>
Uses a lock free linked list to keep track of sensor readings in an ordered list. The linked list code is taken from the textbook and modified to work for nodes containing doubles. This solution uses the Thread.sleep() method to simulate waiting a minute; its default value is 1 (ms) but this can be changed in the `Sensor` class under the constant `WAIT_TIME`. The runtime is also dictated by the `Sensor` class's `HOURS_TO_RUN` value.

I elected to do the report compilation by picking one of the sensor threads to do all of it. Each sensor still takes readings and adds them to the linked list. Initially the linked list used integers for the keys which caused the readings which are double float values to be unordered in the linked list. 

### Efficiency
I estimate the solution to be sufficiently efficient in terms of adding and removing gifts from the chain. Due to the randomness of servants switching between `add()`, `remove()`, and `contains()` there is a possibility for the chain to grew to an inordinate size such that `add()` and `remove()`'s could take a long time due to list traversal.

### Correctness
Due to ordering of the linked list, the first five nodes should be the lowest readings and the last five nodes should be the highest readings. Using this we can easily get the lows and highs for each hour and ten-minute interval. In general, we expect each sensor to finish adding to each list before the report is generated by the lead sensor, so we have all measurements in the list at report compilation. This is especially true for if we use a sleep timer of sufficient length to give time for each add to complete. 

### Progress Guarantee
The solution is lock-free so there is always work being done even if an `add()` or `remove()` fail for a particular thread fails. For the same reason, there is also no risk of deadlock.
