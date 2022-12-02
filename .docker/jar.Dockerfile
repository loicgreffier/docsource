FROM eclipse-temurin:17.0.3_7-jre

RUN echo "#!/bin/sh\n\njava -cp @/app/jib-classpath-file io.lgr.docsource.DocsourceApplication \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["/bin/bash"]