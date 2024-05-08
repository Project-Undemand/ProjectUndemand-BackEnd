<div align="center">
<img src="https://capsule-render.vercel.app/api?type=waving&color=6FC7E1&height=240&section=header&text=PUSHOP&fontColor=ffffffCC&fontSize=60&fontAlignY=35&desc=Project%20Undemand&descSize=20&descAlign=70&descAlignY=53" />
</div>

# Introduction 
> 개발기간 : 2024.03.  
> 배포 :  
- [Front-End](https://github.com/Project-Undemand/ProjectUndemand-FrontEnd)   
- [DOCS](https://rhetorical-cilantro-7e4.notion.site/9e99d5e3a72247f29ee5543e98cf41b2?v=3dec1f93ce8746f988461e90d42f287e)   
<details>
<summary>ERD</summary>
<div markdown='1'></div>

</details>

<details>
<summary>API</summary>
<div markdown='1'></div>

https://documenter.getpostman.com/view/26963254/2sA3JJ7hks#15a6dae6-2464-42c7-9141-d07e19049b92

</details>

### Stacks

|Category|Stacks|
|---|---|
| Backend | <img src="https://img.shields.io/badge/java 17 -007396?style=for-the-badge&logo=java&logoColor=white">  <img src="https://img.shields.io/badge/Spring Boot 3.2.3 -6DB33F?style=for-the-badge&logo=springboot&logoColor=white">  <img src="https://img.shields.io/badge/Spring Security 6.2.2 -6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">  <img src="https://img.shields.io/badge/gradle -02303A?style=for-the-badge&logo=gradle&logoColor=white">  <img src="https://img.shields.io/badge/junit5 -25A162?style=for-the-badge&logo=junit5&logoColor=white">  <img src="https://img.shields.io/badge/Redis 3.2.3 -DC382D?style=for-the-badge&logo=redis&logoColor=white">  <img src="https://img.shields.io/badge/JPA -007396?style=for-the-badge&logo=java&logoColor=white">  <img src="https://img.shields.io/badge/QueryDsl 5.0.0 -007396?style=for-the-badge&logo=java&logoColor=white">  |
| Frontend | <img src="https://img.shields.io/badge/react -61DAFB?style=for-the-badge&logo=amazonec2&logoColor=white">   
| Server | <img src="https://img.shields.io/badge/amazonec2 -FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">  <img src="https://img.shields.io/badge/axios -5A29E4?style=for-the-badge&logo=axios&logoColor=white"> 
| Database | <img src="https://img.shields.io/badge/mysql 8.0.33 -4479A1?style=for-the-badge&logo=mysql&logoColor=white">  




# Features

### 회원기능

#### 회원가입, 로그인

- Spring Security JWT

#### 마이페이지

- 



### 관리자 페이지

#### 상품 관리

- 각 서비스 메서드에 사용자 역할별 권한 검증 커스텀 어노테이션 생성  
- 상품 등록
- 카테고리 등록 : 2계층 (상위 카테고리 / 하위 카테고리)  
- 상품 관리  
  - 상품 정보 + 카테고리 + 옵션(색상, 사이즈)  
  



### 사용자 페이지

#### 상품 조회

<details>
<summary>동적 쿼리로 검색 및 필터링 구현</summary>
<div markdown='1'></div>

- QueryDsl을 활용하여 사용자가 원하는 다양한 정렬 및 필터링 옵션에 따라 상품 리스트를 동적으로 조회할 수 있도록 했습니다.
- 동시에 페이징을 적용하고 fetch join을 적절히 사용해 조회 성능을 개선하였습니다.

</details>




# Troubleshooting

<details>
<summary>전체보기</summary>
<div markdown='1'></div>

https://rhetorical-cilantro-7e4.notion.site/5a4c766d6c144bb1bc02697b7f98484f?v=23410d6a397d4e35b63ffd42a86848aa&pvs=74

</details>



# Changelog


# Service Architecture 







Wireframe
------

