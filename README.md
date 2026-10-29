# 우리의 기록

웹서비스설계 JWT 게시판 과제입니다. 본 저장소는 소스 코드, 실행 가이드, 그리고 최종 제출 보고서를 포함하고 있습니다.

## 작성자와 서비스 소개

- 학번 / 이름: 202413160 최은서
- 서비스 목적과 대상 사용자: 본 서비스는 바쁘게 살아가는 현대인들이 일상의 소소한 순간을 짧은 글로 공유하며 서로의 안부를 확인하고 소통할 수 있도록 제작된 웹 게시판 서비스입니다. 비로그인 사용자는 누구나 전체 게시글 목록 및 상세 내용을 조회할 수 있으며, 로그인한 사용자만 글 작성 및 본인 글의 수정·삭제가 가능하도록 설계되었습니다.
- 선택한 기술: Vue + Spring Boot (Java 17+)
- 주요 기능: 공개 목록 및 상세 조회, 로그인 및 JWT(HS256) 발급, 인증 및 내 정보 조회(`/me`), 본인 글 작성·수정·삭제(서버 권한 검사), 토큰 만료·로그아웃·오류 처리, SQLite 파일 DB 데이터 유지

## 보고서와 테스트 증거

- [구현 보고서 PDF](docs/report.pdf)
- [테스트 증거](docs/evidence/)

## 프로젝트 구조

```text
.
├── README.md
├── .gitignore
├── backend-spring/    # Spring Boot 백엔드 소스 및 의존성 파일 (사용 중)[cite: 6]
├── frontend-vue/      # Vue.js 프론트엔드 소스 및 의존성 파일 (사용 중)[cite: 6]
├── backend-express/   # 타 스택 템플릿 (미사용)[cite: 6]
├── backend-fastapi/   # 타 스택 템플릿 (미사용)[cite: 6]
├── frontend-react/    # 타 스택 템플릿 (미사용)[cite: 6]
├── tools/             # API 자동 검사 스크립트 등 도구 폴더[cite: 6]
└── docs/
    ├── report.pdf     # 구현 보고서 PDF
    └── evidence/      # 테스트 증거 폴더

```

## 실행 환경

| 항목 | 사용 버전 |
| --- | --- |
| 운영체제 | Windows 11

 |
| 프론트 런타임 Node.js | Node.js v18+ |
| 백엔드 런타임 | JDK 17+ (Spring Boot) |
| 프레임워크 | Vue 3 + Spring Boot |
| 데이터 저장 | SQLite (필수)

 |

## 설치와 실행

본인 PC와 교수자 PC에서 로컬로 재현할 수 있도록 실제 명령을 적으세요. 설치·빌드·실행·종료를 구분하고 각 명령을 실행할 폴더를 명시합니다. GitHub는 코드와 문서 제출용이며 외부 배포 URL은 필요하지 않습니다.

### 1 저장소 복제

```sh
git clone [https://github.com/eunseo07211-lab/Our-Board.git](https://github.com/eunseo07211-lab/Our-Board.git) JWT-Assignment
cd JWT-Assignment

```

### 2 백엔드 준비와 실행

* 백엔드 폴더 이동 및 실행 명령 (Maven 빌드):


```sh
cd backend-spring
.\mvnw.cmd spring-boot:run

```


* 환경설정 예시 파일: `backend-spring/src/main/resources/application.properties`


* JWT_SECRET 설정 방법: 애플리케이션 설정 파일 또는 환경 변수를 통한 HS256 서명용 비밀 키 주입 (실제 키는 기록하지 않음)


* TOKEN_TTL: 기본 토큰 만료 시간 설정 및 만료 테스트용 변경 가능


* 백엔드 주소: `http://127.0.0.1:8000`


* 정상 실행 확인 방법: `http://127.0.0.1:8000/api/health` 접속 시 status ok 확인



### 3 프론트 빌드와 로컬 실행

백엔드를 켜 두고 새 터미널에서 실행합니다. 아래 폴더명과 명령을 실제 제출 내용에 맞게 수정하세요.

```sh
cd frontend-vue
npm install
npm run build
npm run preview

```

* 최종 확인 주소: `http://127.0.0.1:4173`


* 개발 중 실행 (참고): `npm run dev`, `http://127.0.0.1:5173`
* API 연결 방식과 포트 변경 방법: 빌드된 프론트 프리뷰 화면에서 백엔드(8000)와 정상 통신 확인


* 종료: 프론트와 백엔드를 실행한 각 터미널에서 `Ctrl + C`
* 코드 변경 후 재실행: 프론트 `npm run build` 후 프리뷰 재실행, 백엔드 재기동



