# ---------- Stage 1: Build ----------
FROM maven:3.9.8-amazoncorretto-21 AS build

# Dùng thư mục /build riêng để tránh lỗi ghi file
WORKDIR /build

# Copy file pom.xml và src
COPY pom.xml .
COPY src ./src

# Build source (clean + skip test)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM amazoncorretto:21.0.4

WORKDIR /app

# Copy file .jar đã build từ stage build
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

# Lệnh chạy app Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
