package eu.ezytarget.processing_template.realms.scanner

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object ScannerRealmBuilder : RealmBuilder {

    override fun build(): Realm = ScannerRealm()

}