# WebFlux + Redis 를 이용한 접속자 대기열 연습

### 구조
- 단순 접속 가능 및 대기열 조회를 하는 `web-service` 모듈
- 대기열 우선처리를 하는 `wait-service` 모듈

### 프로세스
- 접속자가 진입 가능한지 대기열 서비스에서 확인 후 가능하면 리다이렉트 처리
- 그렇지 않다면 `wait-serivce` 로 지속적인 통신으로 가능 여부 체크

### Redis 환경
- docker 컨테이너 기반으로 localhost 에서 실행

```shell
   docker run --name redis -p 6379:6379 -d redis
```

### MySQL 환경
- R2DBC 를 사용하고 MySQL 를 docker 컨테이너 기반으로 localhost 에서 실행

```shell
   docker run --name mysql --env=MYSQL_ROOT_PASSWORD=root --env=MYSQL_USER=mysqluser --env=MYSQL_PASSWORD=mysqlpw -p 3306:3306 -d mysql:8.0
```

### jmeter test
- Number of Threads (users) : 30
- Ramp-up period (seconds) : 10
- url : http://localhost/waiting-room:9010
- method : GET
- params
  - userId : ${__Random(1, 999999)}
  - redirectUrl : http://localhost:9000
- redis docker 컨테이너 접속 후 아래 명령어로 대기, 허용 인원 수 모니터링
```shell
   while [ ture ] ; do date; redis-cli zcard users:queue:default:wait; redis-cli zcard users:queue:default:proceed; sleep 1; done;
```
- 최대 접속 허용 인원 100명으로 설정되어 있으며 3초마다 허용 시킴