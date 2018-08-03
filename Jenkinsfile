stage("flow") {
	node('boxfish-xenial-executor-small') {
		checkout scm
		ansiColor('xterm') {
			sh "./gradlew assemble"
			sh "./gradlew check"
		}
	}
}