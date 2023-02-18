# Store sales processing ![CI](https://github.com/ivankaptue/demo-springboot-aws-s3-db/actions/workflows/ci.yml/badge.svg?branch=main)

### Use case

We have stores that must upload sales report as csv file everyday in our storage space (AWS S3 bucket).
This service will take all the files of all stores every day and save sales in our local database.

Once the data is saved, it is exposed via a Rest API to the various consumers

###### Make it run in local

1. Update application.yml to set your local configurations
2. Run the commands below to start PostgreSQL in docker container, run flyway migrations scripts and then run the
   application
    - `make start-db` to start postgresql in docker container and apply flyway migrations
    - `mvn clean install` to build
    - `mvn spring-boot:run` to run
