# Setup Instruction

### Maven Install all three sub-module

### SonarQube Setup

 1. Download & Extract SonarQube(Community Edition) : https://www.sonarqube.org/downloads/
 2. Start SonarQube(Go to $SONAR_HOME/bin/macosx-universal-64)
 3. ./sonar.sh start
 4. Go to http://localhost:9000
 5. Setup New Password for Admin

### Plugin Setup

 1. Go to Project Folder : [java-custom-rules-example](https://github.com/adhpawal/uvm-personal-data/tree/main/java-custom-rules-example
    "java-custom-rules-example")
 2. mvn clean install
 3. Copy target/java-custom-rules-example-1.0.0-SNAPSHOT.jar to $SONAR_HOME/extensions/plugins
 4. Restart SonarQube

### Project Scanning

 1. Login to SonarQube
 2. Go to Projects > Create New Project > Maven > Manual > File Explorer > Select https://github.com/adhpawal/uvm-personal-data/tree/main/pd-implementation
 3. Copy the generated code and run the maven deploy instruction
