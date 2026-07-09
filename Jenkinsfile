// 개발(dev) 전용 파이프라인. 개발 Jenkins 가 이 파일을 사용한다 (Script Path: Jenkinsfile).
//   Checkout → Gradle bootJar → 이미지 빌드/push(dev 레지스트리) → SSH 로 dev 서버에 compose 배포
pipeline {
    agent any

    environment {
        REGISTRY    = '192.168.0.90:5000'              // dev 레지스트리 (dev 서버와 동일 호스트)
        IMAGE       = "${REGISTRY}/hyu-batch-server-test"
        DEPLOY_HOST = '192.168.0.90'                    // dev 배포 서버
        DEPLOY_PORT = '22'
        DEPLOY_CRED = 'hyu-dev-ssh'                     // Jenkins 자격증명 ID (Username with password: hyu/hyu)
        COMPOSE     = 'docker/hyu-batch/docker-compose.yml -f docker/hyu-batch/docker-compose.dev.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build (Gradle)') {
            steps {
                sh './gradlew clean bootJar -x test'
            }
        }

        stage('Build & Push image') {
            steps {
                // latest + 빌드번호 태그 동시 push (롤백 대비)
                // --provenance=false: 사설 레지스트리에서 OCI 인덱스 대신 단일 매니페스트로 push (공유-매니페스트 삭제 문제 방지)
                sh "docker build --provenance=false -t ${IMAGE}:latest -t ${IMAGE}:${env.BUILD_NUMBER} ."
                sh "docker push ${IMAGE}:latest"
                sh "docker push ${IMAGE}:${env.BUILD_NUMBER}"
            }
        }

        stage('Deploy (dev)') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: env.DEPLOY_CRED,
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASS')]) {
                    script {
                        def remote = [:]
                        remote.name          = 'dev'
                        remote.host          = env.DEPLOY_HOST
                        remote.port          = env.DEPLOY_PORT.toInteger()
                        remote.user          = env.SSH_USER
                        remote.password      = env.SSH_PASS
                        remote.allowAnyHosts = true

                        sshCommand remote: remote, command: "docker compose -f ${env.COMPOSE} pull"
                        sshCommand remote: remote, command: "docker compose -f ${env.COMPOSE} up -d"
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f'
        }
    }
}
