git pull

chmod +x gradlew
sudo ./gradlew bootJar

kill $(cat ~/boot/pid.file)
nohup java -jar ./build/libs/barofish-server-0.0.1-SNAPSHOT.jar > ~/boot/log.txt 2>&1 &
echo $! > ~/boot/pid.file