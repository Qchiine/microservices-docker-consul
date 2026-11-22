# ============================================================
# SERVICE-GATEWAY (port 8888)
#
# Build multi-stage :
#   stage "builder" : image Maven + JDK 17 -> compile et produit le JAR
#   stage "runtime" : image JRE 17 legere    -> execute le JAR
#
# Note : l'enonce propose "openjdk:17-jdk-alpine", mais cette image a ete
# retiree de Docker Hub (le depot officiel "openjdk" est archive et ses tags
# ont disparu). On utilise donc eclipse-temurin:17-jre-alpine, qui est
# l'image officielle recommandee par la fondation Eclipse/Adoptium.
# ============================================================

FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
# Utilisateur non root : bonne pratique securite
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=builder /build/${JAR_FILE} gateway-service.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8888
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/gateway-service.jar"]
