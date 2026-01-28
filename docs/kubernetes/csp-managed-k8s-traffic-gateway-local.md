# CSP 관리형 K8s 외부 트래픽 흐름과 Gateway API 비교

## 1. CSP 관리형 K8s에서 외부 트래픽이 들어오는 방식

관리형 K8s는 "클라우드 로드밸런서 → 클러스터" 구조를 기본으로 제공합니다.

1. 클라우드 L4/L7 로드밸런서 생성

- Service(type=LoadBalancer) 또는 Ingress/Gateway를 만들면
  CSP가 관리형 로드밸런서를 자동 생성/연결합니다.
- 트래픽은 공인 IP 또는 DNS로 유입됩니다.

2. 클러스터 내부 라우팅

- L4 로드밸런서: NodePort 또는 kube-proxy 경유로 Pod로 전달
- L7 로드밸런서: Ingress Controller 또는 Gateway Controller가 규칙 기반 라우팅

3. 보안 및 네트워크 연동

- 보통 VPC, 보안그룹/방화벽, WAF, TLS 관리가 LB 레이어에서 결합됩니다.

## 2. Gateway API와 관리형 K8s 트래픽 경로 비교

### 공통점

- L7 라우팅 규칙을 K8s 선언으로 관리한다.
- 컨트롤러가 리소스를 해석해서 실제 데이터 플레인을 만든다.

### 차이점

1. 컨트롤러 구현 주체
    - Gateway API: 구현은 컨트롤러(Envoy, HAProxy, Istio 등)에 달려 있음
    - 관리형 K8s: CSP가 제공하는 컨트롤러가 클라우드 LB와 직접 연동
2. 트래픽 엔드포인트
    - Gateway API: 컨트롤러가 받는 IP/LoadBalancer는 환경에 따라 다름
    - 관리형 K8s: CSP가 LB를 자동 프로비저닝하고 공인 IP 제공
3. 정책/보안 통합
    - Gateway API: TLS, 인증, 정책은 컨트롤러 구현에 따라 범위가 달라짐
    - 관리형 K8s: CSP의 IAM, 보안 정책, 인증서 서비스와 결합하기 쉬움
4. 운영 책임
    - Gateway API: 컨트롤러 설치/업그레이드는 사용자가 관리
    - 관리형 K8s: 컨트롤러와 LB 연동은 CSP가 관리, 표준화된 운영 제공

## 3. 로컬 Podman Desktop Kubernetes와의 차이

### 인프라 수준 차이

- 로컬 Kubernetes는 단일 노드 또는 경량 VM 위에서 동작
- CSP는 다중 노드 + VPC 네트워크 + 관리형 LB가 결합

### 로드밸런서 제공 여부

- 로컬 환경에는 기본적으로 클라우드 LB가 없음
- Service(type=LoadBalancer)를 만들면 "실제 외부 LB"가 없음

### 네트워크 제약

- 로컬은 host 네트워크 또는 VM 네트워크 안에서 제한적으로만 접근
- CSP는 공인 IP, VPC 라우팅을 통해 외부 트래픽이 자연스럽게 유입됨

## 4. 로컬 환경에서 localhost 트래픽 받는 방법

### 방법 A: Port-Forward (가장 간단)

```
kubectl port-forward svc/my-service 8080:80
```

- localhost:8080으로 접근 가능
- 개발/디버깅용에 적합

### 방법 B: NodePort

```
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: NodePort
  selector:
    app: my-app
  ports:
  - port: 80
    targetPort: 8080
    nodePort: 30080
```

- localhost:30080으로 접근 가능(환경에 따라 VM IP 사용)
- 포트가 고정되며 간단한 테스트에 사용

### 방법 C: Ingress/Gateway + 로컬 LB 에뮬레이션

- 로컬용 Ingress Controller 설치 (nginx, traefik 등)
- localhost 또는 VM IP에 매핑
- Podman Desktop은 환경에 따라 동작 방식이 다르므로
  설치된 컨트롤러의 노출 방식을 확인 필요

## 5. EKS에서 Gateway API 선택 포인트

- EKS는 기본 Gateway 컨트롤러가 내장되어 있지 않음
- 선택지는 두 가지
    - AWS 제공 Gateway 컨트롤러: ALB/NLB, ACM, WAF, 보안그룹 연동이 쉬움
    - 서드파티 컨트롤러(Istio/NGINX/Kong/Traefik/Envoy 등): 기능 자유도 높음
- 어느 쪽이든 Gateway API는 사용 가능하며, 외부 노출 방식이 다를 뿐

## 6. 요약 비교

- CSP 관리형 K8s는 LoadBalancer 리소스가 기본 외부 트래픽 엔드포인트
- Gateway API는 라우팅 정의이고, 실제 트래픽 종단점은 컨트롤러가 결정
- 로컬 K8s는 외부 LB가 없어서 port-forward/NodePort/Ingress로 우회
