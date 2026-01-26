# argo-cd

성능 테스트 환경을 GitOps로 분리해 Argo CD로 운영하기 위한 가이드를 정리한다.

## 기본 방향

- perf-test 전용 Git 저장소로 매니페스트를 분리한다.
- Argo CD는 해당 저장소를 감시해 배포 상태를 유지한다.
- 클러스터별 환경 차이는 `overlays`로 관리한다.

## 저장소 구조 예시

- `manifests/base`: 공통 리소스
- `manifests/overlays/dev`: 개발 환경 (현재는 개인 프로젝트이기에 없음)
- `manifests/overlays/prod`: 운영 환경

## 적용 대상

- `docs/perf_test/namespaces.yml`
- `docs/perf_test/postgres.yml`
- `docs/perf_test/postgres_exporter.yml`
- `docs/perf_test/monitoring.yml`
- `docs/perf_test/nginx_service.yml`

## Argo CD Application 설계

- 네임스페이스는 `CreateNamespace=true`로 자동 생성한다.
- `syncPolicy.automated`로 자동 동기화를 활성화한다.
- 리소스 보존이 필요한 경우 `prune` 정책을 별도로 검토한다.

## 권장 운영 규칙

- 실제 Secret 값은 Git에 저장하지 않는다.
- Secret은 External Secrets 또는 별도 관리 경로로 운영한다.
- 배포 이력과 변경 로그는 PR 단위로 관리한다.

## 배포 예시(Application)

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
    name: perf-test
    namespace: argocd
spec:
    project: default
    source:
        repoURL: https://github.com/<org>/<repo>
        targetRevision: main
        path: manifests/overlays/dev
    destination:
        server: https://kubernetes.default.svc
        namespace: monitoring
    syncPolicy:
        automated:
            prune: true
            selfHeal: true
        syncOptions:
            - CreateNamespace=true
```

## 참고

- Argo CD UI에서 Sync 상태와 Health 상태를 항상 확인한다.
- 변경은 Git 반영 → Argo CD Sync 순서로 유지한다.
