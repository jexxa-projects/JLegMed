# CI/CD process 

This README provides a guideline how to set up and use a CI/CD pipeline based on GitHub actions.

Within the context of this template, CI/CD means: 
*   __Continuous Integration:__ Refers to automatic build, test and merge process. The merge process is only automated for dependency updates of patch and minor versions. 
*   __Continuous Delivery:__ Refers to automatically tag and release a version to a repository. This means building a new docker image and uploaded it to [GitHub Container Registry](https://ghcr.io).

To avoid misunderstandings we do not abbreviate the term __continuous deployment__ which in this context describes the automatic deployment into production.  

## Set up your CI/CD Process

*   Make your repository public to use ghcr or ensure that you have billing plan including access to ghcr.
  
*   Setup action secret for auto merge version updates from dependabot:

    *   Create a personal access token (PAT), as described [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) with `public_repo` access enabled.

    *   Set up a secret for GitHub actions called `MERGE_ME_SECRET` as described [here](https://docs.github.com/en/actions/security-guides/encrypted-secrets?tool=webui#creating-encrypted-secrets-for-a-repository) that includes the generated PAT

*   To build a new release via GitHub actions enable write access for the  `GITHUB_TOKEN` as described [here](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/enabling-features-for-your-repository/managing-github-actions-settings-for-a-repository#configuring-the-default-github_token-permissions).


## Using CI/CD process

The CI/CD process is based on following GitHub actions that are either started manually or automatically:  

*   [mavenBuild.yml](.github/workflows/mavenBuild.yml):
    *   __Description:__ Builds the project after each push
    *   __Started:__ Automatically and manually   

*   [newRelease.yml](.github/workflows/newRelease.yml):
    *   __Description:__ Create a new release using maven via GitHub web page
    *   __Started:__ Manually only 

*   [autoMerge.yml](.github/workflows/autoMerge.yml):
    *   __Description:__ Automatic merge of dependency updates with new patch or minor versions of dependencies from Dependabot. See https://github.com/ridedott/merge-me-action for more information.
    *   __Started:__ Automatically only

*   [dependabot.yml](.github/dependabot.yml):
    *   __Description:__ Check for new dependencies and create a pull request
    *   __Started:__ Automatically only (each day)
  
## Deployment 

In the following we assume a docker-swarm setup which is a typical starting point for clustering your container.
In addition, it can be easily run and maintained on your developing machine. 

### Docker-Stacks

*   [developerStack.yml](deploy/developerStack.yml)
    *   Includes all required dependencies to run the application during development on your local machine

*   [docker-compose.yml](deploy/docker-compose.yml)
    *   Stack to run the application as stack in your production environment

### Deploy Stack 

In order to deploy the stack, you can use following command from your checkout directory. 
```shell
docker stack deploy --compose-file ./deploy/docker-compose.yml jexxatemplate
```

### Continuous Deployment 

If you want start using continuous deployment mechanism which automatically deploys new versions into
production, we recommend to start using some tools such as [Portainer](https://www.portainer.io). This 
container management platform provides unified frontend to docker, docker-swarm and kubernetes. Therefore, 
it is a good starting point.

In this file, we highlight only the following two aspects:

* **Zero downtime deployment:** For zero downtime deployment we configure rolling updates for the application as you can see
  in the [docker-compose.yml](deploy/docker-compose.yml). The most interesting part here are the following lines:
  ```yml  
  healthcheck:
     test: ["CMD-SHELL", "wget -nv -t1 --spider 'http://localhost:7500/BoundedContext/isRunning/'"]`
  ```
  The return value of this call is only true, if the application could be successfully started. This also includes all
  connections to infrastructure components, such as requesting a REST port, or setting-up a connection to a database. In  
  case of a rolling update, a new release is only deployed, if this method returns true within the defined period of time.
  If this call fails, the old version remains active.

