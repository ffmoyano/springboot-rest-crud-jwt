### Rest API example with CRUD and JWT auth

This app is mainly made for quick reference purposes but could be used as a quickstart with a bit of cleanup.

The following libraries are defined in the pom.xml:  
* **Spring JPA**: for database persistence of entities.
* **Spring Validation**: to check that DTO objects sent to the app are valid.
* **Spring Security**: to protect endpoints and manage authentication and authorization.
* **com.auth0.java.jwt**: to generate JWT tokens
* **org.modelmapper.ModelMapper**: to easily convert between DTOs and JPA entities.
* **org.apache.commons.commons-lang3**: contains convenient utility classes for data manipulation.
* **org.mariadb.jdbc.mariadb-java-client**: the database driver, change the dependency if other database used.

Also, the app uses **BcryptPasswordEncoder** for password encryption and **slf4j** for logging, both of them can be changed in
the **configuration.BeanConfiguration** class.

The app has its **CORS** headers defined with all the urls allowed by default, this can be changed in the **configuration.WebConfiguration** class

The authorization and authentication are managed by Spring Security with UserDetails and two filters,  
**filter.CustomAuthenticationFilter**, which extends **UsernamePasswordAuthenticationFilter** and  
**filter.CustomAuthorizationFilter**, which extends **OncePerRequestFilter**.  
The **UserDetails.loadUserByUsername** method is implemented in the **service.UserService** class.

Although the Token entity has a refreshToken string define, the refresh process is not implemented in the app as it doesn't require  
anything specific of Spring. If you wish to implement it you could do so in the *catch(TokenExpiredException e)* in the CustomAuthorizationFilter.

For security reasons the **application.properties** is not added to the repository, so here are listed the properties used:


spring.datasource.url=yourdatabaseconnectionstring  
spring.datasource.username=yourusername  
spring.datasource.password=yourpassword  
spring.datasource.driver-class-name=databasedriver  
spring.jpa.show-sql=true  
spring.jpa.hibernate.ddl-auto=update  
spring.jpa.properties.hibernate.dialect=yourdialect  
variables.jwtSecret=yourjwtsecret