RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-20.10.8.tgz \
  && tar xzvf docker-20.10.8.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-20.10.8.tgz


FROM gradle:jdk11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY . /app

RUN gradle build  -x test

# Use AdoptOpenJDK for base image.
# It's important to use OpenJDK 8u191 or above that has container support enabled.
# https://hub.docker.com/r/adoptopenjdk/openjdk8
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM adoptopenjdk/openjdk11:alpine-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/build/libs/*.jar /app.jar

# Run the web service on container startup.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
