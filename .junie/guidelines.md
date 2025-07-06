1. **Build/Configuration Instructions**: 
   - Use Springboot
   - Use log4j, lombok
   - This is modular monolithic application
   - Declare all the mandatory dependencies as final fields and inject them through the constructor.
   - Spring will auto-detect if there is only one constructor, no need to add @Autowired on the constructor.
   - Avoid field/setter injection in production code.
   - Declare Controllers, their request-handling methods, @Configuration classes and @Bean methods with default (package-private) visibility whenever possible. There's no obligation to make everything public.
   - Group application-specific configuration properties with a common prefix in application.properties or .yml.
   - Bind them to @ConfigurationProperties classes with validation annotations so that the application will fail fast if the configuration is invalid.
   - Prefer environment variables instead of profiles for passing different configuration properties for different environments.




2. **Testing Information**:
    - Need to follow TDD approach
    - Later need to use Test containers for integration testing
    - Locally this machine has Postgres
    - Locally this machine has both Podman
    - Use Podman with Testcontainer

3. **Additional Development Information**: 
    - Later need to introduce Camunda
    - Follow SOLID, KISS principles
    - WIll do one step at a time

