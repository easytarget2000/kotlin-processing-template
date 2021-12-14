package eu.ezytarget.processing_template.realms.tesseract

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object TesseractRealmBuilder : RealmBuilder {
    override fun build(): Realm = TesseractRealm()
}