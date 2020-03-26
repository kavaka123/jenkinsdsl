job('ant-build') {
    scm {
        github('tetradev01/demoapp', 'master')
    }

    steps {
        ant {
            prop('version', 'dev')
            buildFile('build.xml')
            antInstallation('local-ant')
        }
    }

    publishers {
        downstream 'ant-deploy', 'SUCCESS'
    }
}

job('ant-deploy') {
    description 'Deploy app to the demo application server'
    /*
     * configuring ssh plugin to run docker commands
     */
    steps{
       //      shell 'sshpass -p \'123456\' scp /var/lib/jenkins/workspace/antbuild/build/demoapp-dev.war release@10.12.108.11:/opt/tomcat/webapps/'
      batchFile('copy "C:\\Program Files (x86)\\Jenkins\\workspace\\ant-build\\build\\demoapp-dev.war" .')
      batchFile('docker rm -f %JOB_NAME% && docker run -d -p 8888:8080 --name %JOB_NAME% tomcat && docker cp demoapp-dev.war %JOB_NAME%:/usr/local/tomcat/webapps/')
      }
}


deliveryPipelineView('ant job delivery pipeline') {
    showAggregatedPipeline true
    enableManualTriggers true
    pipelineInstances 3
    pipelines {
        component('ant job delivery pipeline', 'ant-build')
    }
}
  
