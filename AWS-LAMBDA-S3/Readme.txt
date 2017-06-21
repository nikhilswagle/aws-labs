Objective : Send email alerts to the subscribers of the "s3-load-event-topic" whenever a file is uploaded in a designated S3 bucket.

Implementation
SNS
1) Create a new SNS topic "s3-load-event-topic".
2) Create an email subscription to the topic. Which will send an email to your email address with a token url to confirm subscrption.
3) Click on the link to confirm subscription.

Lambda (Java 8)
1) Create a lambda function to take S3Event as the input.
2) Use SNS client to publish the message to SNS topic. (I actually have another generic lambda function to publish messages to the SNS topic, but you can integrate it in the same call.)
3) Add a new trigger of type S3 to lambda function. Here you can specify the bucket and the type of event for that bucket you want the lambda function to be triggered.

S3
1) Create S3 bucket e.g. data-load-20170621. Keep the bucket as private we do not need to make is accessible via internet. 
2) In the bucket properties go to Events section and click Add Notification.
3) You can directly hook in a SNS Topic here and start getting notifications. But we want to introduce Lambda instead so that we have option to modify the data as well as look and feel of the email that goes out.
4) Select "put" as the type of event. You may select prefix and sufix if you want to limit the notifications to uploads with specific file names.
5) Save the event.

Now every time a file is uploaded to the above S3 bucket, lambda function will be triggered. Lamdba will publish a message (preferably having bucket name and file name) to SNS topic. Thus an email will be delivered to all the subscribers of the topic.

