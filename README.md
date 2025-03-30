# Alumni Portal
A Spring Boot REST API that searches LinkedIn alumni profiles using PhantomBuster and stores the results in PostgreSQL.

### Technical Specifications
* Springboot project - 3.4.4
* Java Version: 17
* Build Tool: Gradle
* Database: PostgreSQL

## Prerequisites
- Java Development Kit (JDK) 17 or later installed
- Git installed

## Installation

### 1. Clone the Repository
Clone this repository to your local machine using Git:

```bash 
git clone https://github.com/headshot07/alumni-portal.git

cd alumni-portal
```

### 2. Install Java Development Kit (JDK) 17 (Corretto)

#### 2.1. Download & Install Corretto 17

Download the appropriate JDK installer for your operating system from the Amazon Corretto website:
https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html

Follow the installation instructions provided by Amazon to install Corretto 17 on your system:
https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/macos-install.html

#### 2.2. Verify Installation
To verify that Corretto 17 is installed correctly, open a terminal and run the following command:
```java -version```

You should see output similar to the following:
```
openjdk version "17.0.10" 2024-01-16 LTS
OpenJDK Runtime Environment Corretto-17.0.10.7.1 (build 17.0.10+7-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.10.7.1 (build 17.0.10+7-LTS, mixed mode, sharing)
```

### 3. Database Setup
Using Docker:
```bash
docker compose up -d
```

This creates a PostgreSQL container with:

```
Database: alumni_portal
Username: guest
Password: guest
JDBC URL: jdbc:postgresql://localhost:5432/alumni_portal
```

To stop the Docker containers and clean up associated volumes, use:
```bash
docker compose down -v
```

### 4. Database Schema Creation

After setting up the PostgreSQL database using Docker, follow these steps to create the required tables:

#### 4.1 Copy schema file
Copy the schemas.sql file into the PostgreSQL container
```bash 
docker cp schemas.sql postgres-master:/schemas.sql
```

#### 4.2 Run schema file
Run the schemas.sql file to create tables
```bash
docker exec -it postgres-master psql -U guest -d alumni_portal -f /schemas.sql
```
#### 4.3 Verify Tables
To connect to the PostgreSQL database inside the Docker container:

```bash
docker exec -it postgres-master psql -U guest -d alumni_portal
````
#### List all tables
```bash
\dt
```

### 5. Run the Python Mock Server
The Python server (phantom_buster_server.py) is used to mock the PhantomBuster API.
Before running the Spring Boot application, you need to run the Python mock server.

#### 5.1 Run the server

```bash
python3 phantom_buster_server.py
```
This will start the server at: `http://localhost:7009`

#### 5.2 Validate the Python Server
To ensure the mock server is running correctly, use the following cURL command:

```bash
curl --location 'http://localhost:7009/api/alumni/search' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer abc' \
--data '{
    "university": "University of XYZ",
    "designation": "Software Engineer",
    "passoutYear": "2020"
}'
```

If the server is running correctly, you should receive a response like:
```
{
    "status": "success",
    "data": [
        {
            "name": "John Doe",
            "currentRole": "Software Engineer",
            "university": "University of XYZ",
            "location": "San Francisco, CA",
            "linkedinHeadline": "Software Engineer at Google",
            "passoutYear": 2020
        },
        {
            "name": "Jane Smith",
            "currentRole": "Data Scientist",
            "university": "University of XYZ",
            "location": "New York, NY",
            "linkedinHeadline": "Data Scientist | AI Enthusiast",
            "passoutYear": 2019
        }
    ]
}
```

### 6 API Documentation

#### 6.1 Search Alumni from
Endpoint: POST `/api/alumni/search`

Saves and retrieves LinkedIn alumni profiles based on search parameters.

**cURL Command:**
```bash
curl --location 'http://localhost:8080/api/alumni/search' \
--header 'Content-Type: application/json' \
--data '{
    "university": "University of XYZ",
    "designation": "Software Engineer",
    "passoutYear": "2020"
}'

```

#### 6.2 Get Saved Alumni Profiles
Endpoint: GET `/api/alumni/all`

Retrieves previously saved alumni profiles.

**cURL Command:**
```bash
curl --location 'http://localhost:8080/api/alumni/all'
```

### 7. Build and Run the Project
Navigate to the project directory and build the project using Gradle:
```bash
./gradlew clean build
```

After building the project, you can run the application using Gradle:
```bash
./gradlew bootRun
```

### The application will be accessible at:
http://localhost:8080


### 8. Running test cases
To run the unit tests, use the following command:

```bash
./gradlew test
```
