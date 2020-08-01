# Solution
## One time setup automation
### Requirements
- ansible version > 2.9
- Route53 entry vinga.tk
- Kubernets 3 node cluster.(Tested on AWS KOPS version 
### steps:

``` git clone https://github.com/vinga2805/tekion.git ```

``` ansible-playbook fullstack.yml```
### Whats included ?
- 3 nodes Elasticsearch cluster for for storing application logs
- Kibana Dashboard for logs visulatization.
  - Index pattern app-logs*
- Prometheus for metrics and Alerting
- Grafana Dashboard for Application and Infrastructure monitoring with inbuilt 2 Dashboards.
   - jmx-prometheus-exporter (Application with jmx monitoring)
   - mysqld-prometheus (Mysql Monitoring)
- MysqlDB for storing the user and audit data.
- Tekion application for creating DynamoDB via UI and template.

### Output
- Once the playbook run successfully below urls will be automatically configured in r53 with nginx ingress.
- http://prometheus.vinga.tk
- http://grafana.vinga.tk
- http://kibana.vinga.tk
- http://tekion.vinga.tk/teslaDyDB


## Continous Integration and Deployment
### Requirements
- Jenkins server with below plugins installed
  - Docker pipeline 
  - Github Authentication
  - pipeline
- Maven with version 3.6.0
- Java openjdk 1.8.0_252

### Steps:
- Create a Jenkins pipeline Job and with below Jenkinsfile in "pipeline script from SCM"
  - app/Jenkinsfile
  
### Whats included ?
- Dockerfile for application.
- Dockerfile for fluentbit which will be running as a sidecar.
- Jenkinsfile to perform CI and CD
- update_image.py for updating the image in helm chart.

### What does it do?
- Clone the repo
- Build the jar file with maven
- Build the Docker image and names it accoring to the environment.
  - SNAPSHOT --> ${pomVersion}-snapshot.${gitBranch}.${shortCommit}
  - release  --> ${pomVersion}
- Push the built image with dockerhub with my credentials.
- Remove the image from Jenkins box
- Clone the same repo in subdirectory values-files
- Update the tag in the values.yaml 
- Bump up the chart version
- Deploys the application via Helm if its first time otherwise it upgrades the helm chart
- Perform healthcheck of the application
- If the Healthcheck passes then it will push the changes in github
- If the Healthcheck fails it performs rollback via helm.






