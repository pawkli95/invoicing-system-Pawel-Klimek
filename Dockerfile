FROM gradle
EXPOSE 8080
COPY . .
RUN gradle build
ENTRYPOINT ["java","-jar","app/build/libs/app.jar"]


