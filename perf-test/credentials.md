# Credentials

## 원칙

- 실제 값은 Kubernetes Secret에만 저장한다.
- 저장소에는 평문 값을 기록하지 않는다.

## Grafana

- Secret: `grafana-admin` (namespace: `monitoring`)
- Keys: `username`, `password`
- 확인 방법:
    - `kubectl -n monitoring get secret grafana-admin -o jsonpath='{.data.username}' | base64 --decode`
    - `kubectl -n monitoring get secret grafana-admin -o jsonpath='{.data.password}' | base64 --decode`

## PostgreSQL

- Secret: `postgres-credentials` (namespace: `infra`)
- Keys: `username`, `password`, `database`
- 확인 방법:
    - `kubectl -n infra get secret postgres-credentials -o jsonpath='{.data.username}' | base64 --decode`
    - `kubectl -n infra get secret postgres-credentials -o jsonpath='{.data.password}' | base64 --decode`
    - `kubectl -n infra get secret postgres-credentials -o jsonpath='{.data.database}' | base64 --decode`

## Postgres Exporter

- Secret: `postgres-exporter` (namespace: `infra`)
- Key: `data_source_name`
- 확인 방법:
    - `kubectl -n infra get secret postgres-exporter -o jsonpath='{.data.data_source_name}' | base64 --decode`
