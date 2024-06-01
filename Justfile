set windows-shell := ["powershell.exe", "-NoLogo", "-Command"]

build: format
  scala-cli --power compile --suppress-experimental-warning .

check-format:
  scalafmt . --check

format:
  scalafmt .

test:
  scala-cli --power test --suppress-experimental-warning .

run:
  scala-cli --power --suppress-experimental-warning .

run-native:
  scala-cli --power --suppress-experimental-warning --native .

package-native out="rokrok":
  scala-cli --power package --suppress-experimental-warning --native . -o target/{{out}}

publish:
  scala-cli --power publish .

clean:
  scala-cli clean .
