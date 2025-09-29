FROM alpine:3

ARG TARGETOS
ARG TARGETARCH

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY target/docsource-$TARGETOS-$TARGETARCH/docsource-* /app/docsource

RUN apk update \
    && echo -e "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource /app/docsource \
    && apk upgrade \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["docsource"]