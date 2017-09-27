import argparse
import hashlib
import json
import requests

from pprint import pprint


class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


class TestCase:
    def __init__(self, method, endpoint, auth, params=None, headers=None, files=None, data=None,
                 expected=requests.codes.ok):
        self.method = method
        self.endpoint = endpoint
        self.auth = auth
        self.params = params
        self.headers = headers
        self.files = files
        self.data = data
        self.expected = expected

    def run(self, base_url, verbose=False):
        print(Colors.BOLD + self.method + ' ' + self.endpoint)
        r = requests.request(self.method, base_url + self.endpoint,
                             params=self.params,
                             auth=self.auth,
                             headers=self.headers,
                             files=self.files,
                             data=self.data)

        if r.status_code == self.expected:
            print(Colors.OKGREEN + 'SUCCESS' + Colors.ENDC)
            
            if verbose:
                try:
                    pprint(r.json())
                except json.decoder.JSONDecodeError:
                    print(r.text)

            print()
            return 0
        else:
            print(Colors.FAIL + 'FAILURE' + Colors.ENDC)

            if verbose:
                print(r.text)

            print()
            return 1


# Runs all TestCase objects in the tests parameter
def run_all(tests, base_url, verbose=False):
    failures = 0
   
    # Each run will return a 0 on success and a 1 on failure
    # Summing will get the number of failures
    for test in tests:
        failures += test.run(base_url, verbose)

    print(Colors.BOLD + '-----------------------------------')
    if failures > 0:
        print(Colors.FAIL + '{} Failures'.format(failures) + Colors.ENDC)
    else:
        print(Colors.OKGREEN + '{}/{} Pass'.format(len(tests), len(tests)) + Colors.ENDC)


if __name__ == '__main__':
    parser = argparse.ArgumentParser('Test runner for all Lightning endpoints')

    # Add command line args
    parser.add_argument('-e', '--endpoint', type=str, default='http://localhost:9000',
                        help='the base endpoint to connect to')
    parser.add_argument('-m', '--email', type=str, default='Testy@gmail.com',
                        help='the email of the Pilot user to fetch data for')
    parser.add_argument('-p', '--password', type=str, default='password',
                        help='the password of this user')
    parser.add_argument('-a', '--auth', type=str, default='application:secret',
                        help='authentication credentials to connect to all endpoints')
    parser.add_argument('-v', '--verbose', action='store_true',
                        help='increase output verbosity')
    args = parser.parse_args()

    # Hash password
    m = hashlib.md5()
    m.update(args.password.encode('utf-8'))
    password = m.hexdigest()

    # Separate auth
    authentication = (args.auth.split(':')[0], args.auth.split(':')[1])

    # Define test cases
    all_tests = [
        # Facebook
        TestCase('GET', '/facebook/oauthUrl', authentication,
                 params={'redirect': 'sample://url'}),
        TestCase('GET', '/facebook/users', authentication,
                 params={'email': args.email},
                 headers={'password': password}),
        TestCase('GET', '/facebook/photos', authentication,
                 params={'email': args.email},
                 headers={'password': password}),
        TestCase('GET', '/facebook/videos', authentication,
                 params={'email': args.email},
                 headers={'password': password}),
        TestCase('GET', '/facebook/extendedToken', authentication,
                 params={'email': args.email},
                 headers={'password': password}),
        TestCase('POST', '/facebook/publish', authentication,
                 params={'email': args.email, 'type': 'photo', 'message': 'Lightning Logo'},
                 headers={'password': password},
                 files={'file': open('application/src/main/resources/logo.png', 'rb')},
                 data={'title': 'Logo'},
                 expected=requests.codes.created),
        TestCase('POST', '/facebook/publish', authentication,
                 params={'email': args.email, 'type': 'text', 'message': 'Hello World!'},
                 headers={'password': password},
                 files={'file': ''},
                 expected=requests.codes.created),

        # Twitter
        TestCase('GET', '/twitter/oauthUrl', authentication),
        TestCase('GET', '/twitter/users', authentication,
                 params={'email': args.email},
                 headers={'password': password}),
        TestCase('POST', '/twitter/publish', authentication,
                 params={'email': args.email, 'type': 'photo', 'message': 'Test Image'},
                 headers={'password': password},
                 files={'file': open('application/src/main/resources/logo.png', 'rb')},
                 expected=requests.codes.created),
        TestCase('POST', '/twitter/publish', authentication,
                 params={'email': args.email, 'type': 'text', 'message': 'Hello World!'},
                 headers={'password': password},
                 files={'file': ''},
                 expected=requests.codes.created),
]

    # Run tests
    run_all(all_tests, args.endpoint, verbose=args.verbose)
