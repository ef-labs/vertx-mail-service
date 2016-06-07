# Vert.x Mail Service

A simple worker service with event bus proxy to send emails. 

[![Build Status](http://img.shields.io/travis/ef-labs/vertx-mail-service.svg?maxAge=2592000&style=flat-square)](https://travis-ci.org/ef-labs/vertx-mail-service)
[![Maven Central](https://img.shields.io/maven-central/v/com.englishtown.vertx/vertx-mail-service.svg?maxAge=2592000&style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.englishtown.vertx/vertx-mail-service/)

### Usage

An email can be sent in the format:

```
{
    "from": "from.email@address",
    "to": ["toaddress1@something.com", "toaddress2@something.com"],
    "cc": ["ccaddress1@something.com", "ccaddress2@something.com"],
    "bcc": ["bccaddress1@something.com", "bccaddress2@something.com"],
    "subject": "This is a subject",
    "body": "This is a body",
    "contentType": "text/plain"
}
```
