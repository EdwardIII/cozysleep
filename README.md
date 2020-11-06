# cozysleep: Sleep better knowing your sites are up

## Usage

**report** report on previously saved statuses. Outputs in Nagios format.
Example: 
  ```
  lein run report
  ```

**urls** Get the status of some urls from the cli and save them for later
Example: 
  ```
  lein run urls https://google.com https://google.fr
  ```

**cpanel-domains** Get the status of some urls from `/etc/userdomains` and save them for later
Example:
  ```
  lein run cpanel-domains
  ```
