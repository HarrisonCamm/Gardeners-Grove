# SENG302 Example Project
basic project using ```gradle```, ```Spring Boot```, ```Thymeleaf```, and ```GitLab CI```.

> This should be your project's README (renamed to `README.md`) that your team will continually update as your team progresses throughout the year.
>
> Update this document as necessary.

## How to run
### 1 - Create application-dev.properties

- From the root directory go to src/main/resources
- Create the file application-dev.properties
- Type the following line
```
geoapify.api.key=YOUR_API_KEY
```
See below for instructions for getting an API key

### 2 - Getting an API key

- Go to https://www.geoapify.com/
- Create an account
- Create a project
- Select "Autocomplete API" under "Choose Geoapify API key"
- Copy and paste the API key into the application-dev.properties file (see above)

### 3 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 4 - Using the application
> You may want to include information here about how to use the application, notably:
> - default user credentials if required
> - link to home/login page
> - disclosing known issues (and workarounds if applicable)
> - ...
> 

## How to run tests
> Once you have some tests written make sure you detail how to run them, especially if there are special requirements.

## Todo (Sprint 1)

- Add team name into `build.gradle`
- Update this README title
- Update this README contributors

## Todo (Sprint 2)

- Update team name into `build.gradle`
- Update this README title
- Update this README contributors
- Set up Gitlab CI server (refer to the student guide on Scrumboard)
- Decide on a LICENSE

## Contributors

- SENG302 teaching team
- Oliver Clark
- Toby Oliver
- Zak Lockett
- Harrison Camm

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
