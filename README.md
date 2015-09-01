# Description

This algorithm implements searching of 64-bit hash values by any Hamming distance. Also supports thread-safe runtime
appending and deleting. Uses off-heap storage.

The algorithm splits 64-bits long value into four 16-bit sections. We count the number of bits in
each section so we get 4 counters in range [0..16]. So any value could be referred to a bag with
septendecimal bag-id in range `0000@x17`..`GGGG@x17` (numbers are in 17-base format). The total number
of bags is 17^4=83521

When we search for a value with hamming distance we determine the according range of bag-ids and
sequentially scan value arrays in all these bags for proper values.

Some optimitizations are implemented to skip bags with incompatible bit structure.

Hash values and payload values are stored in off-heap memory via sun.misc.Unsafe

The more distance the less usefull this algorithms is

# How to run

```
$ mvn clean install
$ ${JAVA_HOME}/bin/java -jar target/benchmarks.jar -gc true -si true HammL3Benchmark
```

# Becnhmark results x

## Hardware

My laptop has CPU Intel Core i7-3612QM CPU @ 2.10GHz (4 physical cores x 2 hyperthreading)

## Java

```
$ java -version
java version "1.8.0_51"
Java(TM) SE Runtime Environment (build 1.8.0_51-b16)
Java HotSpot(TM) 64-Bit Server VM (build 25.51-b03, mixed mode)
```

## HammL3Benchmark: 1 thread

```
ms/op = milliseconds per 1000 micro-calls

Benchmark              (capacity)  (distance)  Mode  Cnt      Score      Error  Units
HammL3Benchmark.count    10000000           0    ss    5      8.692 ±    6.194  ms/op
HammL3Benchmark.count    10000000           2    ss    5    190.481 ±   15.441  ms/op
HammL3Benchmark.count    10000000           4    ss    5   1132.757 ±   41.768  ms/op
HammL3Benchmark.count    10000000           8    ss    5   6452.011 ±  214.456  ms/op
HammL3Benchmark.count    10000000          16    ss    5  16215.025 ± 3490.957  ms/op
```

## HammL3Benchmark: 8 threads

```
ms/op = milliseconds per 1000 micro-calls

Benchmark              (capacity)  (distance)  Mode  Cnt      Score      Error  Units
HammL3Benchmark.count    10000000           0    ss    5     12.981 ±    6.749  ms/op
HammL3Benchmark.count    10000000           2    ss    5    550.944 ±   20.432  ms/op
HammL3Benchmark.count    10000000           4    ss    5   3568.030 ±  121.268  ms/op
HammL3Benchmark.count    10000000           8    ss    5  20691.266 ±  246.396  ms/op
HammL3Benchmark.count    10000000          16    ss    5  44533.061 ± 1037.946  ms/op
```

## PlainBenchmark: 1 thread

```
ms/op = milliseconds per 1000 micro-calls

Benchmark             (capacity)  (distance)  Mode  Cnt      Score      Error  Units
PlainBenchmark.count    10000000           1    ss    5  12690.818 ± 5523.519  ms/op
PlainBenchmark.count    10000000           8    ss    5   9828.689 ± 2968.578  ms/op
```

## PlainBenchmark: 8 threads

```
ms/op = milliseconds per 1000 micro-calls

Benchmark             (capacity)  (distance)  Mode  Cnt      Score       Error  Units
PlainBenchmark.count    10000000           1    ss    5  37983.394 ± 19062.100  ms/op
PlainBenchmark.count    10000000           8    ss    5  34428.467 ± 13859.389  ms/op
```