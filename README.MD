# BeeSpace

![BS_Logo500x181](https://github.com/CreaviSpace/BeeSpace-FrontEnd/assets/97121074/27534123-e13a-4cc3-a9d6-de4ab5407cf6)

# BeeSpace

"개발자들을 위한 협업과 성장의 장, 비스페이스(BeeSpace)!

비스페이스는 프로젝트 공유, 팀원 모집, 다양한 기술 커뮤니티 및 피드백 기능을 제공하는 통합 개발자 커뮤니티 사이트입니다. 자신의 프로젝트를 소개하고, 협업의 기회를 찾을 수 있는 기회의 공간을 제공합니다.

## 프로젝트 소개

비스페이스는 프로젝트 공유, 팀원 모집, 다양한 기술 커뮤니티 및 피드백 기능을 제공하는 개발자 커뮤니티 사이트입니다. 개발자들이 열정을 담아 만든 프로젝트를 소개하고, 협업 기회를 찾을 수 있는 공간을 제공합니다.

프로젝트 페이지를 통해 서로 프로젝트를 공유하고, 피드백을 주고 받으며 영감을 얻을 수 있습니다. 모집 페이지에서는 팀 프로젝트에 참여하거나 협력할 팀원을 찾을 수 있습니다. 커뮤니티 페이지에서는 여러 기술 스택과 그 이외의 고민에 대해 공유하고, 새로운 아이디어와 해결책을 찾을 수 있습니다.

더불어, 개발자들이 서로의 작업물에 대한 솔직한 피드백을 주고받을 수 있는 피드백 기능을 강화하고 있습니다. 이를 통해 실력 향상과 성장을 위한 소중한 장소로 사용할 수 있습니다.

## 프로젝트 배포 주소 및 홈페이지 이미지

https://beespace.vercel.app/

![image](https://github.com/user-attachments/assets/202da9f8-fae8-4080-8ec3-379b161dbfa9)

## 주요 기능

⭐ Oauth2Login(소셜 로그인) + JWT발급

- Oauth2Login 동작을 백엔드에서 모든 책임을 갖도록 구현했습니다.
- 로그인 페이지 요청 -> 코드 발급 -> Access토큰 -> 유저 정보 획득-> JWT(Access, Refresh) 발급

⭐ 게시글, 댓글 CRUD + Spring Data JPA

- 로그인 사용자의 권한에 따라 게시글 및 댓글 CRUD를 사용할 수 있도록 구현했습니다.
- Spring Data JPA의 영속성 컨텍스트를 활용하여 save 및 update 코드 길이를 축소할 수 있었습니다.
- fetch join 및 batch size를 활용하여 조회 쿼리의 N+1 문제에 대해 해결할 수 있었습니다.

⭐ S3 파일 업로드

- 게시글 작성시 AWS S3를 활용한 이미지 업로드를 가능하도록 구현했습니다.
- 주기적으로 사용되지 않는 이미지 파일을 삭제하도록 구현했습니다.

⭐ 북마크, 좋아요 기능

- 로그인 사용자의 권한에 따라 북마크, 좋아요 기능을 사용 할 수 있도록 구현했습니다.

⭐ 통합검색 기능

- 다양한 종류의 게시글을 검색 조건에 따라 통합 검색할 수 있습니다.
- QueryDSL을 활용해서 타입 세이프한 코드로 작성하였고, 검색조건에 따른 동적 쿼리를 구현했습니다.

⭐ 배포 CI/CD

- 팀 리포지토리의 develop 브런치에 push 상태가 일어나면 docker image가 저장되고 AWS EC2에 배포됩니다.



## 기술스택
`JAVA 17`, `Gradle`, `SpringBoot 3.2.1` , `Spring Data JPA`, `Spring Secruity 6.2.1`, `MySQL`, `AWS`, `S3`, `JWT`, `QueryDsl`

## ERD
ERD사이트 주소 : https://www.erdcloud.com/d/YYHSkLxLQYCqEvQsT
![image](https://github.com/user-attachments/assets/c2ae3ffd-9259-4796-a7f3-54791a813314)

## 프로젝트 아키텍쳐
![image](https://github.com/user-attachments/assets/6ea5d407-7ace-4d5c-b4d1-1fdfbba029e3)

## API 문서

## 멤버

| FE | FE |
| :---: | :---: |
| <a href="https://github.com/springhana"><img src="https://avatars.githubusercontent.com/u/97121074?v=4" alt="profile" width="140" height="140"></a> | <a href="https://github.com/pionoiq"><img src="https://avatars.githubusercontent.com/u/101159509?v=4" alt="profile" width="140" height="140"></a> |
| [김성환](https://github.com/springhana) | [장다은](https://github.com/pionoiq) |

| BE | BE |
| :---: | :---: |
| <a href="https://github.com/YuHoSeong"><img src="https://avatars.githubusercontent.com/u/82141580?v=4" alt="profile" width="140" height="140"></a> | <a href="https://github.com/K-KY"><img src="https://avatars.githubusercontent.com/u/66647057?s=96&v=4" alt="profile" width="140" height="140"></a> |
| [유호성](https://github.com/YuHoSeong) | [김규영](https://github.com/K-KY) | 

---

### 커밋 컨벤션

|태그이름|설명|
|----|----|
|Feat|새로운 기능을 추가할 경우|
|Fix|	버그를 고친 경우|
|Design|CSS 등 사용자 UI 디자인 변경|
|!BREAKING CHANGE|	커다란 API 변경의 경우|
|!HOTFIX|	급하게 치명적인 버그를 고쳐야하는 경우|
|Style|	코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우|
|Refactor|	프로덕션 코드 리팩토링|
|Comment|	필요한 주석 추가 및 변경|
|Docs|	문서를 수정한 경우|
|Test|	테스트 추가, 테스트 리팩토링(프로덕션 코드 변경 X)|
|Chore|	빌드 태스트 업데이트, 패키지 매니저를 설정하는 경우(프로덕션 코드 변경 X)|
|Rename|	파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우|
|Remove|	파일을 삭제하는 작업만 수행한 경우|
