[![Maven Test Build](https://github.com/jexxa-projects/MavenTemplate/actions/workflows/mavenBuild.yml/badge.svg)](https://github.com/jexxa-projects/MavenTemplate/actions/workflows/mavenBuild.yml)
[![New Release](https://github.com/jexxa-projects/MavenTemplate/actions/workflows/newRelease.yml/badge.svg)](https://github.com/jexxa-projects/MavenTemplate/actions/workflows/newRelease.yml)

# MavenTemplate
This template can be used to start your own Jexxa application 

## Requirements

*   Java 17+ installed
*   IDE with maven support 
*   [Optional] Docker or Kubernetes if you want to run your application in a container. See [here](README-CICD.md) for more information.   


## Features

*   Build your project as self-contained jar and/or docker image
    
*   Run unit- and integration tests 

*   Predefined CI/CD pipeline for GitHub including automatic dependency updates 
 
## Create new Project from Template

*   In GitHub press `Use this template` (requires GitHub account) or fork the project. If you do not have a GitHub account you can just clone the repository. 

*   Enter a `project name` for the repository. This template uses following convention:
    *   Project name should be written in camel case notation, such as `MavenTemplate`
    *   Project name of the repository is equal to the name of the java application

## Build the Project

*   Checkout the new project in your favorite IDE

*   Without running integration tests:
    ```shell
    mvn clean install 

    java -jar target/maventemplate-jar-with-dependencies.jar
    ```

## Start Developing your Project

### Adjust Project Name

Rename `MavenTemplate` to your own applications name, as described [here](README-ProjectName.md). 

### Set up the CI/CD Pipeline  

In order to continuously build and deploy your application, configure your CI/CD pipeline as described [here](README-CICD.md).

### Cleanup Readme

After successfully set up your new project, you should clean up the text of README as described [here](https://www.makeareadme.com)    