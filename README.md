# java-miniproject-2026
Java 웹개발자 2026 미니프로젝트1

- 기간 : 2026.04.28 ~ 05.08 (5일간)

## 프로젝트 관리 사이트

- Notion : Github 연동가능. 프로젝트 작성 편리함
- Jira : 폐쇄성 프로젝트 관리 사이트. 10명까지는 무료
  - [https://www.atlassian.com/ko](https://www.atlassian.com/ko)

- Github Repository만 사용
  - `이슈` - 팀원에게 문제점, 개발건 역할분담
  - `위키` - 프로젝트 위키 문서 생성
  - ~~액션~~ - 프로젝트 소스 자동 배포 등
  - `프로젝트` - 보통 칸반을 사용, 이슈 탭에 작성한 이슈들이 바로 디스플레이

- Branch 로컬 리포지토리로 클론은 명령어 추천

  ```powershell
  > git clone --branch [브랜치명] [브랜치존재하는_깃허브리포지토리_주소]
  ```

- Github 저장소 종류
  - main : 배포용 완성된 리포지토리
  - branch : 개발용(팀의 경우 각자 개발하는 리포지토리)

- Branch 생성 작업 후 합치는 방법
  1. main ▼ 클릭 > View all branches 선택
  2. New branch 클릭 이름 적고 생성
  3. 각자 작업(같은 소스를 건드리는 작업 피할 것)
  4. Pull requests > New Pull request
  5. 충돌발생 시 직접 소스해결 Resolve
  6. Create Pull request 진행
  7. Branch 작업 완료 후 branch 삭제버튼 생성 > 클릭하면 branch 리포지토리 삭제됨
  8. View all branches에서 수동 삭제 가능

## 요구사항 정의

[링크](./docs/PRD.md)

## DB 설계

[링크](./docs/DB.md)

## 화면 설계

[링크](./docs/Design.md)

### (임시) 미니 프로젝트 주제

 중고거래 플랫폼
  - 게시판을 변형, 이미지가 리스트에 나오도록
  - 상품등록(이미지), 가격/카테고리, 판매상태, 댓글

 커뮤니티 + 거래결합형
  - 자유게시판 + 거래게시판
  - 댓글/좋아요~

  

https://github.com/JHJH1289/jhdrve 참고용!
