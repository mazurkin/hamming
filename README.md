h1. Descriptions

This algorithms splits 64-bits long value into four 16-bit sections. We count the number of bits in
each section so we get 4 counters in range [0..16]. So any value could be referred to a bag with
septendecimal bag-id in range [0000@x17...GGGG@x17] (numbers are in 17-base format). The total number
of bags is 17^4=83521

When we search for a value with hamming distance we determine the according range of bag-ids and
sequentially scan value arrays in all these bags for proper values.

Some optimitizations are implemented to skip bags with incompatible bit structure.

h1. How to run

```
$ mvn clean install
$ java -jar target/benchmarks.jar
```

h1. Becnhmark results

h2. Thread count: 1

```
Benchmark                  (count)  (distance)  Mode  Cnt  Score   Error  Units
HammL3Benchmark.contains  50000000           1  avgt    2  0.246          ms/op
HammL3Benchmark.contains  50000000           2  avgt    2  0.526          ms/op
HammL3Benchmark.contains  50000000           4  avgt    2  1.024          ms/op
HammL3Benchmark.contains  50000000           8  avgt    2  2.116          ms/op
HammL3Benchmark.contains  50000000          16  avgt    2  2.794          ms/op
HammL3Benchmark.count     50000000           1  avgt    2  0.297          ms/op
HammL3Benchmark.count     50000000           2  avgt    2  0.581          ms/op
HammL3Benchmark.count     50000000           4  avgt    2  0.979          ms/op
HammL3Benchmark.count     50000000           8  avgt    2  1.929          ms/op
HammL3Benchmark.count     50000000          16  avgt    2  2.199          ms/op
```

h2. Thread count: 24

```
Benchmark                  (count)  (distance)  Mode  Cnt   Score   Error  Units
HammL3Benchmark.contains  50000000           1  avgt    2   2.687          ms/op
HammL3Benchmark.contains  50000000           2  avgt    2   6.539          ms/op
HammL3Benchmark.contains  50000000           4  avgt    2  14.847          ms/op
HammL3Benchmark.contains  50000000           8  avgt    2  32.140          ms/op
HammL3Benchmark.contains  50000000          16  avgt    2  32.247          ms/op
HammL3Benchmark.count     50000000           1  avgt    2   2.827          ms/op
HammL3Benchmark.count     50000000           2  avgt    2   6.502          ms/op
HammL3Benchmark.count     50000000           4  avgt    2  14.285          ms/op
HammL3Benchmark.count     50000000           8  avgt    2  25.363          ms/op
HammL3Benchmark.count     50000000          16  avgt    2  29.107          ms/op
```

h2. Thread count: 24 (RWLock in HammL3Cell)

```
Benchmark                  (count)  (distance)  Mode  Cnt   Score   Error  Units
HammL3Benchmark.contains  50000000           1  avgt    2   3.831          ms/op
HammL3Benchmark.contains  50000000           2  avgt    2   8.776          ms/op
HammL3Benchmark.contains  50000000           4  avgt    2  20.637          ms/op
HammL3Benchmark.contains  50000000           8  avgt    2  36.630          ms/op
HammL3Benchmark.contains  50000000          16  avgt    2  38.309          ms/op
HammL3Benchmark.count     50000000           1  avgt    2   3.596          ms/op
HammL3Benchmark.count     50000000           2  avgt    2   7.793          ms/op
HammL3Benchmark.count     50000000           4  avgt    2  19.547          ms/op
HammL3Benchmark.count     50000000           8  avgt    2  32.659          ms/op
HammL3Benchmark.count     50000000          16  avgt    2  37.453          ms/op
```
