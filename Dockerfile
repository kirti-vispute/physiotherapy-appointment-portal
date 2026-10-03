FROM eclipse-temurin:21-jre

WORKDIR /app

# The file database lives outside the image so it survives container replacement.
RUN mkdir -p /app/data && chown -R 10001:10001 /app
COPY --chown=10001:10001 target/physio-portal-1.0.0.jar /app/app.jar

USER 10001:10001
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
