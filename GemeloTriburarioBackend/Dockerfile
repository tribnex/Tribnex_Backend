# ---------- Etapa 1: compilar ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
# Por si mvnw se guardó con saltos de línea de Windows (CRLF)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

# ---------- Etapa 2: ejecutar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Hora de Ecuador y memoria acotada (el plan gratis de Render tiene 512 MB)
ENV TZ=America/Guayaquil
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=America/Guayaquil -XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]