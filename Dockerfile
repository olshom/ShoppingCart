FROM maven:3.9-eclipse-temurin-21
LABEL authors="olga"
WORKDIR /app/
COPY . /app/
RUN mvn package
CMD ["java", "-jar", "target/SW_week1.jar"]