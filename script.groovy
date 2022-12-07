def buildJar() {
    echo "building jar"
    sh 'mvn clean package'
} 

def buildImage() {
    echo "building docker image"
                   withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'password' ,usernameVariable: 'username')]){
                    sh 'docker build -t noucair00/my-repo:$IMAGE_NAME . '
                    sh "echo $password | docker login -u $username --password-stdin"
                    sh "docker push noucair00/my-repo:$IMAGE_NAME"
} 

} 

def deployApp() {
    echo 'deploying the application...'
} 

return this
