# Transformation-Engine-POC

This is a RESTful endpoint to convert the Medication Fhir model from STU3 to R4 and R4 to STU3.
This project is based on HAPI project which is an open-source library that allows us to have all the HL7 capabilities in our projects.


### Request

`POST /$convert/`

To convert FHIR 

## Development

### Requirements
* Java 11
* Docker

### Install

Create docker container
```
docker build -t transformationengine . 
```

Run docker container locally
```
docker run -d -p8080:8080 transformationengine 
```

### Testing
Use the postman collection inside the project.
