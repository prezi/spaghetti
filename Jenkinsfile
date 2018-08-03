def setupHaxe = { ->
	sh '''
curl http://old.haxe.org/file/haxe-3.1.3-linux64.tar.gz | tar xz
export HAXE_HOME=$PWD/haxe-3.1.3
export HAXE_STD_PATH=$PWD/haxe-3.1.3/std
export PATH=$PATH:$HAXE_HOME
haxe -version
'''
}

stage("flow") {
	node('boxfish-xenial-executor-small') {
		checkout scm
		ansiColor('xterm') {
			setupHaxe()

			try {
				if (env.BRANCH_NAME == "master") {
					sh "./gradlew version clean check install publish -Prelease --stacktrace"
				} else {
					sh "./gradlew assemble check"
				}

			} finally {
				archiveArtifacts artifacts: 'spaghetti-haxe-support/build/reports/**'
				archiveArtifacts artifacts: 'spaghetti-js-support/build/reports/**'
				archiveArtifacts artifacts: 'spaghetti-core/build/reports/**'
				archiveArtifacts artifacts: 'spaghetti-typescript-support/build/reports/**'
				archiveArtifacts artifacts: 'gradle-spaghetti-typescript-plugin/build/reports/**'
			}
		}
	}
}
