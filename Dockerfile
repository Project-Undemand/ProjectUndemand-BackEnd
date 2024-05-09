# Temurin 17 이미지를 사용합니다.
FROM eclipse-temurin:17-jdk

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 호스트 머신의 /home/ubuntu/pjundmd-code-deploy/build/libs/ 디렉토리에서
# 모든 jar 파일을 Docker 이미지의 현재 작업 디렉토리(/app)로 복사합니다.
COPY /home/ubuntu/pjundmd-code-deploy/build/libs/*.jar app.jar

# Docker 컨테이너가 시작될 때 실행될 커맨드를 정의합니다.
CMD ["java", "-jar", "app.jar"]
