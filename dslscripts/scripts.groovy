
job('job-dsl-checkout') {
    
    scm {
        github('tetradev01/hello-world-war', 'master')
    }
      
   publishers {
        downstream 'job-dsl-compile', 'SUCCESS'
    }
    
}

mavenJob('job-dsl-compile'){
   
  customWorkspace('C:\\Program Files (x86)\\Jenkins\\workspace\\job-dsl-checkout')
  mavenInstallation('local-maven')
  maven {
    goals('compile')
    mavenOpts('-DproxySet=true -DproxyHost=www-proxy.us.oracle.com -DproxyPort=80')  
  }  
  publishers {
        downstream 'job-dsl-package', 'SUCCESS'
   }
}

mavenJob('job-dsl-package'){
    customWorkspace('C:\\Program Files (x86)\\Jenkins\\workspace\\job-dsl-checkout')
    mavenInstallation('local-maven')
    maven {
      goals('compile')
      mavenOpts('-DproxySet=true -DproxyHost=www-proxy.us.oracle.com -DproxyPort=80')  
    }
    
  publishers {
        downstream 'job-dsl-deploy', 'SUCCESS'
  }
}

job('job-dsl-deploy') {
    description 'Deploy app to the demo server'
    
    steps{
             shell 'sshpass -p "123456" scp /var/lib/jenkins/workspace/job-dsl-checkout/target/hello-world-war-1.0.0.war release@10.12.108.11:/opt/tomcat/webapps/'
      }
}

listView('List View DSLs') {
    jobs {
        regex('job-dsl-.+')
    }
    columns {
        status()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

deliveryPipelineView('job-dsl delivery pipeline') {
    showAggregatedPipeline true
    enableManualTriggers true
    pipelineInstances 5
    pipelines {
        component('job-dsl delivery pipeline', 'job-dsl-checkout')
    }
}
