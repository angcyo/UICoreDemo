#!/usr/bin/env sh

//Jenkins Pipelines 流水线
//https://www.jenkins.io/zh/doc/book/pipeline/jenkinsfile/

//withCredentials(bindings: [sshUserPrivateKey(credentialsId: credentialsId, keyFileVariable: 'SSH_KEY_FOR_GIT')]) {
//    git url: projectGitUrl, branch: projectBranchName
//}

node {
    def projectBranchName = env.branchName == null ? "master" : env.branchName//主工程仓库分支

    echo "开始编译目录: ${env.WORKSPACE} ${projectBranchName}"
    echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"

    //主工程仓库地址
    def projectGitUrl = "git@github.com:angcyo/UICoreDemo.git"

    //核心仓库地址
    def coreGitUrl = "git@gitee.com:angcyo/UICore.git"
    def coreBranchName = "master" //核心仓库分支
    def corePath = "UICore" //仓库存储路径, 不指定则自动获取

    // 插件仓库地址
    def pluginGitUrl = "git@gitee.com:angcyo/UICoreEx.git"
    def pluginBranchName = "master" //插件仓库分支
    def pluginPath = "UICoreEx"

    //构建任务
    def gradleTask = env.gradleTask == null ? "assembleApkRelease" : env.gradleTask

    //收集路径
    def archivePath = env.archivePath == null ? ".apk/**,.apk/**/**" : env.archivePath

    stage('等待构建确认') {
        echo "30秒不确认自动超时"
        timeout(time: 30, unit: 'SECONDS') {
            input message: '是否进行构建?', ok: '构建'
        }
    }

    stage("清理目录") {
        deleteDir()

        echo "Result:${currentBuild.result}"
    }

    stage("拉取仓库代码") {
        git url: projectGitUrl, branch: projectBranchName

        echo "Result:${currentBuild.result}"
    }

    if (coreGitUrl != null) {
        stage("拉取核心代码") {

            //核心仓库名
            def coreName = corePath == null ? coreGitUrl.substring(coreGitUrl.lastIndexOf("/") + 1, coreGitUrl.lastIndexOf(".")) : corePath

            //切换到工作目录
            dir(env.WORKSPACE + "/" + coreName) {
                git url: coreGitUrl, branch: coreBranchName
            }

            echo "Result:${currentBuild.result}"
        }
    }

    if (pluginGitUrl != null) {
        stage("拉取插件代码") {

            //核心仓库名
            def pluginName = pluginPath == null ? pluginGitUrl.substring(pluginGitUrl.lastIndexOf("/") + 1, pluginGitUrl.lastIndexOf(".")) : pluginPath

            //切换到工作目录
            dir(env.WORKSPACE + "/" + pluginName) {
                git url: pluginGitUrl, branch: pluginBranchName
            }

            echo "Result:${currentBuild.result}"

            echo readFile([encoding: 'UTF-8', file: "wt.plugin/github/build.gradle"])
        }
    }

    stage("开始编译") {
        withGradle {
            //readFile encoding: 'UTF-8', file: 'local.properties'
            sh 'chmod +x ./gradlew'
            //sh "./gradlew build"
            sh "./gradlew ${gradleTask}"
        }

        echo "Result:${currentBuild.result}"
    }

    stage("上传apk") {
        withGradle {
            sh "./gradlew _qiniuUpload"
        }

        echo "Result:${currentBuild.result}"
    }

    stage("收集产物") {
        withGradle {
            archiveArtifacts allowEmptyArchive: true, artifacts: archivePath, onlyIfSuccessful: true
            archive archivePath
        }

        echo "Result:${currentBuild.result}"

        //mail bcc: '', body: 'test-body', cc: '', from: '', replyTo: '', subject: 'test-title', to: 'angcyo@126.com'
    }
}