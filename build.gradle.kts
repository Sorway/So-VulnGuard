plugins {
    id("java")
}

group = "fr.sorway.soguard"
version = "1.0-SNAPSHOT"

configurations.implementation.get().setCanBeResolved(true)

repositories {
    mavenCentral()
}

dependencies {
    //Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //Logging
    implementation("ch.qos.logback:logback-core:1.5.17")
    implementation("ch.qos.logback:logback-classic:1.5.17")

    //Discord
    implementation("net.dv8tion:JDA:5.3.2")

    //Gson
    implementation("com.google.code.gson:gson:2.12.1")

    //Dotenv
    implementation("io.github.cdimascio:dotenv-java:3.2.0")

    //Reflections
    implementation("org.reflections:reflections:0.10.2")

    //Database
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.2")
    implementation("org.redisson:redisson:3.45.0")

    //RSS Flux Reader
    implementation("com.apptasticsoftware:rssreader:3.9.2")
}

//Encoding UTF-8
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

tasks.register<Copy>("copyLibs") {
    from(configurations.implementation)
    into(project.rootDir.resolve("reports/libs/"))
}

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "fr.sorway.soguard.SoVulnGuard"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}

tasks.test {
    useJUnitPlatform()
}