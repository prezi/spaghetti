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

def setupTypescript = { ->
	def script = '''
npm install typescript@3.2.4
export TS_BIN=$PWD/node_modules/typescript/bin
export PATH=$PATH:$TS_BIN
echo TS_BIN=$TS_BIN
tsc --version
'''
	def output = sh script: script, returnStdout: true
	def matcher = (output =~ /TS_BIN=(.*)/)
	tsBin = matcher[0][1]
	return tsBin
}

stage("flow") {
	node('boxfish-xenial-executor-small') {
		checkout scm
		ansiColor('xterm') {
			def haxeHome = setupHaxe()
			def tsBin = setupTypescript()

			try {
				withEnv(["PATH+=$tsBin", "PATH+=$haxeHome", "HAXE_STD_PATH=$haxeHome/std"]) {
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
