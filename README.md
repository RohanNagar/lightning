# Lightning
[![Build Status](https://travis-ci.org/RohanNagar/lightning.svg?branch=master)](https://travis-ci.org/RohanNagar/lightning)
![Version](https://img.shields.io/badge/version-v0.0.0-7f8c8d.svg)
[![Twitter](https://img.shields.io/badge/twitter-%40RohanNagar22-00aced.svg)](http://twitter.com/RohanNagar22)

Lightning is a real-time social media REST API, to be used in conjunction with [Thunder](https://github.com/RohanNagar/thunder). Lightning provides methods to request data from various 3rd party social media services, including Facebook and Twitter.

* [Running Locally](#running-locally)
* [Contributing](#contributing)
* [Testing](#testing)

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
You can run the following commands using [HTTPie](https://github.com/jkbrzt/httpie) to test each of the available endpoints. Simply replace the brackets with the appropriate information and run the command via the command line.

### Facebook
- `http -a {application}:{secret} GET localhost:9000/facebook/users?username={name}`
- `http -a {application}:{secret} GET localhost:9000/facebook/photos?username={name}`
- `http -a {application}:{secret} GET localhost:9000/facebook/videos?username={name}`
- `http -a {application}:{secret} GET localhost:9000/facebook/extendedToken?username={name}`
- `http -a {application}:{secret} GET localhost:9000/facebook/oauthUrl`
- `http -a {application}:{secret} -f POST "localhost:9000/facebook/publish?username={name}&type={type}" file@location/to/file message="Some message" title="Some title"`

### Twitter
- `http -a {application}:{secret} GET localhost:9000/twitter/users?username={name}`
- `http -a {application}:{secret} GET localhost:9000/twitter/oauthUrl`
