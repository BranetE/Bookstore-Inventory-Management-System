FROM gradle:8.6-jdk21
COPY common/src/main ./common/src/main
COPY common/build.gradle ./common

COPY server/src/main ./server/src/main
COPY server/build.gradle ./server

COPY build.gradle settings.gradle ./

EXPOSE 9090

CMD ["gradle", "bootRun", "x-test"]

