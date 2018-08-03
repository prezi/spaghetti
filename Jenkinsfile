stage("flow") {
	node('boxfish-xenial-executor-small') {
		checkout scm
		ansiColor('xterm') {
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