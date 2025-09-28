FROM alpine:3.21

# Enable multi-platform build. When enabled, Docker will copy the binary built for the target platform.
ARG MULTI_PLATFORM_BUILD=false

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY ${MULTI_PLATFORM_BUILD:+target/docsource-$TARGETOS-$TARGETARCH/docsource}${MULTI_PLATFORM_BUILD:-target/docsource} /app/docsource

RUN apk update \
    && echo -e "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource \
    && apk upgrade \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["docsource"]