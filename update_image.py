#!/usr/bin/python3

import yaml
import sys
import subprocess
import os
def update_image_tags(filePath, imageTag):

    for _file in filePath:
        # Loading the yaml data from values file
        subprocess.Popen("cd values-files && git checkout master", shell=True).communicate()
        with open(_file, 'r') as yamlFile:
            data = yaml.load(yamlFile, Loader=yaml.FullLoader)

        # Checking if appName is present in the file picked

            data["image"]["tag"] = imageTag

            # Dumping the updated image tag
            with open(_file, "w") as yamlFile:
                yamlFile.write(yaml.dump(data, sort_keys=True))
            print("Bumping up the chart version!!")
            os.system('pybump bump --file values-files/helm-charts/tekion-app/Chart.yaml --level patch')
            pushToBitBucket()
            print ("Updated the image")
            return

def pushToBitBucket():
    # repo       = Repo('values-files')
    try:
        commands = """
            cd values-files && \
            git status && \
            git branch && \
            git diff && \
            git add . && \
            git config --global user.email 'you@example.com' && \
            git config --global user.name 'jenkins@zeta.tech' && \
            git commit -m 'Commit from JenkinsBot to update ValuesFile' && \
            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/tekion.git
            """
        process = subprocess.Popen(
            commands,
            shell = True,
            stdout = subprocess.PIPE
        )
        stdout, stderr = process.communicate()
        if stderr:
            print ("Err.. {}".format(stderr.decode('utf-8')))
            sys.exit(-1)
        else:
            print ("Done!")
    except Exception as E:
        print (E)
        sys.exit(-1)

if __name__ == "__main__":
    if (len(sys.argv)) == 0:
        print ("imageTag missing!")
        sys.exit(-1)

    imageTag         = sys.argv[1].split(':')[1]

    filePath = [ "values-files/helm-charts/tekion-app/values.yaml"]

    update_image_tags(filePath, imageTag)
