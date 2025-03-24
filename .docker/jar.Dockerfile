FROM eclipse-temurin:21-jre

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY target/docsource.jar /app/docsource.jar

RUN echo "#!/bin/sh\n\njava -jar /app/docsource.jar \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["docsource"]