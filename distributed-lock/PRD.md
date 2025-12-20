# 분산락 시스템

AOP 기반(어노테이션) + 프로그래밍 API(LockTemplate) 동시 제공 설계

- lock-provider-redis
- lock-provider-zookeeper
- lock-provider-etcd

... 확장 가능한 구조로 설계

spring boot v4 와 같이 모듈을 분리하여 사용할 수 있도록 설계

