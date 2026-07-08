# hyu-batch

건설현장 IoT 데이터 배치 처리 시스템. 건설장비(불도저·굴삭기·모토그레이더·롤러)의 IoT 트래킹
데이터를 집계하여 **생산성 데이터를 외부 API로 전송**하고, **감리(監理) 리포트를 생성**한다.

> 이 프로젝트는 기존 Spring Boot 2.4 / JDK 8 / Maven 프로젝트를
> **Spring Boot 3.3 / JDK 21 / Gradle(Kotlin DSL)** 로 리뉴얼한 버전이다.
> (엔티티·검증된 집계 SQL 은 그대로 재사용, 구조/설정은 개선)

## 기술 스택

| 항목 | 버전 |
|------|------|
| Java | 21 (LTS) |
| Spring Boot | 3.3.5 |
| Spring Batch | 5.x |
| Build | Gradle 8.14 (Kotlin DSL) |
| DB | PostgreSQL + PostGIS (Hibernate 6 spatial) |
| ORM | Spring Data JPA + QueryDSL 5.1 (jakarta) |
| 기타 | Lombok, ModelMapper, commons-lang3, HikariCP |

## 모듈 구성

```
com.sph.hyu.batch
├── common/config          # QueryDSL(JPAQueryFactory), RestTemplate 설정
├── productivity           # [A] 생산성 집계 → 외부 API 전송 (Spring Batch Job)
│   ├── domain(+type)      #   엔티티 / JobStatus·QueueStatus enum
│   ├── dto                #   ProductivityRow, ProductivityApiRequest (record)
│   ├── repository         #   Job/Queue/Schedule + 집계 네이티브쿼리
│   ├── service            #   집계 / API 전송 / Job 실행 launcher
│   ├── batch(+step)       #   Batch 5 Job/Step 설정 + 8단계 Tasklet
│   ├── scheduler          #   DB 폴링(1분) 자동 실행
│   └── web                #   수동 실행 REST API
└── report/gamrir          # [B] 감리 리포트 생성 (스케줄 기반)
    ├── domain             #   감리 리포트 엔티티 (10종)
    ├── repository         #   집계 네이티브쿼리
    ├── service            #   품질/감리 리포트 생성 로직
    └── scheduler          #   cron 트리거 (05:00 / 17:00)
```

### [A] 생산성 전송 배치 (`productivityTransferJob`)

8단계 Tasklet 파이프라인:

| Step | 역할 |
|------|------|
| 1 createJob | Job 이력 생성 |
| 2 aggregate | 장비 유형별 시간당 작업량/기준 달성률 집계 |
| 3 validate | 검증. 데이터 없으면 `NO_DATA` → Step8 분기 |
| 4 loadQueue | 전송 Queue 적재 |
| 5 transfer | 외부 API POST 전송 |
| 6 updateStatus | 성공/실패 건수 반영 |
| 7 retry | 실패분 재시도 (최대 3회) |
| 8 completeJob | Job 완료 처리 |

- **수동 실행**: `POST /batch/productivity/run` `{"prjId":"<uuid>","workDate":"yyyyMMdd"}`
- **자동 실행**: `TB_BATCH_SCHEDULE` 의 `run_time` 도달 시 (스케줄러 1분 폴링)

### [B] 감리 리포트 (`report.gamrir`)

- **05:00** — 전일(D-1) 품질(다짐) 리포트 재생성
- **17:00** — 금일(D) 장비/인력 투입·위험요소·안전 항목 리포트 생성/갱신

## 설정 구조 (dev / prod 분리)

| 파일 | 역할 |
|------|------|
| `application.yml` | **공통** (batch, jpa, server, logging). DB 접속정보 없음 |
| `application-dev.yml` | dev DB (사내 dev, 기본값 있음) |
| `application-prod.yml` | prod DB (계정/비번은 **주입 필수**, 기본값 없음) |

> **프로파일을 반드시 지정해야 한다.** 미지정 시 datasource 가 없어 기동 실패(의도적 — 환경 혼선 방지).

## 빌드 & 실행

```bash
# 빌드 (프로파일 무관 단일 산출물)
./gradlew clean bootJar -x test

# 로컬 실행 — dev
java -jar build/libs/hyu-batch-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 로컬 실행 — prod (계정/비번 환경변수 필수)
DB_USERNAME=... DB_PASSWORD=... \
java -jar build/libs/hyu-batch-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

> JDK 21 필요. (`JAVA_HOME` 을 JDK 21 로 설정)

### Docker (환경중립 이미지 + compose override)

이미지에는 프로파일/비밀을 굽지 않는다. dev/prod 는 compose override 로 나눈다.

```bash
docker build -t localhost:5000/hyu-batch-server:latest .    # 멀티스테이지(gradle→temurin:21-jre)

# dev 배포  (.env.dev 에서 비밀 주입)
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# prod 배포 (.env.prod 에서 비밀 주입)
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

배포 서버에는 `.env.dev` / `.env.prod` 를 각각 배치한다. (`.env.dev.example` / `.env.prod.example` 복사·작성)

## 환경 변수

| 변수 | 설명 | dev 기본값 | prod |
|------|------|-----------|------|
| `DB_URL` | JDBC URL | `...192.168.0.81:5432/dev` | `...172.25.0.21:5432/bim_gis` |
| `DB_USERNAME` | DB 사용자 | `dev_admin` | **필수(주입)** |
| `DB_PASSWORD` | DB 비밀번호 | `dev_admin` | **필수(주입)** |
| `DB_POOL_MAX` | Hikari 최대 커넥션 | `5` | `5` |
| `SERVER_PORT` | 서버 포트 | `8082` | `8082` |
| `PRODUCTIVITY_API_URL` | 생산성 전송 대상 API (미설정 시 전송 skip) | (빈 값) | (빈 값) |
| `SPRING_PROFILES_ACTIVE` | 활성 프로파일 | `dev` | `prod` |

