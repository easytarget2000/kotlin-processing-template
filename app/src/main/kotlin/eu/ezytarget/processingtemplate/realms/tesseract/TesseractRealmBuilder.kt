package eu.ezytarget.processingtemplate.realms.tesseract

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object TesseractRealmBuilder : RealmBuilder {
    override fun build(): Realm = TesseractRealm()
}