### 4 테스트 계정과 DB

| 사용자 | 이메일 | 실습용 비밀번호 |
| --- | --- | --- |
| Alice | alice@class.test | Classroom!2026 |
| Bob | bob@class.test | Classroom!2026 |

* SQLite 파일 위치와 자동 생성 방식: `backend-spring/data/board.sqlite` (프로젝트 설정 경로에 자동 생성)


* DB 초기화 방법: 서버 종료 후 해당 `.sqlite` 파일 삭제


* 서버 재시작 후 데이터 유지 확인 결과: 백엔드를 재시작해도 기존 작성된 글과 데이터가 정상 유지됨



위 계정은 과제 전용 테스트 계정입니다. 실제 서비스 계정 정보나 서버 비밀 키를 기록하지 않습니다.

## 구현 완료 범위

| 기능 | 완료 여부 | 주요 파일 또는 함수 |
| --- | --- | --- |
| 공개 목록과 상세 | 완료 | BoardController.java, App.vue |
| 로그인과 JWT 발급 | 완료 | AuthService.java, api.js |
| JWT 검증과 내 정보 조회 | 완료 | AuthService.java, App.vue |
| 글 작성과 작성자 저장 | 완료 | BoardController.java, App.vue |
| 본인 글 수정·삭제 | 완료 | BoardController.java, App.vue |
| 토큰 만료와 로그아웃 | 완료 | auth.js, App.vue |
| 입력 오류와 권한 오류 처리 | 완료 | BoardController.java 등 전역 예외 처리 |

알려진 문제와 미완성 기능: 없음

## 테스트 방법과 결과 요약

* 실행한 검사 명령과 실행 위치: `py tools/check_api.py --mode final` (프로젝트 루트)
* Alice/Bob 권한 구분 확인 결과: 본인 글 수정·삭제 성공(200/204), 타인 글 수정·삭제 시도 시 403 FORBIDDEN 및 데이터 불변 확인
* HTTP 상태와 오류 code 확인: 200·201·204 / 400·401·403·404의 실제 결과 확인 완료 ([구현 보고서 PDF](https://www.google.com/search?q=docs/report.pdf) 참조)
* 응답 헤더와 본문 확인: 201 Location / 보호 API 401 / 204 빈 본문 확인 완료
* 토큰 없음·변조·만료 확인 결과: 401 Unauthorized 정상 반환 확인
* 전체 T01~T17 결과와 증거: 보고서 4장 및 [테스트 증거](https://www.google.com/search?q=docs/evidence/) 폴더 내 스크린샷 참조

## 핵심 구현 설명

본 서비스는 클라이언트(Vue)에서 로그인 요청 시 백엔드(`AuthService.java`)를 통해 JWT(HS256)를 발급받아 보관하고, 이후 API 요청 시 헤더에 토큰을 포함하여 전송합니다. 서버에서는 이를 검증하여 인증된 사용자 정보를 확인하며, 게시글 작성·수정·삭제 요청 시 `BoardController.java`에서 작성자 본인 여부를 엄격하게 서버 권한 검사(타인 글 조작 시 403 Forbidden 반환)하도록 구현하였습니다. 상세 코드 설명은 [구현 보고서 PDF](https://www.google.com/search?q=docs/report.pdf)에 작성되어 있습니다.

## AI 활용 요약과 참고 자료

* AI 사용 여부: 사용
* 도구와 사용 목적: Gemini (설계, 생성, 디버깅, 문장 정리)
* 반영 위치와 직접 수정·검증한 내용: 전체 아키텍처 설계 및 에러 핸들링 검증
* 상세 활용 기록: 보고서 해당 절 참고
* 참고한 수업 자료·외부 코드·공식 문서: `06주차_JWT_로그인과인증.html`

## 제출 확인

* [✔] README의 안내 문구를 실제 내용으로 교체했다.
* [✔] 새 폴더에 clone하여 README만으로 설치·환경설정·빌드·로컬 실행했다.


* [✔] 4173번 빌드 화면에서 API 연결과 전체 필수 기능을 확인했다.


* [✔] GitHub에서 보고서와 증거 링크가 열린다.
* [✔] 교수자가 저장소를 열람·복제할 수 있다.


* [✔] 비밀 키·실제 토큰·DB·node_modules·.venv·target을 커밋하지 않았다.


* [✔] 최종 push 후 저장소 URL과 제출 기준 커밋 SHA를 LMS에 제출했다.

```

```
