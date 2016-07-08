<img align="left" src="application/src/main/resources/logo.png">

# Lightning
[![Build Status](https://travis-ci.org/RohanNagar/lightning.svg?branch=master)](https://travis-ci.org/RohanNagar/lightning)
[![Coverage Status](https://coveralls.io/repos/github/RohanNagar/lightning/badge.svg?branch=master)](https://coveralls.io/github/RohanNagar/lightning?branch=master)
[![Version](https://img.shields.io/badge/version-v0.1.0-7f8c8d.svg)](https://github.com/RohanNagar/lightning/releases)
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
There is a Python testing script available in the `scripts` directory. To run this script, make sure you are in the base lightning directory and run the following command.

```bash
$ python scripts/tester.py
```

There are multiple optional command line arguments for the testing script. These are described in the table below, along with their default values. Additionally, when running the script from the command line, adding the `-h` option will display a help message with all optional arguments.

|        Flag        |                                                  Description                                                  |      Default Value      |
|:------------------:|:-------------------------------------------------------------------------------------------------------------:|:-----------------------:|
|    `-h` `--help`   |                                            Display a help message.                                            |           ----          |
|  `-e` `--endpoint` |                                   The endpoint to connect to lightning with.                                  | `http://localhost:9000` |
|  `-u` `--username` |                                       The username to make requests for.                                      |         `Testy`         |
|  `-p` `--password` |              The password of the user that matches the username. Should not be a hashed password.             |        `password`       |
|    `-a` `--auth`   |                   The basic authentication credentials in the form `{app_name}:{app_secret}`                  |    `lightning:secret`   |
| `-v` `--verbosity` | Use a value of `0` to only show the success or failure message. Use a value of `1` to additionally show the JSON response. |           `0`           |

When adding new endpoints to lightning, be sure to include a new `TestCase` for that endpoint in the `tester.py` script.

Another testing option is to run the following commands using [HTTPie](https://github.com/jkbrzt/httpie). Simply replace the brackets with the appropriate information and run the command via the command line.

### Facebook
- `http -a {application}:{secret} GET localhost:9000/facebook/users?username={name} password:{password}`
- `http -a {application}:{secret} GET localhost:9000/facebook/photos?username={name} password:{password}`
- `http -a {application}:{secret} GET localhost:9000/facebook/videos?username={name} password:{password}`
- `http -a {application}:{secret} GET localhost:9000/facebook/extendedToken?username={name} password:{password}`
- `http -a {application}:{secret} GET localhost:9000/facebook/oauthUrl`
- `http -a {application}:{secret} -f POST "localhost:9000/facebook/publish?username={name}&type={type}" file@path/to/file message="Some Message" title="Some Title" password:{password}`

### Twitter
- `http -a {application}:{secret} GET localhost:9000/twitter/users?username={name} password:{password}`
- `http -a {application}:{secret} GET localhost:9000/twitter/oauthUrl`
