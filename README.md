# Vert.x Mail Service

A simple worker service with event bus proxy to send emails. An email can be sent in the format:

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
