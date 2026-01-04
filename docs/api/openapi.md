# OpenAPI

_**Table of Contents**_

<!-- TOC -->

* [OpenAPI](#openapi)
    * [allOf / oneOf / anyOf](#allof--oneof--anyof)
        * [1. allOf: 상속/확장](#1-allof-상속확장)
        * [2. oneOf: enum](#2-oneof-enum)
    * [3. anyOf: 하나 이상 만족하면 됨 (OR)](#3-anyof-하나-이상-만족하면-됨-or)
    * [OpenAPI Convention](#openapi-convention)

<!-- TOC -->

## allOf / oneOf / anyOf

**스키마 조합**(Composition) 용도임.

### 1. allOf: 상속/확장

**의미**

- 여러 스키마를 **합쳐서 하나로 만듦**
- 객체 기준으로는 "부모 + 자식"구조에 가장 가까움
- AND 조건

**예시**

```yaml
BaseUser:
    type: object
    required: [ id ]
    properties:
        id:
            type: string

User:
    allOf:
        -   $ref: '#/components/schemas/BaseUser'
        -   type: object
            required: [ name ]
            properties:
                name:
                    type: string
```

- `User`는 `id` 필수, `name` 필수

### 2. oneOf: enum

**의미**

- 여러 스키마 중 **딱 하나만** 유효해야 함
- XOR 개념
- polymorphism 표현에 사용

**예시**

```yaml
Cat:
    type: object
    required: [ meow ]
    properties:
        meow:
            type: boolean

Dog:
    type: object
    required: [ bark ]
    properties:
        bark:
            type: boolean

Pet:
    oneOf:
        -   $ref: '#/components/schemas/Cat'
        -   $ref: '#/components/schemas/Dog'
```

- `Pet`은 `Cat`이거나 `Dog`
- `{ meow: true, bark: true }` → X
- `{ meow: true }` → O
- `{ bark: true }` → O

**discriminator 같이 쓰는 경우(권장)**

```yaml
Pet:
    oneOf:
        -   $ref: '#/components/schemas/Cat'
        -   $ref: '#/components/schemas/Dog'
    discriminator:
        propertyName: type
```

## 3. anyOf: 하나 이상 만족하면 됨 (OR)

**의미**

- 여러 스키마 중 **하나 이상** 만족하면 유효
- OR 조건
- 유연하지만 애매함

**예시**

```yaml
HasEmail:
    type: object
    required: [ email ]
    properties:
        email:
            type: string

HasPhone:
    type: object
    required: [ phone ]
    properties:
        phone:
            type: string

Contact:
    anyOf:
        -   $ref: '#/components/schemas/HasEmail'
        -   $ref: '#/components/schemas/HasPhone'
```

**해석**

- `{ email: email@email.com }` → O
- `{ phone: 010-XXXX-XXXX }` → O
- `{ email: email@email.com, phone: 010-XXXX-XXXX }` → O

## OpenAPI Convention

OpenAPI 컨벤션을 통해 HTTP 스펙에 대해서는 팀 내 컨벤션을 적용할 수 있다.

생각의 흐름에서는 다양한 언어를 사용하는 마이크로서비스 환경에서 한 단계 추상화된 스펙에서 검증을 하게 된다면,

해당 추상화 스펙에서 API 컨벤션을 적용하면 전체적으로 적용할 수 있다는 장점이 있다.

```text
OpenAPI Convention Linter =
(Spec → AST) + (AST → Rules(Validation) + (Rules → Report)

Rule은 DSL로 표현할 수 있도록 구성.
- DSL -> 코드 매핑 방식
```
