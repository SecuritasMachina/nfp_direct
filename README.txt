./gradlew clean;./gradlew build;./gradlew deployROOT

ssh -i ~/Documents/ssh/id_rsa jaxtrx@ackdev.com

./gradlew copyForDemo bootWar deploy
./gradlew copyForContainerAuthorization bootWar deploy

cd /Users/jaxtrx/Documents/data/src/url-shortener
./gradlew clean;./gradlew bootwar;./gradlew deployROOT

rsync -avz -e "ssh -i ~/Documents/ssh/id_rsa" /Volumes/data/Documents/data/src/url-shortener/tomcat/webapps/nfp.war jaxtrx@ackdev.com:/tmp/4/webapps

rsync -avz -e "ssh -i ~/Documents/ssh/id_rsa" jaxtrx@ackdev.com:/tmp/4/nfp.sql /Volumes/data/Documents/data
mysql -u root -pTODO_PASSWORD nfp </Volumes/data/Documents/data/nfp.sql

#Backup:
rsync -ua -v --quiet --delete /Users/jaxtrx/Documents/data/src/url-shortener/ /Volumes/USB1/src

 ./gradlew clean;./gradlew bootwar;./gradlew deploy

#Client
cd /Volumes/data/Documents/data/src/url-shortener/client
ng serve

 ng build --prod --output-path /Volumes/data/Documents/data/src/url-shortener/src/main/resources/static --deploy-url /go/
 ng build --prod --output-path /Volumes/data/Documents/data/src/url-shortener/src/main/resources/static --deploy-url /internalgo/
 ng build --prod --output-path /Volumes/data/Documents/data/src/url-shortener/src/main/resources/static --deploy-url /

#Hard coded path:
ng build --prod --output-path /Users/jaxtrx/Documents/apache-tomcat-8.5.37/webapps/URL-Shortener-0.0.1-SNAPSHOT/WEB-INF/classes/static --deploy-url /URL-Shortener-0.0.1-SNAPSHOT/

ng build --prod --output-path /Volumes/data/Documents/apache-tomcat-8.5.37/webapps/go/WEB-INF/classes/static --deploy-url /go/
ng build --prod --output-path /Volumes/data/Documents/apache-tomcat-8.5.37/webapps/go/WEB-INF/classes/static --deploy-url /

