def setupHaxe = { ->
	def script = '''
curl http://old.haxe.org/file/haxe-3.1.3-linux64.tar.gz | tar xz
export HAXE_HOME=$PWD/haxe-3.1.3
export HAXE_STD_PATH=$PWD/haxe-3.1.3/std
export PATH=$PATH:$HAXE_HOME
echo HAXE_HOME=$HAXE_HOME
haxe -version
'''
	def output = sh script: script, returnStdout: true
	def matcher = (output =~ /HAXE_HOME=(.*)/)
	haxeHome = matcher[0][1]
	return haxeHome
}

stage("flow") {
	node('boxfish-xenial-executor-small') {
		checkout scm
		ansiColor('xterm') {
			def haxeHome = setupHaxe()

			try {
				withEnv(["PATH+=$haxeHome"]) {
					if (env.BRANCH_NAME == "master") {
						sh "./gradlew version clean check install publish -Prelease --stacktrace"
					} else {
						sh "./gradlew assemble check"
					}
				}
			} finally {
				archiveArtifacts artifacts: 'spaghetti-haxe-support/build/reports/**', allowEmptyArchive: true
				archiveArtifacts artifacts: 'spaghetti-js-support/build/reports/**', allowEmptyArchive: true
				archiveArtifacts artifacts: 'spaghetti-core/build/reports/**', allowEmptyArchive: true
				archiveArtifacts artifacts: 'spaghetti-typescript-support/build/reports/**', allowEmptyArchive: true
				archiveArtifacts artifacts: 'gradle-spaghetti-typescript-plugin/build/reports/**', allowEmptyArchive: true
			}
		}
	}
}
