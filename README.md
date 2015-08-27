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

# How to run

```
$ mvn clean install
$ $JAVA_HOME/bin/java -jar target/benchmarks.jar -gc true
```

# Becnhmark results

## Hardware

My laptop has CPU Intel Core i7-3612QM CPU @ 2.10GHz (4 physical cores x 2 hyperthreading)

## Java

```
$ java -version
java version "1.8.0_51"
Java(TM) SE Runtime Environment (build 1.8.0_51-b16)
Java HotSpot(TM) 64-Bit Server VM (build 25.51-b03, mixed mode)
```

## Thread count: 1

```
Benchmark                  (count)  (distance)  Mode  Cnt   Score    Error  Units
HammL3Benchmark.contains  50000000           0  avgt    3  ≈ 10⁻⁴           ms/op
HammL3Benchmark.contains  50000000           1  avgt    3   0.220 ±  0.118  ms/op
HammL3Benchmark.contains  50000000           2  avgt    3   0.430 ±  0.077  ms/op
HammL3Benchmark.contains  50000000           4  avgt    3   0.867 ±  0.076  ms/op
HammL3Benchmark.contains  50000000           8  avgt    3   1.614 ±  0.632  ms/op
HammL3Benchmark.contains  50000000          16  avgt    3   1.835 ±  0.109  ms/op
HammL3Benchmark.count     50000000           0  avgt    3  ≈ 10⁻⁴           ms/op
HammL3Benchmark.count     50000000           1  avgt    3   0.211 ±  0.020  ms/op
HammL3Benchmark.count     50000000           2  avgt    3   0.426 ±  0.043  ms/op
HammL3Benchmark.count     50000000           4  avgt    3   1.200 ±  4.458  ms/op
HammL3Benchmark.count     50000000           8  avgt    3   1.764 ±  4.256  ms/op
HammL3Benchmark.count     50000000          16  avgt    3   1.860 ±  0.077  ms/op
```

## Thread count: 8

```
Benchmark                  (count)  (distance)  Mode  Cnt   Score    Error  Units
HammL3Benchmark.contains  50000000           0  avgt    3  ≈ 10⁻⁴           ms/op
HammL3Benchmark.contains  50000000           1  avgt    3   0.579 ±  0.163  ms/op
HammL3Benchmark.contains  50000000           2  avgt    3   1.132 ±  0.160  ms/op
HammL3Benchmark.contains  50000000           4  avgt    3   2.216 ±  4.122  ms/op
HammL3Benchmark.contains  50000000           8  avgt    3   5.273 ±  2.169  ms/op
HammL3Benchmark.contains  50000000          16  avgt    3   7.642 ±  6.781  ms/op
HammL3Benchmark.count     50000000           0  avgt    3  ≈ 10⁻⁴           ms/op
HammL3Benchmark.count     50000000           1  avgt    3   0.504 ±  0.099  ms/op
HammL3Benchmark.count     50000000           2  avgt    3   1.035 ±  1.118  ms/op
HammL3Benchmark.count     50000000           4  avgt    3   3.680 ± 11.782  ms/op
HammL3Benchmark.count     50000000           8  avgt    3   4.227 ±  8.633  ms/op
HammL3Benchmark.count     50000000          16  avgt    3   8.541 ± 17.771  ms/op
```