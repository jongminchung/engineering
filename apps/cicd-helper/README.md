# @cloud/cicd-helper

GitLab 규칙 검증과 자동화를 위한 TypeScript 기반 CLI입니다. `commander.js`와 `@gitbeaker/core`를 사용해 GitLab API를 호출하며, 컬러 출력으로 CI 로그를 읽기
쉽게 제공합니다.

## 요구 사항

- Node.js 18 이상
- [pnpm](https://pnpm.io/) 8 이상

## 설치 및 빌드

```bash
pnpm setup

# 목록 조회
pnpm list -g
```

```bash
# ~/apps/cicd-helper
pwd

pnpm link --global

pnpm rm -g @cloud/cicd-helper
```

## 지원 명령

### `team-rule mr-squash`

Merge Request가 팀 규칙에 맞게 squash 옵션을 사용했는지 검사합니다.

- 기본 GitLab URL: `https://gitlab.gabia.com` (`--base-url` 또는 `GITLAB_BASE_URL`로 재정의 가능)
- 필요 환경 변수
    - `CI_PROJECT_ID`
    - `CI_MERGE_REQUEST_IID`
    - `GITLAB_TOKEN`

예시:

```bash
node dist/index.js team-rule mr-squash \
  --project-id "$CI_PROJECT_ID" \
  --merge-request-iid "$CI_MERGE_REQUEST_IID" \
  --token "$GITLAB_TOKEN"
```

### `badge upload`

`common/badge.gitlab-ci.yml`과 동일한 로직으로 프로젝트 배지를 업데이트합니다. 실행 전 필수 변수 설정 상태를 컬러 배너로 출력합니다.

- 필요 환경 변수
    - `CI_PROJECT_ID`
    - `BADGE_ID`
    - `BADGE_URL`
    - `BADGE_IMAGE`
    - `GITLAB_TOKEN`

예시:

```bash
node dist/index.js badge upload \
  --project-id "$CI_PROJECT_ID" \
  --badge-id "$BADGE_ID" \
  --badge-url "$BADGE_URL" \
  --badge-image "$BADGE_IMAGE" \
  --token "$GITLAB_TOKEN"
```

## 개발 팁

- `pnpm lint`로 타입 검사를 수행할 수 있습니다.
- 새 명령을 추가할 때는 `src/commands` 디렉터리에 모듈을 만들고 `src/index.ts`에서 등록하세요.
