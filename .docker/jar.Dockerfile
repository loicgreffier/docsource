FROM eclipse-temurin:21-jre

COPY target/docsource.jar /app/docsource.jar

RUN echo "#!/bin/sh\n\njava -jar /app/docsource.jar \"\$@\"" > /usr/bin/docsource \
    && chmod +x /usr/bin/docsource

ENTRYPOINT ["/bin/bash"]