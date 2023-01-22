# Demo Spring AWS S3 DB ![CI](https://github.com/ivankaptue/demo-springboot-aws-s3-db/actions/workflows/ci.yml/badge.svg?branch=main)

We have stores that must upload sales report as csv file everyday in our storage space (AWS S3 bucket).
This service will take all the files of all stores every day and save sales in our local database.

###### Make it run

- `make start-db` to start postgresql in docker container and apply flyway migrations
- `mvn clean install` to build
- `mvn spring-boot:run` to run
