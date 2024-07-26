# Team 600 - Gardeners Grove Users
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
weather.api.key=YOUR_OPENWEATHERMAP_API_KEY
weather.api.url=https://api.openweathermap.org/data/2.5/

```
See below for instructions for getting an API key

### 2 - Getting an API key

## Geoapify API Key

- Go to https://www.geoapify.com/
- Create an account
- Create a project
- Select "Autocomplete API" under "Choose Geoapify API key"
- Copy and paste the API key into the application-dev.properties file (see above)

## OpenWeatherMap API Key

- Go to https://openweathermap.org/
- Create an account.
- Navigate to the API keys section.
- Generate an API key.
- Copy and paste the API key into the application-dev.properties file (see above).


### 3 - Setting Credentials in application-dev.properties

- Copy and paste the following lines into the application-dev.properties file
```
# Spring Mail Credentials
spring.mail.username=gardenersgrovenoreply@gmail.com
spring.mail.password=yqzl kzje hkhn ekbl

#Spring Data Source Credentials
spring.datasource.username=sa
spring.datasource.password=password
```
- These properties are to configure the application to work with the mail server and database used.

### 4 - Running the project
We are not deploying on the PROD instance, and we do not have it persisting on their database.

From the root directory ...

1. Open a terminal in the root directory of the project.
2. Run the following command to execute the application:

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 5 - Using the application

#### Default User Credentials
The application comes with several default users created at startup for testing purposes. Here are the credentials:

1. Startup User:
    - Username: startup@user.com
    - Password: Password1!

2. Sarah:
    - Username: sarah@email.com
    - Password: Password1!

3. Inaya Singh:
    - Username: inaya@email.com
    - Password: Password1!

4. Kaia Pene:
    - Username: kaia@email.com
    - Password: Password1!

5. Lei Yuan:
    - Username: lei@email.com
    - Password: Password1!

6. Liam Müller:
    - Username: liam@email.com
    - Password: Password1!

#### Home Page
You can access the application by navigating to [http://localhost:8080/home](http://localhost:8080/home) if you are running locally.

You can access the application by navigating to [https://csse-seng302-team600.canterbury.ac.nz/prod/](https://csse-seng302-team600.canterbury.ac.nz/prod/) if you are running on the PROD instance.

#### Known Issues and Workarounds
> You may want to include information here about any known issues and possible workarounds.

## How to run tests
From the root directory ...

1. Open a terminal in the root directory of the project.
2. Run the following command to execute the tests:

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
```

## Contributors

- Henry Borthwick (hbo50)
- Bella Hill (ihi17)
- Angelica Silva (ams361)
- Aakrista Dahal (ada185)
- Oliver Clark (ocl28)
- Toby Oliver (tol21)
- Zak Lockett (zlo18)
- Harrison Camm (hrc48)
- SENG302 teaching team

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
