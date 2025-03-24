FROM ubuntu:24.04

LABEL org.opencontainers.image.authors="io.github.loicgreffier"

COPY target/docsource /app/docsource

RUN echo "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["docsource"]