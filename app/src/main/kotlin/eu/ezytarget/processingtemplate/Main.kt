package eu.ezytarget.processingtemplate

fun main(args: Array<String>) {
    val processingNativeLibs = "/Applications/Processing.app/Contents/Java/core/library/macos-aarch64"
    val updatedPath = "$processingNativeLibs:${System.getProperty("java.library.path")}"
    System.setProperty("java.library.path", updatedPath)

    MainApplet().runSketch()
}
