FROM eclipse-temurin:latest

RUN echo "#!/bin/sh\n\njava -cp @/app/jib-classpath-file io.github.loicgreffier.DocsourceApplication \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["/bin/bash"]