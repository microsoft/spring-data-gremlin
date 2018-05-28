# How to Build and Contribute
Below are guidelines for building and code contribution.

## Prerequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

## Build from source
To build the project, run maven commands.

```bash
git clone https://github.com/Microsoft/azure-spring-boot.git 
cd azure-spring-boot
mvn clean install
```

## Test

- Run unit tests
```bash
mvn clean install
```

- [Run integration tests]()

- Skip test execution
```bash
mvn clean install -DskipTests
```

## Version management
Developing version naming convention is like `0.1.0-SNAPSHOT`. Release version naming convention is like `2.0.0`. Please don't update version if no release plan. 

## CI
[Travis](https://travis-ci.org/Microsoft/spring-data-gremlin)    
[Codecov](https://codecov.io/gh/Microsoft/spring-data-gremlin)     
[Codacy](https://app.codacy.com/project/Incarnation-p-lee/spring-data-gremlin/dashboard)   

## Contribution
Code contribution is welcome. To contribute to existing code or add a new starter, please make sure below check list is checked.
- [ ] Build pass. Checkstyle and findbugs is enabled by default. Please check [checkstyle.xml](config/checkstyle.xml) to learn detailed checkstyle configuration.
- [ ] Documents are updated to align with code.
- [ ] New starter must have sample folder containing sample code and corresponding readme file.
- [ ] Keep Code coverage for repository >= 90%. Code coverage check is not enabled, as you may need to split feature code and test code in different PR.
