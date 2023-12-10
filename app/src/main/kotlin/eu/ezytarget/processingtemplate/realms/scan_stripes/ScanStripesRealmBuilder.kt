package eu.ezytarget.processingtemplate.realms.scan_stripes

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder
import eu.ezytarget.processingtemplate.realms.scan_stripes.ScanStripesRealm

object ScanStripesRealmBuilder : RealmBuilder {
    override fun build(): Realm = ScanStripesRealm()
}