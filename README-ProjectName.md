# Adjust Project Name 

*   Refactor/Rename file `JLegMed.java` into `<ProjektName>.java` within your IDE

*   Refactor/Rename the GroupId (directory) `io.jexxa.jlegmed` into `com.github.<your-github-account>` for example within your IDE

*   Adjust all sections marked with TODO (and remove TODO statement) in : 
    *    [pom.xml](pom.xml) 
    *    [docker-compose.yml](deploy/docker-compose.yml)

*   In README.md and README-ProjectName.md:
    *   Search/replace (case-sensitive) `JLegMed` by `<ProjectName>`
    *   Search/replace (case-sensitive) `jlegmed` by `<projectname>`
    *   Adjust the badges (first two lines)

*   Adjust release version
    ```shell
    mvn versions:set -DnewVersion='0.1.0-SNAPSHOT'
    ```


