plugins {
    id("buildlogic.kotlin-library-conventions")
}

sourceSets {
    main {
        kotlin {
            srcDir(".")
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}