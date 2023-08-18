We have runner class which we can trigger from the pom.xml

```
mvn test-compile exec:java -Dexec.mainClass="runner.Runner" -Dexec.classpathScope="test" -Dexec.args="-p C:\config\"
```

-p defines the path where file needs to be downloaded and stored