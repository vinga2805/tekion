# Solution
## One time setup automation
### Requirements
- ansible version > 2.9
- Route53 entry vinga.tk
- Kubernets 3 node cluster.(Tested on AWS KOPS version 
### steps

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


## Continous Integration and Deployment
### Requirements
- Jenkins server with below plugins installed
  - Docker pipeline plugin
  - Github Authentication
- Maven with version 

