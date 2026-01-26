# 성능 테스트 환경 요약

k6 부하 테스트와 Prometheus/Grafana 관측을 결합해 API/웹/DB 성능을 같은 타임라인에서 분석한다.

## 핵심 구성

- 테스트: k6 (필요 시 k6-browser)
- 수집/저장: Prometheus 3.9.1
- 시각화: Grafana 12.3.1
- 인프라: Kubernetes

## 흐름

1. k6가 API/웹 요청을 발생
2. Prometheus가 시스템/DB 메트릭 수집
3. Grafana에서 k6 결과와 메트릭을 함께 분석

## DB 구성

- 현재: PostgreSQL 단일 인스턴스 (`postgres:18.1`)
- 데이터 경로: `PGDATA=/var/lib/postgresql/data/pgdata` (PVC 루트 `lost+found` 충돌 회피)
- 향후: CloudNativePG로 확장

## 외부 접속

- `grafana.jamie.kr` → nginx-service:443 → Grafana
- `postgres.jamie.kr` → nginx-service:5432 → PostgreSQL
- OpenStack LB는 L4 전달, Host 기반 분기는 Nginx에서 처리
- Ingress Controller 미사용

## 시크릿 관리

- 실제 값은 Kubernetes Secret에만 저장
- 확인 방법: `./credentials.md`

## 적용 로그(요약)

- `namespaces.yml`, `postgres.yml`, `postgres_exporter.yml`, `monitoring.yml`, `nginx_service.yml` 적용
- Nginx Service annotation 타입 오류 → 문자열로 수정

## k6

- Grafana 대시보드: 기본 제공 템플릿 사용(특정 ID 없음)
- 메타데이터: 기존 PostgreSQL 저장

구현 계획(실행 단계)

1.  k6-operator 설치(Helm)
    - monitoring 네임스페이스에 Helm 설치
    - CRD 설치 확인 및 기본 values 정리
2.  DB 스키마 정의
    - 테이블: perf_tests
        - id, name, description, script_path, created_at, updated_at
    - 마이그레이션 방식 결정(앱 초기화 시 SQL 실행 or 별도 마이그레이션 도구)
3.  스크립트 저장 PVC 설계
    - PVC 생성 + 앱 서버에 마운트
    - 업로드 시 /scripts/<test-name>/script.js로 저장

## 참고

- https://tech.kakaopay.com/post/perftest_zone/
- https://grafana.com/docs/k6/latest/
- https://cloudnative-pg.io/docs/1.28/

### 고도화 관련 참고

- https://grafana.com/docs/k6/latest/using-k6-browser/
- https://openai.com/index/scaling-postgresql/
