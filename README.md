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
$ java -jar target/benchmarks.jar
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
Benchmark                  (count)  (distance)  Mode  Cnt   Score   Error  Units
HammL3Benchmark.contains  50000000           0  avgt    2   ~10⁻⁴          ms/op
HammL3Benchmark.contains  50000000           1  avgt    2   0.225          ms/op
HammL3Benchmark.contains  50000000           2  avgt    2   0.439          ms/op
HammL3Benchmark.contains  50000000           4  avgt    2   0.957          ms/op
HammL3Benchmark.contains  50000000           8  avgt    2   1.688          ms/op
HammL3Benchmark.contains  50000000          16  avgt    2   1.888          ms/op
HammL3Benchmark.count     50000000           0  avgt    2   ~10⁻⁴          ms/op
HammL3Benchmark.count     50000000           1  avgt    2   0.234          ms/op
HammL3Benchmark.count     50000000           2  avgt    2   0.462          ms/op
HammL3Benchmark.count     50000000           4  avgt    2   0.956          ms/op
HammL3Benchmark.count     50000000           8  avgt    2   1.719          ms/op
HammL3Benchmark.count     50000000          16  avgt    2   2.066          ms/op
```

## Thread count: 8

```
Benchmark                  (count)  (distance)  Mode  Cnt   Score   Error  Units
HammL3Benchmark.contains  50000000           0  avgt    2   ~10⁻⁴          ms/op
HammL3Benchmark.contains  50000000           1  avgt    2   0.567          ms/op
HammL3Benchmark.contains  50000000           2  avgt    2   1.072          ms/op
HammL3Benchmark.contains  50000000           4  avgt    2   2.127          ms/op
HammL3Benchmark.contains  50000000           8  avgt    2   4.217          ms/op
HammL3Benchmark.contains  50000000          16  avgt    2   5.896          ms/op
HammL3Benchmark.count     50000000           0  avgt    2   ~10⁻⁴          ms/op
HammL3Benchmark.count     50000000           1  avgt    2   0.488          ms/op
HammL3Benchmark.count     50000000           2  avgt    2   0.970          ms/op
HammL3Benchmark.count     50000000           4  avgt    2   2.098          ms/op
HammL3Benchmark.count     50000000           8  avgt    2   6.286          ms/op
HammL3Benchmark.count     50000000          16  avgt    2   8.739          ms/op
```