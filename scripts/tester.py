import argparse
import hashlib
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

    def run(self, base_url, verbosity=0):
        print(Colors.BOLD + self.method + ' ' + self.endpoint)
        r = requests.request(self.method, base_url + self.endpoint,
                             params=self.params,
                             auth=self.auth,
                             headers=self.headers,
                             files=self.files,
                             data=self.data)

        if r.status_code == self.expected:
            print(Colors.OKGREEN + 'SUCCESS' + Colors.ENDC)
            
            if verbosity == 1:
                try:
                    pprint(r.json())
                except:
                    print(r.text)

            print()
            return 0
        else:
            print(Colors.FAIL + 'FAILURE' + Colors.ENDC)

            if verbosity == 1:
                print(r.text)

            print()
            return 1


# Runs all TestCase objects in the tests parameter
def run_all(tests, base_url, verbosity=0):
    failures = 0
   
    # Each run will return a 0 on success and a 1 on failure
    # Summing will get the number of failures
    for test in tests:
        failures += test.run(base_url, verbosity)

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
    parser.add_argument('-u', '--username', type=str, default='testy@gmail.com',
                        help='the Pilot username to fetch data for')
    parser.add_argument('-p', '--password', type=str, default='password',
                        help='the password of this user')
    parser.add_argument('-v', '--verbosity', type=int, default=0, choices={0, 1},
                        help='0 = only success/failure. 1 = show HTTP response')
    parser.add_argument('-a', '--auth', type=str, default='application:secret',
                        help='authentication credentials to connect to all endpoints')
    args = parser.parse_args()

    # Hash password
    m = hashlib.md5()
    m.update(args.password.encode('utf-8'))
    password = m.hexdigest()

    # Separate auth
    auth = (args.auth.split(':')[0], args.auth.split(':')[1])

    # Define test cases
    tests = [
        # Facebook
        TestCase('GET', '/facebook/oauthUrl', auth),
        TestCase('GET', '/facebook/users', auth,
                 params={'username': args.username},
                 headers={'password': password}),
        TestCase('GET', '/facebook/photos', auth,
                 params={'username': args.username},
                 headers={'password': password}),
        TestCase('GET', '/facebook/videos', auth,
                 params={'username': args.username},
                 headers={'password': password}),
        TestCase('GET', '/facebook/extendedToken', auth,
                 params={'username': args.username},
                 headers={'password': password}),
        TestCase('POST', '/facebook/publish', auth,
                 params={'username': args.username, 'type': 'photo'},
                 headers={'password': password},
                 files={'file': open('application/src/main/resources/logo.png', 'rb')},
                 data={'message': 'Lightning Logo', 'title': 'Logo'}),

        # Twitter
        TestCase('GET', '/twitter/oauthUrl', auth),
        TestCase('GET', '/twitter/users', auth, params={'username': args.username},
                 headers={'password': password}),
    ]

    # Run tests
    run_all(tests, args.endpoint, verbosity=args.verbosity)
