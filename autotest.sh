#!/bin/bash
# auto test class files and build

class=`tail -n 100 ~/MC/testserver/logs/latest.log | grep -ioE "java.lang.NoClassDefFoundError: (.+)$" | grep -ioE "com.+" | sed 's,\/,\\\/,g'`
echo $class

while [ -n "$class" ]; do
  sed -i "s/class<\/include><\/includes><\/filter><\/filters>/class<\/include>\n<include>$class.class<\/include><\/includes><\/filter><\/filters>/g" EssentialsGeoIP/pom.xml

  mvn install
  cp EssentialsGeoIP/target/EssentialsXGeoIP-2.0.1.jar ~/MC/testserver/plugins/
  bash -c "/home/kenneth/MC/testserver/run.sh"

  class=`tail -n 100 ~/MC/testserver/logs/latest.log | grep -ioE "java.lang.NoClassDefFoundError: (.+)$" | grep -ioE "com.+" | sed 's,\/,\\\/,g'`
  sleep 1
done
