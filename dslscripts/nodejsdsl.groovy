job('nodejs-dsl-checkout') {
    
    scm {
        github('kavaka123/nodejsapp', 'master')
    }
  
   publishers {
        downstream 'nodejs-dsl-install', 'SUCCESS'
    }
}

job('nodejs-dsl-install') {
	customWorkspace('C:\\Program Files (x86)\\Jenkins\\workspace\\nodejs-dsl-checkout')

	steps{
		batchFile('npm install')
	}

	publishers{
		downstream 'nodejs-dsl-test', 'SUCCESS'
	}
}

job('nodejs-dsl-test'){
	customWorkspace('C:\\Program Files (x86)\\Jenkins\\workspace\\nodejs-dsl-checkout')

	steps{
		batchFile('npm start && npm test && npm stop')
	}

	publishers{
		downstream 'nodejs-dsl-archive', 'SUCCESS'
	}
}


job('nodejs-dsl-archive'){
	configure { project ->
        project / buildWrappers / 'org.jvnet.hudson.plugins.SSHBuildWrapper' {
            siteName 'release@localhost:2202'
            postScript """        
            	tar -zcvf /var/archive/app.tar.gz /var/myapp/           
	      """
            }
	}

	publishers{
		downstream 'nodejs-dsl-deploy', 'SUCCESS'
	}
}

job('nodejs-dsl-deploy'){
	configure { project ->
        project / buildWrappers / 'org.jvnet.hudson.plugins.SSHBuildWrapper' {
            siteName 'release@localhost:2202'
            postScript """        
            	cd /var/myapp
            	git pull origin master
				"""
            }
	}
    }


deliveryPipelineView('nodejs app delivery pipeline') {
    showAggregatedPipeline true
    allowPipelineStart true
    enableManualTriggers true
    pipelineInstances 3
    pipelines {
        component('nodejs app delivery pipeline', 'nodejs-dsl-checkout')
    }
}
