# NOTE: Spring Boot (JDBC) connects directly to the PostgreSQL database.
# It CANNOT use Supabase API Keys (anon/service_role keys).
# You MUST use the "Database Password" you created when setting up the project.

# Example DB_URL for Supabase (Transaction/Session Pooler):
# Format: jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
# Host: aws-0-us-east-1.pooler.supabase.com (Find in Database -> Connection String -> JDBC)
# Port: 5432 or 6543 (Use 5432 for direct, 6543 for pooler)

# Load environment variables from .env file
if [ -f .env ]; then
  export $(cat .env | xargs)
else
  echo ".env file not found! Please create one with DB_URL, DB_USERNAME, and DB_PASSWORD."
  exit 1
fi

# Ensure we use Java 17
export JAVA_HOME="/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home"

# Run the application
mvn spring-boot:run
