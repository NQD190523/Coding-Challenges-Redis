<h1 align="center">Welcome to Coding-Challenges-Redis 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
  <a href="https://docs.google.com/document/d/1R8__7EFc0nS__trKjRQgYETu2VLCDJP8n4DXrxag72M/edit?usp=sharing" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
</p>

> This is a coding-challenge project, implementation of a Redis-like in-memory key-value store, written in Java. Designed as an educational project to explore distributed systems and server development, it replicates core Redis functionality while providing a simple, extensible foundation for learning and experimentation. Running on port 6380, it supports a subset of Redis commands, making it compatible with tools like redis-cli and redis-benchmark for testing and performance evaluation.

## Install

```sh
npm install
```

## Usage

#### Clone the Repository
Clone the project from GitHub to your local machine:
```sh
git clone https://github.com/NQD190523/Coding-Challenges-Redis.git 
```
#### Compile the Java source files
```sh
javac -d bin/main src/main/main/com/codingchallenges/*.java 
```
#### Run the Server
```sh
java -cp bin/main main.com.codingchallenges.redisserver.RedisServer
```
#### Test with redis-cli
In a separate terminal, connect and run commands:
```sh
redis-cli -h localhost -p 6380
127.0.0.1:6380> SET key "hello"
OK
127.0.0.1:6380> GET key
"hello"
127.0.0.1:6380> INCR counter
1
```
#### Optional: Benchmark Performance
Evaluate RedisCC performance:
```sh
redis-benchmark -h localhost -p 6380 -c 50 -n 10000
```

## Author

👤 **Duc Nguyen**

* Github: [@DucDUc](https://github.com/DucDUc)
* LinkedIn: [@Đức Nguyễn](https://linkedin.com/in/Đức Nguyễn)

## Show your support

Give a ⭐️ if this project helped you!

***
_This README was generated with ❤️ by [readme-md-generator](https://github.com/kefranabg/readme-md-generator)_