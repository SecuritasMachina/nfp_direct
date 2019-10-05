appName=nfp
cwd=$(pwd)
echo Rsyncing $appName Demo

cd $cwd
./gradlew clean
./gradlew -DORG_GRADLE_PROJECT_TOMCAT_DEST=$ORG_GRADLE_PROJECT_TOMCAT_DEST bootWar deployNFP
# cp $cwd/build/libs/
rsync -av -e "ssh -i $ssh_key_path" $ORG_GRADLE_PROJECT_TOMCAT_DEST/nfp.war jaxtrx@ackdev.com:/tmp/4/webapps
#rsync -avzz -e "ssh -i ~/Documents/ssh/id_rsa" jaxtrx@ackdev.com:/home/jaxtrx/*.sql /c/data
