# githubcontributors
Current Version: 1.0.3

![GitHub Contributors](http://www.aha.io/assets/integration_logos/github-bb449e0ffbacbcb7f9c703db85b1cf0b.png)

## How to build
```text
# Build the artifact
gradle fatJar

# And run!!
java -Dlog4j.configurationFile=conf/log4j2.xml -jar build/libs/githubContributors-1.0.2.jar
```

## Config And port
Service is running HTTPService by default in port 8080, and host is "localhost", but you can change
it for production environment.

You can config the service properties in the file "conf/configuration.properties"

Default values:
```text
HttpServicePort=8080
HazelCastGroup=GitHubContributors
ServiceHttpHost=localhost
```

## Request samples

### Version

Version Request

- http://localhost:8090/version
```text
1.0.2
```

### Top Requests

Top Request need to send the following segments:
* operation
* location (or city)

The request should by like the samples:
* http://localhost:8090/top5/Malaga
* http://localhost:8090/top10/Barcelona
* http://localhost:8090/top25/Barcelona
* http://localhost:8090/top50/New York

You can specify operations top operations with number less or equals than 50, for example:
* top5, top10, top20, top25, top50, top8, top2, top15, ...

#### Some samples
- http://localhost:8090/top5/Malaga
```json
[
    {
    "userId": 472208,
    "login": "Ilgrim",
    "location": "Malaga"
    },
    {
    "userId": 3452158,
    "login": "athento",
    "location": "Malaga"
    },
    {
    "userId": 1378853,
    "login": "PeteBoucher",
    "location": "Malaga"
    },
    {
    "userId": 4376867,
    "login": "deors",
    "location": "Malaga"
    },
    {
    "userId": 31100,
    "login": "jesusgollonet",
    "location": "Malaga"
    }
]
```

- http://localhost:8090/top5/Barcelona
```json
[
    {
    "userId": 663460,
    "login": "ajsb85",
    "location": "Barcelona"
    },
    {
    "userId": 13684313,
    "login": "leobcn",
    "location": "Barcelona"
    },
    {
    "userId": 1142360,
    "login": "ardock",
    "location": "Barcelona"
    },
    {
    "userId": 1734320,
    "login": "joeromero",
    "location": "Barcelona"
    },
    {
    "userId": 1731699,
    "login": "netmarti",
    "location": "Barcelona"
    }
]
```

## Scale
The Github Contributors Service can scale with multiple nodes really fast.
Each Github Contributors node have a HazelCast instance in order to share data between the different 
nodes, and avoid to do the same request to GitHub API. 

### Why Hazelcast?
We have other solutions in the market, like Redis for example, but for this context is more fast to
deploy a service based on HazelCast, because this is integrated in the same service, and we don't need
to run a third party service (like Redis, for example).
 
### How to configure nodes
The idea is config the "conf/hazelcast.xml" file in order to define the IP nodes, for example:

```xml
        <join>
            <!-- Can configure by multicast -->
            <multicast enabled="false">
                <multicast-group>224.2.2.3</multicast-group>
                <multicast-port>54327</multicast-port>
            </multicast>
            <tcp-ip enabled="true">
                <!-- Can define here the different service nodes -->
                <interface>192.168.1.10</interface>
                <interface>192.168.1.11</interface>
                <!-- Can define the interface with a domain 
                (Remember to conf the internal interface in /etc/hosts to resolv name) -->
                <interface>node3.example.com</interface> 
            </tcp-ip>
            <aws enabled="false">
                (...)
            </aws>
        </join>
```

In the tcp-ip block, you can configure the different nodes, for example, for a 3 nodes cluster:
* 192.168.1.10 // Node 1
* 192.168.1.11 // Node 2
* node3.example.com // Node 3 defined with a domain

## High Performance
The service use light and fast libraries in order to have nice performance; the idea is use small and 
efficient components, but really fast to change by other (if they can improve it more).

By other hand, HazelCast allows to scale in number of nodes and share the information between their,
and at the same time, cache the results (for an hour) in order to request to Github the minimal number of times.
This cache allows to improve the request average time when the user request the same location 
(with different or the same top operation).

## Performance improves v1.0.2 to v1.0.3
The service added a first Cache layer using Caffeine libraries in order to improve the efficiency of Cache
hits. In this type of paradigm, Cache has a high percentage of hits, since the variability of locations 
is high, but there is a greater probability that different tops are requested for the same cities; 
So it is very interesting to improve the performance in the cache (adding a first layer lighter 
than Hazelcast we can improve the performance).

By the other hand, in case of Miss, the service will send a request to GitHub, and the service will
update the different cache levels asynchronously, in order to improve the response time.

### Benchmark evolution from v1.0.2 to v1.0.3 

- WRK Results for Service v1.0.2 (based only in Hazelcast) - Scenario with always HITS
```
Running 30s test @ http://localhost:8080/top5/Barcelona
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    11.34ms   16.70ms 467.19ms   92.93%
    Req/Sec     3.80k     1.67k    8.84k    63.91%
  445425 requests in 30.09s, 201.78MB read
Requests/sec:  14800.77
Transfer/sec:      6.70MB

Running 30s test @ http://localhost:8080/top5/Barcelona
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    10.67ms   16.57ms 408.82ms   94.40%
    Req/Sec     3.86k     1.77k    8.69k    63.19%
  446956 requests in 30.09s, 202.47MB read
  Socket errors: connect 144, read 0, write 0, timeout 0
Requests/sec:  14855.35
Transfer/sec:      6.73MB
```

- WRK Results for Service v1.0.3 (based on Caffeine in first layer) - Scenario with always HITS
```
Running 30s test @ http://localhost:8080/top5/Barcelona
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.17ms   14.79ms 411.24ms   98.80%
    Req/Sec     5.67k     1.69k   11.43k    69.82%
  677442 requests in 30.03s, 306.88MB read
Requests/sec:  22556.23
Transfer/sec:     10.22MB

Running 30s test @ http://localhost:8080/top5/Barcelona
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.56ms   12.26ms 413.53ms   98.11%
    Req/Sec     4.92k     1.68k   10.55k    69.77%
  587563 requests in 30.10s, 266.16MB read
Requests/sec:  19523.08
Transfer/sec:      8.84MB

Running 30s test @ http://localhost:8080/top5/Barcelona
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.01ms   12.13ms 408.51ms   98.82%
    Req/Sec     5.49k     1.63k   11.29k    68.06%
  655234 requests in 30.03s, 296.82MB read
  Socket errors: connect 0, read 1, write 0, timeout 0
Requests/sec:  21816.32
Transfer/sec:      9.88MB
```

We can see an improvement in the performance only using Caffeine Libraries

## Monitor
Service logger show KPIs about the Service each minute like the sample:

```text
2017-04-04 08:08:23,665 INFO (MonitorInfo.java:25): ** Monitor Report **********************************
2017-04-04 08:08:23,666 INFO (MonitorInfo.java:26):  > Total HTTP Requests:     40
2017-04-04 08:08:23,666 INFO (MonitorInfo.java:27):  > Total Hit Cache:         37 (92.5%)
2017-04-04 08:08:23,667 INFO (MonitorInfo.java:28):  > Total GitHub Requests:   3 (7.5%)
2017-04-04 08:08:23,667 INFO (MonitorInfo.java:29):  > Total Error Requests:    1 (2.5%)
2017-04-04 08:08:23,667 INFO (MonitorInfo.java:30):  > Avg. request time:       61.175ms
2017-04-04 08:08:23,683 INFO (MonitorInfo.java:31):  > Cache Contributors size: 3
2017-04-04 08:08:23,684 INFO (MonitorInfo.java:32):  > Hazelcast nodes:         1
2017-04-04 08:08:23,684 INFO (MonitorInfo.java:33): ****************************************************
```

## Dependencies
- UnirestIO: Lib allows to do request to GitHub API (so fast and clear)
- UndertowIO: WebContainer lib (so lighty and fast)
- GSON (Google): Lib to generate Json messages
- Caffeine Libs: Local Cache - Level 1 (in order to improve performance for a Cache Hits, it is better than HazelCast solution)
- HazelCast: Distributed Cache - Level 2 (in order to scale with more nodes, and cache response from GitHub API)

## Benchmark tools
- wrk: wrk is a modern HTTP benchmarking tool capable of generating significant load when run on a 
single multi-core CPU. It combines a multithreaded design with scalable event notification systems 
such as epoll and kqueue. More info: https://github.com/wg/wrk


## Author
Miguel Ángel Zambrana
