FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY target/docsource-*.jar /app/docsource.jar

RUN apk update \
    && echo -e "#!/bin/sh\n\njava -jar /app/docsource.jar \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource \
    && apk upgrade \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["docsource"]