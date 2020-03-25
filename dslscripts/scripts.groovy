
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
  goals('compile')  
 // * Add mavne options manually to job under build step advanced button as the below code is not working *   
//  maven {
//    goals('compile')
//    mavenOpts('-DproxySet=true -DproxyHost=www-proxy.us.oracle.com -DproxyPort=80')  
//  }  
  publishers {
        downstream 'job-dsl-package', 'SUCCESS'
   }
}

mavenJob('job-dsl-package'){
    customWorkspace('C:\\Program Files (x86)\\Jenkins\\workspace\\job-dsl-checkout')
    mavenInstallation('local-maven')
    goals('package')
 // * Add mavne options manually to job under build step advanced button as the below code is not working *     
 //   maven {
 //     goals('package')
 //     mavenOpts('-DproxySet=true -DproxyHost=www-proxy.us.oracle.com -DproxyPort=80')  
 //   }
    
  publishers {
        downstream 'job-dsl-deploy', 'SUCCESS'
  }
}

job('job-dsl-deploy') {
    description 'Deploy app to docker container. view the output at curl -L http://localhost:8888/hello-world-war-1.0.0'
    
    steps{
        batchFile('copy "C:\\Program Files (x86)\\Jenkins\\workspace\\job-dsl-checkout\\target\\hello-world-war-1.0.0.war" .')
        batchFile('docker rm -f %JOB_NAME% && docker run -d -p 8888:8080 --name %JOB_NAME% tomcat && docker cp hello-world-war-1.0.0.war %JOB_NAME%:/usr/local/tomcat/webapps/')
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
