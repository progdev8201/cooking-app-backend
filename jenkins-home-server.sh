pipeline {
    agent any

    stages {
        stage('build image push image to Docker Hub CI') {
            steps {
                git branch: 'main',
                credentialsId: 'git-credentials',
                url: 'https://github.com/progdev8201/cooking-app-backend.git'

                sh 'docker system prune -a -f'

                sh "docker login --username orewaprogdev13 --password kulotte2015"

                sh "docker build -t orewaprogdev13/cooking-api ."

                sh "docker push orewaprogdev13/cooking-api"
            }
        }

        stage('deploy app CD') {
            steps {
                sh("""
                docker-compose -f docker-compose-home-server.yml down;
                docker system prune -a -f;
                docker-compose -f docker-compose-home-server.yml up -d;
                """)
           }
        }
    }
}
