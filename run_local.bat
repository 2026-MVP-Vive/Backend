@echo off

set S3_BUCKET_NAME=seoul-study-591
set DB_USERNAME=root
set DB_PASSWORD=1234
echo AWS_ACCESS_KEY=%AWS_ACCESS_KEY%
echo AWS_SECRET_KEY=%AWS_SECRET_KEY%
echo S3_BUCKET_NAME=%S3_BUCKET_NAME%
call gradlew clean bootRun --no-daemon --console=plain
