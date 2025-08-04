# SOPO: 오픈 패션 마켓 플랫폼

누구나 새 옷과 중고 옷을 자유롭게 사고팔 수 있는,  
개인과 셀러 모두를 위한 의류 중심 오픈마켓입니다.

판매자는 입점 후 상품을 등록하고,  
사용자는 장바구니와 결제를 통해 옷을 구매합니다.  
관리자는 판매자 승인과 운영을 맡으며,  
각 역할에 따라 권한이 분리되어 작동합니다.

---

## 주요 기능

- 회원가입 및 로그인
- 판매자 상품 등록 / 수정
- 장바구니 기능
- 주문 생성 및 결제
- 사용자 / 판매자 / 관리자 권한 분리

---

## 사용 기술

- Spring Boot 3.5.4
- JPA (Hibernate)
- Spring Security
- Thymeleaf
- MySQL 8.x
- Gradle

---

## 프로젝트 구조

```
com.sopo
├── config          : 설정 클래스 (보안, JPA, Web 등)
├── controller      : MVC + REST API 컨트롤러
├── domain          : 핵심 엔티티
├── dto             : 요청/응답 DTO
├── repository      : JPA 기반 데이터 접근 계층
├── service         : 도메인 단위 서비스
├── exception       : 에러 코드 및 비즈니스 예외
└── common          : 공통 유틸, 인증 관련
```

---

## 실행 방법

1. MySQL에서 `sopo` 데이터베이스를 생성합니다.

```sql
create database sopo default character set utf8mb4 collate utf8mb4_unicode_ci;
```

2. `application-local.yml` 파일을 `src/main/resources` 경로에 생성합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sopo
    username: root
    password: (비밀번호)
```

3. Gradle 빌드 후 실행합니다.

```bash
./gradlew bootRun
```

---

## 커밋 메시지 규칙

- 🎉 Initial commit: 프로젝트 초기 세팅
- ✨ feat: 새로운 기능 추가
- ♻️ refactor: 리팩토링
- 🐛 fix: 버그 수정
- 📄 docs: 문서 수정

---

## 개발자

ParkYoungWon  
https://github.com/park4ever
