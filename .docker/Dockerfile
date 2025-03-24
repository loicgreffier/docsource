FROM ubuntu:24.04

COPY target/docsource /app/docsource

RUN echo "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["/bin/bash"]