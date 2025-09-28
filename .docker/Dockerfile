FROM alpine:3.21

ARG ARTIFACT_PATH=target/docsource

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY $ARTIFACT_PATH /app/docsource

RUN apk update \
    && echo -e "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource \
    && apk upgrade \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["docsource"]