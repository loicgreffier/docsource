FROM ubuntu:latest

RUN echo "#!/bin/sh\n\n/app/docsource \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["/bin/bash"]