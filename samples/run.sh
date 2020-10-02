# Note: this bash script is only for CI/CD

# Call this script from main directory
#
# ./samples/run.sh <project>

PROJECT=$1

# Search for the build.gradle.kts file
if [ -f "./samples/$PROJECT/build.gradle.kts" ]; then
  echo "Running project '$PROJECT'"

  pushd "./samples/$PROJECT"
  bash ./gradlew run
  popd
else
  echo "Sample project '$PROJECT' not found."
  exit 1
fi