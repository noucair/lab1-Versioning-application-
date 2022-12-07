#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
     remote: 'https://github.com/noucair/jenkins-shared-library',
     credentialsId: 'github-code'
    ]
)
pipeline {
    agent any
    tools {
        maven 'Maven'
    }
   
    stages {
        stage("increment version"){
                steps {
                    script {
                        echo 'increment version'
                       sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"

                    }
                }
            }

        stage('build app') {
            steps {
               script {
                  echo 'building application jar...'
                  buildJar()
               }
            }
        }
        stage('build image') {
            steps {
                script {
                   echo 'building docker image...'
                   buildImage(env.IMAGE_NAME)
                   dockerLogin()
                   dockerPush(env.IMAGE_NAME)
                }
            }
        }


        stage('deploy') {
            steps {
                script {
                   echo 'deploying docker image to EC2...'
                   def shellCmd = "bash ./shellscript.sh ${IMAGE_NAME}"
                   sshagent(['ec2-server-key']){
                       sh "scp -o StrictHostKeyChecking=no shellscript.sh ec2-user@IP:/home/ec2-user"
                       sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ec2-user@IP:/home/ec2-user"
                       sh "ssh -o StrictHostKeyChecking=no ec2-user@35.180.138.199 ${shellCmd}"
                   }
                }
            }
        }
          stage('commit version update') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'github-code', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        // git config here for the first time run
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "jenkins"'
                        sh 'git status'
                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/noucair/lab1-Versioning-application-"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push -u origin main'
                    }
                }
            }
        }

    }
}
