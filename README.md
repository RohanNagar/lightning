# Lightning
Lightning is a real-time social media REST API. Lightning provides methods to request data from various 3rd party social media services, including Facebook and Twitter.

* [Endpoints](#endpoints)
* [Running Locally](#running-locally)
* [Contributing](#contributing)

## Endpoints
* Facebook
 * GET /facebook/newsFeed?username=Testy

   ```json
    [
      {
        "username" : "Testy"
      }
    ]
   ```

* Twitter

## Running Locally
- Requirements
  - Java 1.8
  - Maven 3.3.3

First, fork this repo on GitHub. Then, clone your forked repo onto your machine.

```bash
$ git clone YOUR-FORK-URL
```

Compile and package the source code with Maven.

```bash
$ mvn package
```

Run the packaged jar with the server argument.

```bash
$ java -jar application/target/application-*.jar server
```

Lightning should now be running on localhost port 9000.

## Contributing
Make changes to your local repository and push them up to your fork on GitHub.
Submit a pull request to this repo with your changes as a single commit.
Your changes will be reviewed and merged when appropriate.

## Testing
You can run the following commands using [HTTPie](https://github.com/jkbrzt/httpie) to test each of the available endpoints.
Simply replace the brackets with the appropriate information and run the command.

* Facebook
 * `http -a {application}:{secret} GET localhost:9000/facebook/users?username={name}`
 * `http -a {application}:{secret} GET localhost:9000/facebook/photos?username={name}`
 * `http -a {application}:{secret} GET localhost:9000/facebook/videos?username={name}`
 * `http -a {application}:{secret} GET localhost:9000/facebook/extendedToken?username={name}`
 * `http -a {application}:{secret} GET localhost:9000/facebook/oauthUrl`
 * `http -a {application}:{secret} -f POST "localhost:9000/facebook/publish?username={name}&type={type}" file@location/to/file message="Some message" title="Some title"`

* Twitter
 * `http -a {application}:{secret} GET localhost:9000/twitter/users?username={name}`
 * `http -a {application}:{secret} GET localhost:9000/twitter/oauthUrl`
