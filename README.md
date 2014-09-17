#vertx-mod-mailer
A simple worker module that accepts event bus messages to send emails. An email can be sent in the format:

```
{
    "from": "from.email@address",
    "to": ["toaddress1@something.com", "toaddress2@something.com"],
    "cc": ["ccaddress1@something.com", "ccaddress2@something.com"],
    "bcc": "bccaddress@something.com",
    "subject": "This is a subject",
    "body": "This is a body",
    "content_type": "text/plain"
}
```

The `to`, `cc` and `bcc` addresses can be either a single string, containing one address, or a JsonArray of many addresses. The `content_type` has to be valid type, such as `text/plain` or `text/html`.
