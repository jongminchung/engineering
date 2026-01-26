# K6 admin page

- 스크립트 경로: /scripts/<test-name>/script.js로 통일
- Grafana 대시보드: 기본 제공 템플릿 사용(특정 ID 없음)
- Grafana 임베딩: auth_request + 로그인 페이지 리다이렉트
- 메타데이터: 기존 PostgreSQL 저장
- 앱: TanStack Start, Kubernetes Deployment

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
4.  TanStack Start API
    - POST /tests: 업로드 + DB 저장
    - GET /tests: 목록
    - POST /tests/{id}/run: k6 TestRun CR 생성
    - DELETE /tests/{id}: 삭제 + 스크립트 파일 삭제
5.  UI 구성
    - 목록/업로드/실행 버튼 1페이지
    - 우측(또는 하단)에 Grafana iframe 영역 배치
    - 기본 Grafana 홈/템플릿 URL 사용
6.  Grafana 프록시(auth_request)
    - Nginx /grafana 요청에 auth_request 연결
    - 인증 실패 시 앱 로그인 페이지로 리다이렉트
    - 성공 시 Grafana 서비스로 프록시
7.  문서 업데이트
    - perf-test/readme.md에 설치/운영 절차 추가
    - 인증/접근 흐름 요약