- context-path: `/batch`
- Spring Batch 메타 테이블: `scop.BATCH_*` (`spring.batch.jdbc.table-prefix`)
- Job 부팅 시 자동 실행 안 함 (`spring.batch.job.enabled=false`)

## 실행 프로파일 & CI/CD 배포 흐름

> **중요: 프로파일은 빌드가 아니라 "실행 시점"에 결정된다.**
> `bootJar` 는 프로파일과 무관하게 **동일한 jar 하나**를 만들고, 그 안에 `application.yml` +
> `application-prod.yml` 이 **모두 포함**된다. dev/prod 구분은 실행 시
> `-Dspring.profiles.active=prod` (또는 `SPRING_PROFILES_ACTIVE=prod`) 로 정해진다.
> 따라서 **접속정보를 이미지에 굽지 않고, 배포 시 환경변수로 주입**한다.

개발망·운영망에 **각각 독립된 Jenkins + 레지스트리**가 있고, 두 CI/CD 가 완전히 분리되어 돈다.
(운영은 격리 환경 — 운영 Jenkins 가 운영망 안에서 build→push→deploy 까지 자체 완결)

```
[개발망] develop ─▶ 개발 Jenkins(Jenkinsfile)      → dev 레지스트리 → SSH → dev 서버
                     build → push → compose.dev.yml (.env.dev, 프로파일 dev)

[운영망] main ────▶ 운영 Jenkins(Jenkinsfile-prod)  → 운영 레지스트리 → SSH → 운영 서버
   (격리)            build → push → compose.prod.yml (.env.prod, 프로파일 prod)  ※ 릴리스 때 수동 Build
```

- 이미지는 **비밀이 없는 환경중립** 산출물. dev/prod 는 compose override + 프로파일로만 구분.
- 이미지 태그: `:latest` + `:<빌드번호>` 동시 push → 이전 버전 이미지 보존(롤백 대비).
- 각 배포 서버의 `.env.dev` / `.env.prod`(git 미포함)가 DB 비밀을 공급.

### Jenkins 파이프라인 (환경별 2개)

| 파일 | 사용 Jenkins | 배포 대상 | 프로파일 |
|------|-------------|-----------|----------|
| `Jenkinsfile` | 개발 Jenkins | dev 서버 (SSH) | dev |
| `Jenkinsfile-prod` | 운영 Jenkins | 운영 서버 (SSH) | prod |

각 파이프라인: **Checkout → Gradle bootJar → 이미지 빌드/push → SSH compose 배포**.
브랜치 자동판단 없음 — 각 Jenkins 잡의 **Script Path** 로 자기 파일을 지정한다.

사전 준비:
- **Jenkins 자격증명(SSH)** 등록: 개발 `dev-hyu`, 운영 `prod-hyu` (Username with password).
- **배포 서버**에 `docker/hyu-batch/` 하위로 `docker-compose.yml` + 환경별 override + `.env.{dev,prod}` 배치.
- compose 의 `image` 주소를 **배포 서버가 레지스트리를 부르는 주소**와 일치시킨다.
- 운영 배포 서버 주소는 `Jenkinsfile-prod` 의 `DEPLOY_HOST`(`<PROD_DEPLOY_HOST>`) 를 채운다 — 채우기 전엔 Guard 스테이지에서 배포가 중단된다.
- DB 접속정보는 **Jenkins·이미지에 두지 않고** 배포 서버의 `.env.*` 에만 존재.

### IntelliJ 에서 실행 설정 (Run/Debug Configurations)

`HyuBatchApplication` 실행 구성에서:

1. **JDK**: `Run → Edit Configurations → HyuBatchApplication` — JRE 를 **21** 로
2. **프로파일(필수)**: `Active profiles` 에 `dev` 입력 (미지정 시 기동 실패)
3. **환경변수(선택)**: dev 는 기본값이 있어 생략 가능. prod 로 띄우면 `DB_USERNAME`/`DB_PASSWORD` 필수
   ```
   DB_USERNAME=dev_admin;DB_PASSWORD=dev_admin
   ```
4. Gradle 로 실행 시: `Settings → Build Tools → Gradle → Gradle JVM = 21`

## 기존 대비 개선 사항

- **스택 현대화**: Boot 2.4/JDK8/Maven → Boot 3.3/JDK21/Gradle, `javax` → `jakarta`
- **설정 외부화 + 프로파일 분리**: 평문 DB 접속정보 → 환경변수 주입,
  `application.yml`(공통)·`application-dev.yml`·`application-prod.yml` 로 dev/prod 대칭 분리
  (prod 계정/비번은 기본값 없이 주입 강제)
- **Spring Batch 5**: `JobBuilderFactory`/`StepBuilderFactory` 제거 → `JobBuilder`/`StepBuilder`,
  커스텀 `JobRepository` 설정 → 프로퍼티(`table-prefix`) 로 단순화
- **상태 enum 화**: 문자열 상수 → `JobStatus` / `QueueStatus` enum (오타 방지)
- **DTO record 화**: 가변 DTO → 불변 `record`
- **계층 분리**: 감리 스케줄러의 비즈니스 로직을 Service 로 분리 (스케줄러는 트리거만)
- **DRY**: Job 실행(JobParameters) 로직을 `ProductivityJobLauncher` 로 공통화
- **일괄 저장**: 전송 스텝의 개별 `save` → `saveAll` 배치 저장
- **불필요 스캐폴딩 제거**: 빈 QueryDSL Custom/Impl 인터페이스 제거
```
