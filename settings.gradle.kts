plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "ordermanagement"

include("ordermanagement-domain")
include("ordermanagement-jdbc")
include("ordermanagement-springbootapi")
include("ordermanagement-redis")
