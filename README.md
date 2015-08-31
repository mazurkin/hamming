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
HammL3Benchmark.count    10000000           0    ss    5      8.166 ±    5.018  ms/op
HammL3Benchmark.count    10000000           2    ss    5    178.331 ±    6.518  ms/op
HammL3Benchmark.count    10000000           4    ss    5   1134.726 ±  149.419  ms/op
HammL3Benchmark.count    10000000           8    ss    5   7482.099 ± 2890.950  ms/op
HammL3Benchmark.count    10000000          16    ss    5  18900.717 ± 3525.560  ms/op
```

## HammL3Benchmark: 8 threads

```
ms/op = milliseconds per 1000 micro-calls

Benchmark              (capacity)  (distance)  Mode  Cnt      Score       Error  Units
HammL3Benchmark.count    10000000           0    ss    5     13.525 ±     7.422  ms/op
HammL3Benchmark.count    10000000           2    ss    5    624.531 ±    36.620  ms/op
HammL3Benchmark.count    10000000           4    ss    5   4039.825 ±   349.892  ms/op
HammL3Benchmark.count    10000000           8    ss    5  26622.942 ± 15663.154  ms/op
HammL3Benchmark.count    10000000          16    ss    5  61455.721 ± 16375.911  ms/op
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