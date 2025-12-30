# Infrastructure as Code (IaC)

Infrastructure as Code(IaC)는 코드를 통해 인프라를 프로비저닝하고 관리하는 방식임.

**_Table of Contents_**
<!-- TOC -->

- [Infrastructure as Code (IaC)](#infrastructure-as-code-iac)
  - [핵심 개념](#핵심-개념)
    - [1. 선언형 (Declarative) vs 명령형 (Imperative)](#1-선언형-declarative-vs-명령형-imperative)
  - [Ansible vs Terraform 비교](#ansible-vs-terraform-비교)
    - [공통점](#공통점)
  - [Terraform (프로비저닝 중심)](#terraform-프로비저닝-중심)
    - [특징](#특징)
    - [코드 예시 (HCL)](#코드-예시-hcl)
  - [Ansible (구성 관리 중심)](#ansible-구성-관리-중심)
    - [특징](#특징-1)
    - [코드 예시 (YAML)](#코드-예시-yaml)
  - [공식 문서 링크](#공식-문서-링크)

<!-- TOC -->

## 핵심 개념

### 1. 선언형 (Declarative) vs 명령형 (Imperative)

- **선언형 (Declarative)**: "무엇(What)"을 원하는지 정의함.
  최종 상태(Desired State)를 기술하면 도구가 이를 달성하기 위한 절차를 자동으로 결정함.
  - 예: "VPC 1개와 서브넷 2개가 있는 상태를 만들어줘."
  - 대표 도구: **Terraform**(OpenTofu), CloudFormation, Kubernetes Manifests.
- **명령형 (Imperative)**: "어떻게(How)" 목표를 달성할지 정의함.
  실행할 명령어나 단계를 순서대로 나열합니다.
  - 예: "AWS CLI로 VPC를 만들고, 그 아이디를 받아서 서브넷을 생성해."
  - 대표 도구: **Ansible**(하이브리드적 성격), Shell Scripts.

---

## Ansible vs Terraform 비교

| 구분         | Terraform                 | Ansible                          |
|:-----------|:--------------------------|:---------------------------------|
| **주요 목적**  | 인프라 프로비저닝 (Provisioning)  | 구성 관리 (Configuration Management) |
| **방식**     | 선언형 (Declarative)         | 선언형 + 명령형 (Hybrid)               |
| **상태 관리**  | State 파일 있음 (`.tfstate`)  | 상태 저장 안 함 (Stateless)            |
| **통신 방식**  | Agentless (API 호출)        | Agentless (SSH/WinRM)            |
| **라이프사이클** | 생성, 수정, 삭제(Destroy) 관리 용이 | 주로 설정 적용 및 배포에 최적화               |

### 공통점

- **Templating**: 변수와 템플릿을 사용하여 동적인 설정 가능.
- **Open Source**: 활발한 커뮤니티와 에코시스템.
- **Agentless**: 대상 서버에 별도의 에이전트 설치가 필요 없음.

---

## Terraform (프로비저닝 중심)

Terraform은 인프라의 현재 상태를 `state` 파일로 관리하며, 변경 사항이 있을 때 `plan`을 통해 예상 결과를 미리 확인할 수
있습니다.

### 특징

- **Provisioning**: VPC, DB, EC2 등 인프라 리소스를 생성하는 데 특화.
- **State**: 인프라의 실제 상태와 코드를 동기화하여 추적.
- **Dependency**: 리소스 간의 의존성을 자동으로 파악하여 실행 순서를 결정.

### 코드 예시 (HCL)

```hcl
# VPC 생성 예시
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "main-vpc"
  }
}

# DB 생성 (VPC 의존성 예시)
resource "aws_db_instance" "default" {
  allocated_storage    = 10
  engine               = "mysql"
  instance_class       = "db.t3.micro"
  db_name              = "mydb"
  # VPC가 생성된 후 진행되도록 암시적/명시적 의존성 관리 가능
}
```

---

## Ansible (구성 관리 중심)

Ansible은 Playbook을 통해 서버 내 패키지 설치, 설정 파일 수정, 서비스 실행 등을 자동화합니다.

### 특징

- **Configuration Management**: 운영체제 내부 설정 및 애플리케이션 배포에 특화.
- **Top to Bottom**: Playbook에 작성된 순서대로 작업(Task)이 실행됨.
- **Idempotency (멱등성)**: 동일한 작업을 여러 번 실행해도 결과가 동일하도록 보장 (선언적 요소).

### 코드 예시 (YAML)

```yaml
- name: Nginx 설치 및 실행 Playbook
  hosts: webservers
  become: yes
  tasks:
    - name: Nginx 패키지 설치
      apt:
        name: nginx
        state: present

    - name: Nginx 설정 파일 복사
      template:
        src: nginx.conf.j2
        dest: /etc/nginx/nginx.conf
      notify: Restart Nginx

  handlers:
    - name: Restart Nginx
      service:
        name: nginx
        state: restarted
```

---

## 공식 문서 링크

- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
- [Ansible Documentation](https://docs.ansible.com/)
- [Terraform vs. Ansible (Hashicorp Blog)](https://www.hashicorp.com/resources/ansible-terraform-better-together)
