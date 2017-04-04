# githubcontributors
Current Version: 1.0.2

![GitHub Contributors](http://www.aha.io/assets/integration_logos/github-bb449e0ffbacbcb7f9c703db85b1cf0b.png)

## How to build
```text
# Build the artifact
gradle fatJar

# And run!!
java -Dlog4j.configurationFile=conf/log4j2.xml -jar build/libs/githubContributors-1.0.2.jar
```

## Dependencies
- UnirestIO: Lib allows to do request to GitHub API (so fast and clear)
- UndertowIO: WebContainer lib (so lighty and fast)
- GSON (Google): Lib to generate Json messages
- HazelCast: Distributed Cache (in order to scale with more nodes, and cache response from GitHub API)

## Author
Miguel Ángel Zambrana
