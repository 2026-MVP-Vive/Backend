@echo off
set AWS_ACCESS_KEY=dummy-access-key
set AWS_SECRET_KEY=dummy-secret-key
set S3_BUCKET_NAME=dummy-bucket
set DB_USERNAME=root
set DB_PASSWORD=1234
echo AWS_ACCESS_KEY=%AWS_ACCESS_KEY%
echo AWS_SECRET_KEY=%AWS_SECRET_KEY%
echo S3_BUCKET_NAME=%S3_BUCKET_NAME%
call gradlew clean bootRun --no-daemon --console=plain
