# Docker Application Setup Guide

## Prerequisites

### Docker Installation

1. If you don't have Docker installed, download and install Docker from [Docker's official website](https://www.docker.com/get-started).

2. Verify Docker installation by running the following commands in your terminal:
```bash
docker --version
docker-compose --version
```
If both commands return version information, Docker is successfully installed.

### Environment Setup

1. Create a `.env` file in the root directory if it doesn't exist:

Unix/Linux/Mac
```bash
touch .env
```
Windows CMD
```cmd
type nul > .env
```
Windows Powershell
```cmd
New-Item -Path . -Name ".env" -ItemType "file"
```

2. Add the following environment variables to your `.env` file:
```
AWS_ACCESS_KEY_ID={YOUR_AWS_ACCESS_KEY}
AWS_SECRET_ACCESS_KEY={YOUR_SECRET_KEY}
AWS_REGION=ap-south-1
```
**Note: Replace the placeholder values with your actual AWS credentials.**

## Running the Application

### Local Development
To run the application in development environment:
```bash
docker-compose -f docker-compose.dev.yml up
```

### UAT Environment
To run the application in UAT environment:
```bash
docker-compose -f docker-compose.uat.yml up
```

## Important Notes
- Ensure all environment variables are properly set before running the application
- Keep your AWS credentials secure and never commit them to version control
- The default region is set to ap-south-1, but you can modify it according to your requirements

## Rebuilding image and running the Application

### Local Development
To run the application in development environment:
```bash
docker-compose -f docker-compose.dev.yml up --build
```

### UAT Environment
To run the application in UAT environment:
```bash
docker-compose -f docker-compose.uat.yml up --build
```


### Swagger | Open API Document URL
To run the application in UAT environment:
```link
http://localhost:8086/upload-service/v1/swagger-ui.html
```
Note: Replace domain to visit remote APIs.