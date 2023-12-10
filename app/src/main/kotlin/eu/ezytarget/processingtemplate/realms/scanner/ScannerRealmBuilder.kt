package eu.ezytarget.processingtemplate.realms.scanner

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object ScannerRealmBuilder : RealmBuilder {

    override fun build(): Realm = ScannerRealm()

}