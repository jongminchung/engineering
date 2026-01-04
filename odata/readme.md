# OData

- **OData**(Open Data Protocol)는 HTTP + REST + JSON/XML 위에서 동작하는
  데이터 접근 표준 프로토콜임

- 목적은 → **서버의 데이터를 표준화된 방식으로 조회·필터·정렬·페이징·수정**하게 만드는 것임
- Microsoft 주도로 시작했고
  지금은 **[OASIS](https://www.oasis-open.org/)** 표준임

## OData가 정의하는 것들

OData는 단순히 URL 규칙만 말하는 게 아님
아래 전부를 포함함

### 1. 리소스 모델 (데이터 모델)

- Entity (엔티티)
- EntitySet (엔티티 컬렉션)
- Property (속성)
- Navigation Property (관계)

예:

```text
Products
 ├─ ID
 ├─ Name
 ├─ Price
 └─ Category
```

### 2. 표준 URL 규칙 (쿼리 옵션)

OData의 핵심임
**CRUD + 검색 + 필터링 + 정렬 + 페이징**을 전부 URL로 표현함

| 기능    | OData 쿼리        |
|-------|-----------------|
| 필터    | `$filter`       |
| 정렬    | `$orderby`      |
| 선택    | `$select`       |
| 페이징   | `$top`, `$skip` |
| 관계 포함 | `$expand`       |
| 개수    | `$count`        |

### 3. 메타데이터 제공

OData 서버는 **자기 자신이 어떤 API인지 설명하는 메타데이터**를 제공함

```bash
GET /odata/$metadata
```

- EDMX(XML) 형식
- 타입, 필드, 관계, 키 정보 전부 정의됨
- 클라이언트 자동 생성 가능

### 4. HTTP 메서드 규칙

| 작업 | HTTP        |
|----|-------------|
| 조회 | GET         |
| 생성 | POST        |
| 수정 | PATCH / PUT |
| 삭제 | DELETE      |

REST 원칙 그대로 따름

## OData는 RESTful API 표준인가?

정확히 말하면 아님

- REST = 아키텍처 스타일
- OData = REST 위에서 동작하는 구체적인 프로토콜 규약

관계는 이렇다고 보면 됨

```text
REST (개념)
 └─ OData (구현 표준 중 하나)
```

비유하면

- REST = “자유형 수영”
- OData = “자유형 수영의 국제 경기 규칙”

## OData URL 예시

**전체 조회**

```bash
GET /odata/Products
```

**단건 조회**

```bash
GET /odata/Products(1)
```

**필터**

```bash
GET /odata/Products?$filter=Price gt 1000
```

**정렬**

```bash
GET /odata/Products?$orderby=Price desc
```

**페이징**

```bash
GET /odata/Products?$top=10&$skip=20
```

**특정 필드만**

```bash
GET /odata/Products?$select=ID,Name
```

**관계 포함**

```bsah
GET /odata/Products?$expand=Category
```

**개수**

```text
GET /odata/Products/$count
```

**응답 예시(JSON)**

```json
{
    "@odata.context": "https://example.com/odata/$metadata#Products",
    "value": [
        {
            "ID": 1,
            "Name": "Laptop",
            "Price": 1500
        }
    ]
}
```
