shaman-api
==========

Devops 플랫폼 API 


STS-3.5.0 + Tomcat v7.0 Setting
----------------
- Pre Requirement
  - STS 3.5
  - Tomcat-Server v7.0
  - Gradle Plugin
1. Project Properties > Project Facets
  - Check Dynamic Web Module 3.0
  - Check Java 1.6 : 
  - Check JavaScript 1.0
2. Project Properties > Deployment Assembly
  - Add > Folder, Source : /src/main/webapp, Deploy Path : /
  - Add > Java Build Path Entries > Gradle Dependencies
3. Windows > Show views > Server 
  - Add Tomcat Web Module
* TODO
  - java 1.7로 하면 tomcat v7.0에서 지원할 수 없다고 나오는데 이부분은 추후 다시 확인
  - Gradle 저장소와 Maven Local 저장소 함께 사용 가능 한지, 되도록 함께 쓰는 방향으로 설정 추후 고민
  