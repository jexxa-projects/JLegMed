# Adjust Project Name 

*   Refactor/Rename file `MavenTemplate.java` into `<ProjektName>.java` within your IDE

*   Refactor/Rename the GroupId (directory) `io.jexxa.maventemplate` into `com.github.<your-github-account>` for example within your IDE

*   Adjust all sections marked with TODO (and remove TODO statement) in : 
    *    [pom.xml](pom.xml) 
    *    [docker-compose.yml](deploy/docker-compose.yml)

*   In README.md and README-ProjectName.md:
    *   Search/replace (case-sensitive) `MavenTemplate` by `<ProjectName>`
    *   Search/replace (case-sensitive) `maventemplate` by `<projectname>`
    *   Adjust the badges (first two lines)

*   Adjust release version
    ```shell
    mvn versions:set -DnewVersion='0.1.0-SNAPSHOT'
    ```

*   [Optional] Build a docker image via ['New Release' GitHub-Actions](https://github.com/jexxa-projects/MavenTemplate/actions/workflows/newRelease.yml) 

